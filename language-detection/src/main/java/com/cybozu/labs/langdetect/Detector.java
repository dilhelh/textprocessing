package com.cybozu.labs.langdetect;

/*
 * Copyright (C) 2016 Konstantin Gusarov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.cybozu.labs.langdetect.util.NGram;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringEscapeUtils.escapeJava;

/**
 * {@link com.cybozu.labs.langdetect.Detector} class is to detect language from specified text.
 * Its instance is able to be constructed via the factory class {@link DetectorFactory}.
 * <p/>
 * After appending a target text to the {@link com.cybozu.labs.langdetect.Detector} instance with {@link #append(java.io.Reader)} or {@link #append(String)},
 * the detector provides the language detection results for target text via {@link #detect()} or {@link #getProbabilities()}.
 * {@link #detect()} method returns a single language name which has the highest probability.
 * {@link #getProbabilities()} methods returns a list of multiple languages and their probabilities.
 * <p/>
 * The detector has some parameters for language detection.
 * See {@link #setAlpha(double)}, {@link #setMaxTextLength(int)} and {@link #setPriorMap(java.util.Map)}.
 * <p/>
 * <pre>
 * import java.util.List;
 * import com.cybozu.labs.langdetect.Detector;
 * import com.cybozu.labs.langdetect.DetectorFactory;
 * import com.cybozu.labs.langdetect.Language;
 *
 * class LangDetectSample {
 *     public String detect(String text) throws LangDetectException {
 *         Detector detector = DetectorFactory.create();
 *         detector.append(text);
 *         return detector.detect();
 *     }
 *
 *     public List<Language> detectLangs(String text) throws LangDetectException {
 *         Detector detector = DetectorFactory.create();
 *         detector.append(text);
 *         return detector.getProbabilities();
 *     }
 * }
 * </pre>
 * <p/>
 * <ul>
 * <li>4x faster improvement based on Elmer Garduno's code. Thanks!</li>
 * </ul>
 *
 * @author Nakatani Shuyo
 * @author Konstantin Gusarov
 * @see DetectorFactory
 */
public class Detector {
    private static final double DEFAULT_ALHPA = 0.5;
    private static final double ALPHA_WIDTH = 0.05;

    private static final int TRIAL_COUNT = 7;
    private static final int ITERATION_LIMIT = 1000;
    private static final double PROB_THRESHOLD = 0.1;
    private static final double CONV_THRESHOLD = 0.99999;
    private static final int BASE_FREQ = 10000;
    private static final String UNKNOWN_LANG = "unknown";

    private static final Pattern URL_REGEX = Pattern.compile("https?://[-_.?&~;+=/#0-9A-Za-z]{1,2076}");
    private static final Pattern MAIL_REGEX = Pattern.compile("[-_.0-9A-Za-z]{1,64}@[-_0-9A-Za-z]{1,255}[-_.0-9A-Za-z]{1,255}");
    private static final int DEFAULT_MAX_TEXT_LENGTH = 10000;

    private final Map<String, double[]> languageProbabilityMap;
    private final List<String> languageList;

    private final StringBuilder text = new StringBuilder();
    private double[] langprob;

    private double alpha = DEFAULT_ALHPA;
    private int maxTextLength = DEFAULT_MAX_TEXT_LENGTH;
    private double[] priorMap;
    private boolean verbose;

    /**
     * Detector instance can be constructed via {@link DetectorFactory#create()}.
     *
     * @param factory {@link DetectorFactory} instance (only DetectorFactory inside)
     */
    Detector(final DetectorFactory factory) {
        languageProbabilityMap = factory.languageProbabilityMap;
        languageList = factory.languages;
    }

    /**
     * Set Verbose Mode(use for debug).
     */
    public void setVerbose() {
        verbose = true;
    }

    /**
     * Set smoothing parameter.
     * The default value is 0.5(i.e. Expected Likelihood Estimate).
     *
     * @param alpha the smoothing parameter
     */
    public void setAlpha(final double alpha) {
        this.alpha = alpha;
    }

    /**
     * Set prior information about language probabilities.
     *
     * @param priorMap the priorMap to set
     * @throws LangDetectException
     */
    public void setPriorMap(final Map<String, Double> priorMap) throws LangDetectException {
        this.priorMap = new double[languageList.size()];
        double sump = 0;
        for (int i = 0; i < this.priorMap.length; ++i) {
            final String lang = languageList.get(i);
            if (priorMap.containsKey(lang)) {
                final double p = priorMap.get(lang);
                if (p < 0)
                    throw new LangDetectException(ErrorCode.INIT_PARAM, "Prior probability must be non-negative.");
                this.priorMap[i] = p;
                sump += p;
            }
        }
        if (sump <= 0)
            throw new LangDetectException(ErrorCode.INIT_PARAM, "More one of prior probability must be non-zero.");
        for (int i = 0; i < this.priorMap.length; ++i) this.priorMap[i] /= sump;
    }

    /**
     * Specify max size of target text to use for language detection.
     * The default value is 10000(10KB).
     *
     * @param maxTextLength         The maxTextLength to set
     */
    public void setMaxTextLength(final int maxTextLength) {
        this.maxTextLength = maxTextLength;
    }

    /**
     * Append the target text for language detection.
     * If the total size of target text exceeds the limit size specified by {@link com.cybozu.labs.langdetect.Detector#setMaxTextLength(int)},
     * the rest is cut down.
     *
     * @param text                  The target text to append
     */
    public void append(String text) {
        text = URL_REGEX.matcher(text).replaceAll(" ");
        text = MAIL_REGEX.matcher(text).replaceAll(" ");
        text = NGram.normalizeVietnamese(text);
        char pre = 0;
        for (int i = 0; i < text.length() && i < maxTextLength; ++i) {
            final char c = text.charAt(i);
            if (c != ' ' || pre != ' ') this.text.append(c);
            pre = c;
        }
    }

