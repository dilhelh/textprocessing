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

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.UnicodeBlock.*;

/**
 * Cut out N-gram from text.
 * Users don't use this class directly.
 *
 * @author Nakatani Shuyo
 * @author Konstantin Gusarov
 */
class NGram {
    private static final String LATIN1_EXCLUDED = Messages.getString("NGram.LATIN1_EXCLUDE");
    private static final HashMap<Character, Character> CJK_MAP = Maps.newHashMap();

    /**
     * CJK Kanji Normalization Mapping
     */
    private static final String[] CJK_CLASSES = {
            Messages.getString("NGram.KANJI_1_0"),
            Messages.getString("NGram.KANJI_1_2"),
            Messages.getString("NGram.KANJI_1_4"),
            Messages.getString("NGram.KANJI_1_8"),
            Messages.getString("NGram.KANJI_1_11"),
            Messages.getString("NGram.KANJI_1_12"),
            Messages.getString("NGram.KANJI_1_13"),
            Messages.getString("NGram.KANJI_1_14"),
            Messages.getString("NGram.KANJI_1_16"),
            Messages.getString("NGram.KANJI_1_18"),
            Messages.getString("NGram.KANJI_1_22"),
            Messages.getString("NGram.KANJI_1_27"),
            Messages.getString("NGram.KANJI_1_29"),
            Messages.getString("NGram.KANJI_1_31"),
            Messages.getString("NGram.KANJI_1_35"),
            Messages.getString("NGram.KANJI_2_0"),
            Messages.getString("NGram.KANJI_2_1"),
            Messages.getString("NGram.KANJI_2_4"),
            Messages.getString("NGram.KANJI_2_9"),
            Messages.getString("NGram.KANJI_2_10"),
            Messages.getString("NGram.KANJI_2_11"),
            Messages.getString("NGram.KANJI_2_12"),
            Messages.getString("NGram.KANJI_2_13"),
            Messages.getString("NGram.KANJI_2_15"),
            Messages.getString("NGram.KANJI_2_16"),
            Messages.getString("NGram.KANJI_2_18"),
            Messages.getString("NGram.KANJI_2_21"),
            Messages.getString("NGram.KANJI_2_22"),
            Messages.getString("NGram.KANJI_2_23"),
            Messages.getString("NGram.KANJI_2_28"),
            Messages.getString("NGram.KANJI_2_29"),
            Messages.getString("NGram.KANJI_2_30"),
            Messages.getString("NGram.KANJI_2_31"),
            Messages.getString("NGram.KANJI_2_32"),
            Messages.getString("NGram.KANJI_2_35"),
            Messages.getString("NGram.KANJI_2_36"),
            Messages.getString("NGram.KANJI_2_37"),
            Messages.getString("NGram.KANJI_2_38"),
            Messages.getString("NGram.KANJI_3_1"),
            Messages.getString("NGram.KANJI_3_2"),
            Messages.getString("NGram.KANJI_3_3"),
            Messages.getString("NGram.KANJI_3_4"),
            Messages.getString("NGram.KANJI_3_5"),
            Messages.getString("NGram.KANJI_3_8"),
            Messages.getString("NGram.KANJI_3_9"),
            Messages.getString("NGram.KANJI_3_11"),
            Messages.getString("NGram.KANJI_3_12"),
            Messages.getString("NGram.KANJI_3_13"),
            Messages.getString("NGram.KANJI_3_15"),
            Messages.getString("NGram.KANJI_3_16"),
            Messages.getString("NGram.KANJI_3_18"),
            Messages.getString("NGram.KANJI_3_19"),
            Messages.getString("NGram.KANJI_3_22"),
            Messages.getString("NGram.KANJI_3_23"),
            Messages.getString("NGram.KANJI_3_27"),
            Messages.getString("NGram.KANJI_3_29"),
            Messages.getString("NGram.KANJI_3_30"),
            Messages.getString("NGram.KANJI_3_31"),
            Messages.getString("NGram.KANJI_3_32"),
            Messages.getString("NGram.KANJI_3_35"),
            Messages.getString("NGram.KANJI_3_36"),
            Messages.getString("NGram.KANJI_3_37"),
            Messages.getString("NGram.KANJI_3_38"),
            Messages.getString("NGram.KANJI_4_0"),
            Messages.getString("NGram.KANJI_4_9"),
            Messages.getString("NGram.KANJI_4_10"),
            Messages.getString("NGram.KANJI_4_16"),
            Messages.getString("NGram.KANJI_4_17"),
            Messages.getString("NGram.KANJI_4_18"),
            Messages.getString("NGram.KANJI_4_22"),
            Messages.getString("NGram.KANJI_4_24"),
            Messages.getString("NGram.KANJI_4_28"),
            Messages.getString("NGram.KANJI_4_34"),
            Messages.getString("NGram.KANJI_4_39"),
            Messages.getString("NGram.KANJI_5_10"),
            Messages.getString("NGram.KANJI_5_11"),
            Messages.getString("NGram.KANJI_5_12"),
            Messages.getString("NGram.KANJI_5_13"),
            Messages.getString("NGram.KANJI_5_14"),
            Messages.getString("NGram.KANJI_5_18"),
            Messages.getString("NGram.KANJI_5_26"),
            Messages.getString("NGram.KANJI_5_29"),
            Messages.getString("NGram.KANJI_5_34"),
            Messages.getString("NGram.KANJI_5_39"),
            Messages.getString("NGram.KANJI_6_0"),
            Messages.getString("NGram.KANJI_6_3"),
            Messages.getString("NGram.KANJI_6_9"),
            Messages.getString("NGram.KANJI_6_10"),
            Messages.getString("NGram.KANJI_6_11"),
            Messages.getString("NGram.KANJI_6_12"),
            Messages.getString("NGram.KANJI_6_16"),
            Messages.getString("NGram.KANJI_6_18"),
            Messages.getString("NGram.KANJI_6_20"),
            Messages.getString("NGram.KANJI_6_21"),
            Messages.getString("NGram.KANJI_6_22"),
            Messages.getString("NGram.KANJI_6_23"),
            Messages.getString("NGram.KANJI_6_25"),
            Messages.getString("NGram.KANJI_6_28"),
            Messages.getString("NGram.KANJI_6_29"),
            Messages.getString("NGram.KANJI_6_30"),
            Messages.getString("NGram.KANJI_6_32"),
            Messages.getString("NGram.KANJI_6_34"),
            Messages.getString("NGram.KANJI_6_35"),
            Messages.getString("NGram.KANJI_6_37"),
            Messages.getString("NGram.KANJI_6_39"),
            Messages.getString("NGram.KANJI_7_0"),
            Messages.getString("NGram.KANJI_7_3"),
            Messages.getString("NGram.KANJI_7_6"),
            Messages.getString("NGram.KANJI_7_7"),
            Messages.getString("NGram.KANJI_7_9"),
            Messages.getString("NGram.KANJI_7_11"),
            Messages.getString("NGram.KANJI_7_12"),
            Messages.getString("NGram.KANJI_7_13"),
            Messages.getString("NGram.KANJI_7_16"),
            Messages.getString("NGram.KANJI_7_18"),
            Messages.getString("NGram.KANJI_7_19"),
            Messages.getString("NGram.KANJI_7_20"),
            Messages.getString("NGram.KANJI_7_21"),
            Messages.getString("NGram.KANJI_7_23"),
            Messages.getString("NGram.KANJI_7_25"),
            Messages.getString("NGram.KANJI_7_28"),
            Messages.getString("NGram.KANJI_7_29"),
            Messages.getString("NGram.KANJI_7_32"),
            Messages.getString("NGram.KANJI_7_33"),
            Messages.getString("NGram.KANJI_7_35"),
            Messages.getString("NGram.KANJI_7_37"),
    };

