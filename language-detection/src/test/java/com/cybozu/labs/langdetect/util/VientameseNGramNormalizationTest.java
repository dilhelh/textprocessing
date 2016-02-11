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
public final class VientameseNGramNormalizationTest {
    @Parameterized.Parameters(name = "{index}: NGram.normalizeVietnamese")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"", ""},
                {"ABC", "ABC"},
                {"012", "012"},
                {"\u00c0", "\u00c0"},

                {"\u0041\u0300", "\u00C0"},
                {"\u0045\u0300", "\u00C8"},
                {"\u0049\u0300", "\u00CC"},
                {"\u004F\u0300", "\u00D2"},
                {"\u0055\u0300", "\u00D9"},
                {"\u0059\u0300", "\u1EF2"},
                {"\u0061\u0300", "\u00E0"},
                {"\u0065\u0300", "\u00E8"},
                {"\u0069\u0300", "\u00EC"},
                {"\u006F\u0300", "\u00F2"},
                {"\u0075\u0300", "\u00F9"},
                {"\u0079\u0300", "\u1EF3"},
                {"\u00C2\u0300", "\u1EA6"},
                {"\u00CA\u0300", "\u1EC0"},
                {"\u00D4\u0300", "\u1ED2"},
                {"\u00E2\u0300", "\u1EA7"},
                {"\u00EA\u0300", "\u1EC1"},
                {"\u00F4\u0300", "\u1ED3"},
                {"\u0102\u0300", "\u1EB0"},
                {"\u0103\u0300", "\u1EB1"},
                {"\u01A0\u0300", "\u1EDC"},
                {"\u01A1\u0300", "\u1EDD"},
                {"\u01AF\u0300", "\u1EEA"},
                {"\u01B0\u0300", "\u1EEB"},

                {"\u0041\u0301", "\u00C1"},
                {"\u0045\u0301", "\u00C9"},
                {"\u0049\u0301", "\u00CD"},
                {"\u004F\u0301", "\u00D3"},
                {"\u0055\u0301", "\u00DA"},
                {"\u0059\u0301", "\u00DD"},
                {"\u0061\u0301", "\u00E1"},
                {"\u0065\u0301", "\u00E9"},
                {"\u0069\u0301", "\u00ED"},
                {"\u006F\u0301", "\u00F3"},
                {"\u0075\u0301", "\u00FA"},
                {"\u0079\u0301", "\u00FD"},
                {"\u00C2\u0301", "\u1EA4"},
                {"\u00CA\u0301", "\u1EBE"},
                {"\u00D4\u0301", "\u1ED0"},
                {"\u00E2\u0301", "\u1EA5"},
                {"\u00EA\u0301", "\u1EBF"},
                {"\u00F4\u0301", "\u1ED1"},
                {"\u0102\u0301", "\u1EAE"},
                {"\u0103\u0301", "\u1EAF"},
                {"\u01A0\u0301", "\u1EDA"},
                {"\u01A1\u0301", "\u1EDB"},
                {"\u01AF\u0301", "\u1EE8"},
                {"\u01B0\u0301", "\u1EE9"},

                {"\u0041\u0303", "\u00C3"},
                {"\u0045\u0303", "\u1EBC"},
                {"\u0049\u0303", "\u0128"},
                {"\u004F\u0303", "\u00D5"},
                {"\u0055\u0303", "\u0168"},
                {"\u0059\u0303", "\u1EF8"},
                {"\u0061\u0303", "\u00E3"},
                {"\u0065\u0303", "\u1EBD"},
                {"\u0069\u0303", "\u0129"},
                {"\u006F\u0303", "\u00F5"},
                {"\u0075\u0303", "\u0169"},
                {"\u0079\u0303", "\u1EF9"},
                {"\u00C2\u0303", "\u1EAA"},
                {"\u00CA\u0303", "\u1EC4"},
                {"\u00D4\u0303", "\u1ED6"},
                {"\u00E2\u0303", "\u1EAB"},
                {"\u00EA\u0303", "\u1EC5"},
                {"\u00F4\u0303", "\u1ED7"},
                {"\u0102\u0303", "\u1EB4"},
                {"\u0103\u0303", "\u1EB5"},
                {"\u01A0\u0303", "\u1EE0"},
                {"\u01A1\u0303", "\u1EE1"},
                {"\u01AF\u0303", "\u1EEE"},
                {"\u01B0\u0303", "\u1EEF"},

                {"\u0041\u0309", "\u1EA2"},
                {"\u0045\u0309", "\u1EBA"},
                {"\u0049\u0309", "\u1EC8"},
                {"\u004F\u0309", "\u1ECE"},
                {"\u0055\u0309", "\u1EE6"},
                {"\u0059\u0309", "\u1EF6"},
                {"\u0061\u0309", "\u1EA3"},
                {"\u0065\u0309", "\u1EBB"},
                {"\u0069\u0309", "\u1EC9"},
                {"\u006F\u0309", "\u1ECF"},
                {"\u0075\u0309", "\u1EE7"},
                {"\u0079\u0309", "\u1EF7"},
                {"\u00C2\u0309", "\u1EA8"},
                {"\u00CA\u0309", "\u1EC2"},
                {"\u00D4\u0309", "\u1ED4"},
                {"\u00E2\u0309", "\u1EA9"},
                {"\u00EA\u0309", "\u1EC3"},
                {"\u00F4\u0309", "\u1ED5"},
                {"\u0102\u0309", "\u1EB2"},
                {"\u0103\u0309", "\u1EB3"},
                {"\u01A0\u0309", "\u1EDE"},
                {"\u01A1\u0309", "\u1EDF"},
                {"\u01AF\u0309", "\u1EEC"},
                {"\u01B0\u0309", "\u1EED"},

                {"\u0041\u0323", "\u1EA0"},
                {"\u0045\u0323", "\u1EB8"},
                {"\u0049\u0323", "\u1ECA"},
                {"\u004F\u0323", "\u1ECC"},
                {"\u0055\u0323", "\u1EE4"},
                {"\u0059\u0323", "\u1EF4"},
                {"\u0061\u0323", "\u1EA1"},
                {"\u0065\u0323", "\u1EB9"},
                {"\u0069\u0323", "\u1ECB"},
                {"\u006F\u0323", "\u1ECD"},
                {"\u0075\u0323", "\u1EE5"},
                {"\u0079\u0323", "\u1EF5"},
                {"\u00C2\u0323", "\u1EAC"},
                {"\u00CA\u0323", "\u1EC6"},
                {"\u00D4\u0323", "\u1ED8"},
                {"\u00E2\u0323", "\u1EAD"},
                {"\u00EA\u0323", "\u1EC7"},
                {"\u00F4\u0323", "\u1ED9"},
                {"\u0102\u0323", "\u1EB6"},
                {"\u0103\u0323", "\u1EB7"},
                {"\u01A0\u0323", "\u1EE2"},
                {"\u01A1\u0323", "\u1EE3"},
                {"\u01AF\u0323", "\u1EF0"},
                {"\u01B0\u0323", "\u1EF1"}
        });
    }

    private final String s;
    private final String expected;

    public VientameseNGramNormalizationTest(final String s, final String expected) {
        this.s = s;
        this.expected = expected;
    }

    @Test
    public void testNormalization() throws Exception {
        final String actual = NGram.normalizeVietnamese(s);
        assertEquals(expected, actual);
    }
}