    /**
     * Cleaning text to detect
     * (eliminate URL, e-mail address and Latin sentence if it is not written in Latin alphabet)
     */
    /*private void cleaningText() {
        int latinCount = 0;
        int nonLatinCount = 0;

        for (int i = 0; i < text.length(); ++i) {
            final char c = text.charAt(i);
            if (c <= 'z' && c >= 'A') {
                ++latinCount;
            } else if (c >= '\u0300' && !Objects.equals(UnicodeBlock.of(c), UnicodeBlock.LATIN_EXTENDED_ADDITIONAL)) {
                ++nonLatinCount;
            }
        }
        if (latinCount * 2 < nonLatinCount) {
            final StringBuilder textWithoutLatin = new StringBuilder();
            for (int i = 0; i < text.length(); ++i) {
                final char c = text.charAt(i);
                if (c > 'z' || c < 'A') textWithoutLatin.append(c);
            }
            text = textWithoutLatin;
        }

    }*/

    /**
     * Detect language of the target text and return the language name which has the highest probability.
     *
     * @return detected language name which has most probability.
     * @throws LangDetectException code = ErrorCode.CANNOT_DETECT : Can't detect because of no valid features in text
     */
    public String detect() throws LangDetectException {
        final List<Language> probabilities = getProbabilities();
        if (!probabilities.isEmpty()) return probabilities.get(0).getLanguage();
        return UNKNOWN_LANG;
    }

    /**
     * Get language candidates which have high probabilities
     *
     * @return possible languages list (whose probabilities are over PROB_THRESHOLD, ordered by probabilities descendently)
     * @throws LangDetectException code = ErrorCode.CANNOT_DETECT : Can't detect because of no valid features in text
     */
    public List<Language> getProbabilities() throws LangDetectException {
        if (langprob == null) {
            detectBlock();
        }

        return sortProbability(langprob);
    }
    
    private void detectBlock() throws LangDetectException {
        //cleaningText();
        final List<String> ngrams = extractNGrams();
        if (ngrams.isEmpty())
            throw new LangDetectException(ErrorCode.CANNOT_DETECT, "no features in text");

        langprob = new double[languageList.size()];

        final Random rand = new Random();
        for (int t = 0; t < TRIAL_COUNT; ++t) {
            final double[] prob = initProbability();
            final double alpha = this.alpha + rand.nextGaussian() * ALPHA_WIDTH;

            for (int i = 0; ; ++i) {
                final int r = rand.nextInt(ngrams.size());
                updateLangProb(prob, ngrams.get(r), alpha);
                if (i % 5 == 0) {
                    if (normalizeProb(prob) > CONV_THRESHOLD || i >= ITERATION_LIMIT) break;
                    if (verbose) System.out.println("> " + sortProbability(prob));
                }
            }
            for (int j = 0; j < langprob.length; ++j) langprob[j] += prob[j] / TRIAL_COUNT;
            if (verbose) System.out.println("==> " + sortProbability(prob));
        }
    }

    private double[] initProbability() {
        final double[] prob = new double[languageList.size()];
        if (priorMap != null) {
            for (int i = 0; i < prob.length; ++i) prob[i] = priorMap[i];
        } else {
            for (int i = 0; i < prob.length; ++i) prob[i] = 1.0 / languageList.size();
        }
        return prob;
    }

    private List<String> extractNGrams() {
        final List<String> list = Lists.newArrayList();
        final NGram ngram = new NGram();
        for (int i = 0; i < text.length(); ++i) {
            ngram.addChar(text.charAt(i));
            for (int n = 1; n <= NGram.MAX_NGRAM_LENGTH; ++n) {
                final String w = ngram.get(n);
                if (w != null && languageProbabilityMap.containsKey(w)) list.add(w);
            }
        }
        return list;
    }

    private boolean updateLangProb(final double[] prob, final String word, final double alpha) {
        if (word == null || !languageProbabilityMap.containsKey(word)) return false;

        final double[] langProbMap = languageProbabilityMap.get(word);
        if (verbose) {
            System.out.println(word + "(" + escapeJava(word) + "):" + wordProbToString(langProbMap));
        }

        final double weight = alpha / BASE_FREQ;
        for (int i = 0; i < prob.length; ++i) {
            prob[i] *= weight + langProbMap[i];
        }
        return true;
    }

    private String wordProbToString(final double[] prob) {
        final Formatter formatter = new Formatter();
        for (int j = 0; j < prob.length; ++j) {
            final double p = prob[j];
            if (p >= 0.00001) {
                formatter.format(" %s:%.5f", languageList.get(j), p);
            }
        }
        final String string = formatter.toString();
        formatter.close();
        return string;
    }

    private static double normalizeProb(final double[] prob) {
        double maxp = 0;
        double sump = 0;
        for (int i = 0; i < prob.length; ++i) sump += prob[i];
        for (int i = 0; i < prob.length; ++i) {
            final double p = prob[i] / sump;
            if (maxp < p) maxp = p;
            prob[i] = p;
        }
        return maxp;
    }

    private List<Language> sortProbability(final double[] prob) {
        final List<Language> list = Lists.newArrayList();
        for (int j = 0; j < prob.length; ++j) {
            final double p = prob[j];
            if (p > PROB_THRESHOLD) {
                for (int i = 0; i <= list.size(); ++i) {
                    if (i == list.size() || list.get(i).getProbability() < p) {
                        list.add(i, new Language(languageList.get(j), p));
                        break;
                    }
                }
            }
        }
        return list;
    }
}
