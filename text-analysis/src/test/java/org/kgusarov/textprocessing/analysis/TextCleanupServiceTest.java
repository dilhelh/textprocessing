package org.kgusarov.textprocessing.analysis;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TextCleanupServiceTest {
    private static final TextCleanupService SERVICE = new TextCleanupService();
    private static final String WITH_GARBAGE = "\u200B\u200C\u200D\u200E\u200FTh\u200B\u200C\u200D\u200E\u200Fis " +
            "is a test stri\u202A\u202B\u202C\u202D\u202Eng\u202A\u202B\u202C\u202D\u202E";
    private static final String CLEAN = "This is a test string";

    @Test
    public void testRemoveDirectionAndInvisibleChars() throws Exception {
        final String s = SERVICE.removeDirectionAndInvisibleChars(WITH_GARBAGE);
        assertEquals(CLEAN, s);
    }
}