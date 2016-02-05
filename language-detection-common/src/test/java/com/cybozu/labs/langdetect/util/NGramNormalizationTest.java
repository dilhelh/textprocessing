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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * @author Nakatani Shuyo
 * @author Konstantin Gusarov
 */
@RunWith(Parameterized.class)
public final class NGramNormalizationTest {
    @Parameterized.Parameters(name = "{index}: NGram.normalize({0})={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {'\u0000', ' '},
                {'\u0009', ' '},
                {'\u0020', ' '},
                {'\u0030', ' '},
                {'\u0040', ' '},
                {'\u0041', '\u0041'},
                {'\u005a', '\u005a'},
                {'\u005b', ' '},
                {'\u0060', ' '},
                {'\u0061', '\u0061'},
                {'\u007a', '\u007a'},
                {'\u007b', ' '},
                {'\u007f', ' '},
                {'\u0080', '\u0080'},
                {'\u00a0', ' '},
                {'\u00a1', '\u00a1'},
                {'\u4E00', '\u4E00'},
                {'\u4E01', '\u4E01'},
                {'\u4E02', '\u4E02'},
                {'\u4E03', '\u4E01'},
                {'\u4E04', '\u4E04'},
                {'\u4E05', '\u4E05'},
                {'\u4E06', '\u4E06'},
                {'\u4E07', '\u4E07'},
                {'\u4E08', '\u4E08'},
                {'\u4E09', '\u4E09'},
                {'\u4E10', '\u4E10'},
                {'\u4E11', '\u4E11'},
                {'\u4E12', '\u4E12'},
                {'\u4E13', '\u4E13'},
                {'\u4E14', '\u4E14'},
                {'\u4E15', '\u4E15'},
                {'\u4E1e', '\u4E1e'},
                {'\u4E1f', '\u4E1f'},
                {'\u4E20', '\u4E20'},
                {'\u4E21', '\u4E21'},
                {'\u4E22', '\u4E22'},
                {'\u4E23', '\u4E23'},
                {'\u4E24', '\u4E13'},
                {'\u4E25', '\u4E13'},
                {'\u4E30', '\u4E30'},
                {'\u015f', '\u015f'},
                {'\u0163', '\u0163'},
                {'\u0219', '\u015f'},
                {'\u021b', '\u0163'}
        });
    }

    private final Character c;
    private final Character expected;

    public NGramNormalizationTest(final Character c, final Character expected) {
        this.c = c;
        this.expected = expected;
    }

    @Test
    public void testNormalization() throws Exception {
        final Character actual = NGram.normalize(c);
        assertEquals(expected, actual);
    }
}