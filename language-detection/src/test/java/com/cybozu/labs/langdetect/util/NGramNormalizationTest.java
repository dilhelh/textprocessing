package com.cybozu.labs.langdetect.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public final class NGramNormalizationTest {
    @Parameterized.Parameters(name = "{index}: NGram.normalize")
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