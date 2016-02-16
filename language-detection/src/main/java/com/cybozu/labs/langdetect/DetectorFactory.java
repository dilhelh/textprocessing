package com.cybozu.labs.langdetect;

/*
 * Copyright (C) 2010-2014 Cybozu Labs, 2016 Konstantin Gusarov
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

import com.cybozu.labs.langdetect.util.LangProfile;
import com.cybozu.labs.langdetect.util.NGram;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.kgusarov.textprocessing.langdetect.LangProfileDocument;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;

/**
 * Language Detector Factory Class
 * <p/>
 * This class manages an initialization and constructions of {@link Detector}.
 * <p/>
 * When the language detection,
 * construct Detector instance via {@link DetectorFactory#create()}.
 * See also {@link Detector}'s sample code.
 * <p/>
 * <ul>
 * <li>4x faster improvement based on Elmer Garduno's code. Thanks!</li>
 * </ul>
 *
 * @author Nakatani Shuyo
 * @author Konstantin Gusarov
 * @see Detector
 */
@SuppressWarnings("unchecked")
public class DetectorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DetectorFactory.class);
    private static final Pattern SHORT_MESSAGE_RESOURCES = Pattern.compile("^sm/(.*)\\.json$");
    private static final Pattern LONG_MESSAGE_RESOURCES = Pattern.compile("^nr/(.*)\\.json$");
    private static final Pattern JSON_MATCHER = Pattern.compile("^(.*)\\.json$");

    final Map<String, double[]> languageProbabilityMap = Maps.newHashMap();
    final List<String> languages = Lists.newArrayList();

    /**
     * Create new {@code DetectorFactory}
     *
     * @param shortMessages             Should this detector factory use short message profiles
     * @throws LangDetectException
     */
    public DetectorFactory(final boolean shortMessages) {
        final Pattern resourceFilter = shortMessages ? SHORT_MESSAGE_RESOURCES : LONG_MESSAGE_RESOURCES;
        final Reflections reflections = new Reflections(null, new ResourcesScanner());
        final List<String> resources = reflections.getResources(JSON_MATCHER)
                .stream()
                .filter(s -> resourceFilter.matcher(s).matches())
                .collect(Collectors.toList());

        final int languageCount = resources.size();
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final ObjectMapper mapper = new ObjectMapper();

        for (int i = 0; i < languageCount; i++) {
            final String profile = resources.get(i);

            try (final InputStream is = cl.getResourceAsStream(profile)) {
                final LangProfileDocument lpd = mapper.readValue(is, LangProfileDocument.class);
                final LangProfile langProfile = new LangProfile(lpd);
                addProfile(langProfile, i, languageCount);
            } catch (final IOException e) {
                throw new LangDetectException(ErrorCode.FAILED_TO_INITIALIZE, "Failed to read language profile", e);
            }
        }
    }

    @VisibleForTesting
    DetectorFactory() {
        // Used only for tests
    }

    /**
     * Merge information from language profile instance into this factory
     *
     * @param profile                   Language profile to be merged
     * @param languageCount             Total amount of language profiles
     * @param index                     Index of language profile being added
     * @throws LangDetectException
     */
    @VisibleForTesting
    void addProfile(final LangProfile profile, final int index, final int languageCount) {
        final String language = profile.getName();
        if (languages.contains(language)) {
            throw new LangDetectException(ErrorCode.DUPLICATE_LANGUAGE, language + " language profile is already defined");
        }

        languages.add(language);
        final Map<String, Integer> frequencies = profile.getFrequencies();

        for (final Map.Entry<String, Integer> entry : frequencies.entrySet()) {
            final String ngram = entry.getKey();

            if (!languageProbabilityMap.containsKey(ngram)) {
                languageProbabilityMap.put(ngram, new double[languageCount]);
            }

            final int length = ngram.length();
            final int[] nGramCount = profile.getNGramCount();
            if ((length >= 1) && (length <= NGram.MAX_NGRAM_LENGTH)) {
                final Double count = entry.getValue().doubleValue();
                final double probability = count / nGramCount[length - 1];

                languageProbabilityMap.get(ngram)[index] = probability;
            } else {
                LOGGER.warn("Invalid n-gram in language profile: {}", ngram);
            }
        }
    }

    /**
     * Construct Detector instance
     *
     * @return          Detector instance
     * @throws          LangDetectException
     */
    public Detector create() {
        return createDetector();
    }

    /**
     * Construct Detector instance with smoothing parameter
     *
     * @param alpha         smoothing parameter (default value = 0.5)
     * @return              Detector instance
     * @throws              LangDetectException
     */
    public Detector create(final double alpha) {
        final Detector detector = createDetector();
        detector.setAlpha(alpha);
        return detector;
    }

    private Detector createDetector() {
        if (languages.isEmpty()) {
            throw new LangDetectException(ErrorCode.PROFILE_NOT_LOADED, "No language profile classes found");
        }

        return new Detector(this);
    }

    public List<String> getLangList() {
        return unmodifiableList(languages);
    }
}
