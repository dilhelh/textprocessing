package com.cybozu.labs.langdetect.util;

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

import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * {@link LangProfile} is a Language Profile Class.
 * Users don't use this class directly.
 *
 * @author Nakatani Shuyo
 * @author Konstantin Gusarov
 */
public abstract class LangProfile {
    private static final int MINIMUM_FREQ = 2;
    private static final int LESS_FREQ_RATIO = 100000;
    private static final Pattern ROMAN_CHECK_REGEX = Pattern.compile(".*[A-Za-z].*");
    private static final Pattern LATIN_CHECK_REGEX = Pattern.compile("^[A-Za-z]$");

    private final String name;
    private final Map<String, Integer> freq = Maps.newHashMap();
    private final int[] nGramCount = new int[NGram.N_GRAM];

    /**
     * @param name              Language name
     * @param initialFreqs      Initial frequency information
     */
    protected LangProfile(final String name, final Map<String, Integer> initialFreqs) {
        this.name = name;

        freq.putAll(initialFreqs);
    }

    /**
     * Add n-gram to profile
     *
     * @param gram      N-Gram to be added
     */
    public void add(final String gram) {
        if (name == null || gram == null) {
            // Illegal
            return;
        }

        final int len = gram.length();
        if (len < 1 || len > NGram.N_GRAM) {
            // Illegal
            return;
        }

        nGramCount[len - 1]++;
        final Integer current = freq.get(gram);
        final Integer val = (current == null)? 1 : current + 1;

        freq.put(gram, val);
    }

    /**
     * Eliminate below less frequency n-grams and noise Latin alphabets
     */
    public void omitLessFreq() {
        if (name == null) {
            // Illegal
            return;
        }

        int threshold = nGramCount[0] / LESS_FREQ_RATIO;
        if (threshold < MINIMUM_FREQ) {
            threshold = MINIMUM_FREQ;
        }

        int roman = 0;
        final Set<String> keys = freq.keySet();

        for (final Iterator<String> i = keys.iterator(); i.hasNext(); ) {
            final String key = i.next();
            final int count = freq.get(key);

            if (count <= threshold) {
                nGramCount[key.length() - 1] -= count;
                i.remove();
            } else {
                if (LATIN_CHECK_REGEX.matcher(key).matches()) {
                    roman += count;
                }
            }
        }

        // roman check
        if (roman < nGramCount[0] / NGram.N_GRAM) {
            final Set<String> keys2 = freq.keySet();

            for (final Iterator<String> i = keys2.iterator(); i.hasNext(); ) {
                final String key = i.next();
                if (ROMAN_CHECK_REGEX.matcher(key).matches()) {
                    nGramCount[key.length() - 1] -= freq.get(key);
                    i.remove();
                }
            }
        }
    }

    /**
     * Update the language profile with (fragmented) text.
     * Extract n-grams from text and add their frequency into the profile.
     *
     * @param text          (Fragmented) Text to extract n-grams
     */
    public void update(String text) {
        if (text == null) return;
        text = NGram.normalizeVietnamese(text);
        final NGram gram = new NGram();
        for (int i = 0; i < text.length(); ++i) {
            final char ch = text.charAt(i);
            gram.addChar(ch);

            for (int n = 1; n <= NGram.N_GRAM; ++n) {
                final String s = gram.get(n);
                add(s);
            }
        }
    }
}