    private static final String[] NORMALIZED_VI_CHARS = {
            Messages.getString("NORMALIZED_VI_CHARS_0300"),
            Messages.getString("NORMALIZED_VI_CHARS_0301"),
            Messages.getString("NORMALIZED_VI_CHARS_0303"),
            Messages.getString("NORMALIZED_VI_CHARS_0309"),
            Messages.getString("NORMALIZED_VI_CHARS_0323")};

    private static final String TO_NORMALIZE_VI_CHARS = Messages.getString("TO_NORMALIZE_VI_CHARS");
    private static final String DMARK_CLASS = Messages.getString("DMARK_CLASS");
    private static final Pattern ALPHABET_WITH_DMARK = Pattern.compile("([" + TO_NORMALIZE_VI_CHARS + "])(["
            + DMARK_CLASS + "])");

    static final int N_GRAM = 3;

    static {
        for (final String cjkClass : CJK_CLASSES) {
            final char representative = cjkClass.charAt(0);

            cjkClass.chars()
                    .mapToObj(i -> (char) i)
                    .forEach(c -> CJK_MAP.put(c, representative));
        }
    }

    private String chars;
    private boolean capital;

    NGram() {
        chars = " ";
        capital = false;
    }

    /**
     * Append a character into ngram buffer.
     *
     * @param ch    Character to add to buffer
     */
    void addChar(final char ch) {
        final char normalized = normalize(ch);
        final char lastchar = chars.charAt(chars.length() - 1);

        if (lastchar == ' ') {
            chars = " ";
            capital = false;

            if (normalized == ' ') {
                return;
            }
        } else if (chars.length() >= N_GRAM) {
            chars = chars.substring(1);
        }

        chars += normalized;

        if (Character.isUpperCase(normalized)) {
            if (Character.isUpperCase(lastchar)) {
                capital = true;
            }
        } else {
            capital = false;
        }
    }

