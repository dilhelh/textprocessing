package com.cybozu.labs.langdetect.util;

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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Nakatani Shuyo
 * @author Konstantin Gusarov
 */
public class LangProfileTest {
    @Test
    public final void testAdd() {
        final LangProfile profile = new TestLangProfile(new int[]{0, 0, 0});

        profile.add("a");
        assertEquals(1, (int) profile.frequencies.get("a"));

        profile.add("a");
        assertEquals(2, (int) profile.frequencies.get("a"));
    }

    @Test
    public final void testAddIllegally() {
        final LangProfile profile = new TestLangProfile(new int[]{0, 0, 0});
        profile.add("a");
        profile.add("");
        profile.add("abcd");

        assertEquals(1, (int) profile.frequencies.get("a"));
        assertNull(profile.frequencies.get(""));
        assertNull(profile.frequencies.get("abcd"));
    }

    @Test
    public final void testOmitLessFreq() {
        final LangProfile profile = new TestLangProfile(new int[]{0, 0, 0});
        final String[] grams = "a b c \u3042 \u3044 \u3046 \u3048 \u304a \u304b \u304c \u304d \u304e \u304f".split(" ");

        for (int i = 0; i < 5; ++i) {
            for (final String g : grams) {
                profile.add(g);
            }
        }

        profile.add("\u3050");

        assertEquals(5, (int) profile.frequencies.get("a"));
        assertEquals(5, (int) profile.frequencies.get("\u3042"));
        assertEquals(1, (int) profile.frequencies.get("\u3050"));

        profile.omitLessFreq();
        assertNull(profile.frequencies.get("a"));
        assertEquals(5, (int) profile.frequencies.get("\u3042"));
        assertNull(profile.frequencies.get("\u3050"));
    }
}