    /**
     * Get n-Gram
     *
     * @param n     Length of n-gram
     * @return      n-Gram String (null if it is invalid)
     */
    @Nullable
    String get(final int n) {
        if (capital) {
            return null;
        }

        final int len = chars.length();
        if (n < 1 || n > N_GRAM || len < n) {
            return null;
        }

        if (n == 1) {
            final char ch = chars.charAt(len - 1);
            if (ch == ' ') {
                return null;
            }

            return Character.toString(ch);
        }

        return chars.substring(len - n, len);
    }

    /**
     * Character Normalization
     *
     * @param ch    Character to be normalized
     * @return      Normalized character
     */
    static char normalize(final char ch) {
        final Character.UnicodeBlock block = of(ch);
        if (Objects.equals(block, GENERAL_PUNCTUATION)) {
            return ' ';
        }

        if (Objects.equals(block, BASIC_LATIN)) {
            if (ch < 'A' || (ch < 'a' && ch > 'Z') || ch > 'z') {
                return ' ';
            }
        }

        if (Objects.equals(block, LATIN_1_SUPPLEMENT)) {
            if (LATIN1_EXCLUDED.indexOf(ch) >= 0) {
                return ' ';
            }
        }

        if (Objects.equals(block, LATIN_EXTENDED_B)) {
            // normalization for Romanian
            if (ch == '\u0219') {
                // Small S with comma below => with cedilla
                return '\u015f';
            }

            if (ch == '\u021b') {
                // Small T with comma below => with cedilla
                return '\u0163';
            }
        }

        if (Objects.equals(block, ARABIC)) {
            if (ch == '\u06cc') {
                // Farsi yeh => Arabic yeh
                return '\u064a';
            }
        }

        if (Objects.equals(block, LATIN_EXTENDED_ADDITIONAL)) {
            if (ch >= '\u1ea0') {
                return '\u1ec3';
            }
        }

        if (Objects.equals(block, HIRAGANA)) {
            return '\u3042';
        }

        if (Objects.equals(block, KATAKANA)) {
            return '\u30a2';
        }

        if (Objects.equals(block, BOPOMOFO) || Objects.equals(block, BOPOMOFO_EXTENDED)) {
            return '\u3105';
        }

        if (Objects.equals(block, CJK_UNIFIED_IDEOGRAPHS)) {
            if (CJK_MAP.containsKey(ch)) {
                return CJK_MAP.get(ch);
            }
        }

        if (Objects.equals(block, HANGUL_SYLLABLES)) {
            return '\uac00';
        }

        return ch;
    }

    /**
     * Normalizer for Vietnamese.
     * Normalize Alphabet + Diacritical Mark(U+03xx) into U+1Exx .
     *
     * @param text      Vietnamese text to be normalized
     * @return          Normalized text
     */
    static String normalizeVietnamese(final String text) {
        final Matcher m = ALPHABET_WITH_DMARK.matcher(text);
        final StringBuffer sb = new StringBuffer();

        while (m.find()) {
            final String a = m.group(1);
            final int alphabet = TO_NORMALIZE_VI_CHARS.indexOf(a);

            // Diacritical Mark
            final String dm = m.group(2);
            final int dmark = DMARK_CLASS.indexOf(dm);

            final String replacement = NORMALIZED_VI_CHARS[dmark]
                    .substring(alphabet, alphabet + 1);

            m.appendReplacement(sb, replacement);
        }

        if (sb.length() == 0) {
            return text;
        }

        m.appendTail(sb);
        return sb.toString();
    }
}
