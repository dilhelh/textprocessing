package org.kgusarov.textprocessing.analysis;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TextCleanupServiceTest {
    private static final TextCleanupService SERVICE = new TextCleanupService();
    private static final String WITH_GARBAGE = "\u200B\u200C\u200D\u200E\u200FTh\u200B\u200C\u200D\u200E\u200Fis " +
            "is a test stri\u202A\u202B\u202C\u202D\u202Eng\u202A\u202B\u202C\u202D\u202E";
    private static final String CLEAN = "This is a test string";

    private static final String WITH_HASHTAGS = "I've just done #some #work";
    private static final String WITH_HASHTAGS_AND_GARBAGE = "I'\u200Cve\u200C \u200Cj\u200Cust\u200C " +
            "\u200Cdone\u200C \u200C#some\u200C \u200C#work\u200C";
    private static final String WITHOUT_HASHTAGS = "I've just done";

    private static final String WITH_CASHTAGS = "I've just done $some $work";
    private static final String WITH_CASHTAGS_AND_GARBAGE = "I'\u200Cve\u200C \u200Cj\u200Cust\u200C " +
            "\u200Cdone\u200C \u200C$some\u200C \u200C$work\u200C";
    private static final String WITHOUT_CASHTAGS = "I've just done";

    private static final String WITH_MENTIONS = "@kgusarov Hey, there!";
    private static final String WITH_MENTIONS_AND_GARBAGE = "\u200C@kgu\u200Csarov\u200C \u200CHey, \u200Cthere!";
    private static final String WITHOUT_MENTIONS = "Hey, there!";

    private static final String WITH_URLS = "visit http://www.google.com for something";
    private static final String WITH_URLS_AND_GARBAGE = "visit\u200C\u200C http:\u200C//www.google.com f\u200Cor \u200Csomething";
    private static final String WITHOUT_URLS = "visit  for something";

    private static final String WITH_NOPROTO_URLS = "visit google.com for something";
    private static final String WITH_NOPROTO_URLS_AND_GARBAGE = "visit\u200C\u200C \u200Cgoogle.com f\u200Cor \u200Csomething";
    private static final String WITHOUT_NOPROTO_URLS = "visit  for something";

    private static final String WITH_MIXED_ENTITITES = "$pssst @kgusarov Hey, there! Visit http://www.google.com for #something";
    private static final String WITH_MIXED_ENTITITES_AND_GARBAGE = "$pssst @kgusarov\u200C Hey, \u200Cthe\u200Cre! Visit " +
            "http:\u200C/\u200C\u200C/www.google.com for\u200C #somet\u200Ching";
    private static final String WITHOUT_MIXED_ENTITITES = "Hey, there! Visit  for";

    @Test
    public void testRemoveTwitterEntititesNoProtoUrls() throws Exception {
        String s = SERVICE.removeTwitterEntities(WITH_NOPROTO_URLS, false);
        assertEquals(WITHOUT_NOPROTO_URLS, s.trim());

        s = SERVICE.removeTwitterEntities(WITH_NOPROTO_URLS_AND_GARBAGE, true);
        assertEquals(WITHOUT_NOPROTO_URLS, s.trim());
    }

    @Test
    public void testRemoveDirectionAndInvisibleChars() throws Exception {
        final String s = SERVICE.removeDirectionAndInvisibleChars(WITH_GARBAGE);
        assertEquals(CLEAN, s);
    }

    @Test
    public void testRemoveTwitterEntitiesNoEntities() throws Exception {
        String s = SERVICE.removeTwitterEntities(WITH_GARBAGE, false);
        assertEquals(WITH_GARBAGE, s);

        s = SERVICE.removeTwitterEntities(WITH_GARBAGE, true);
        assertEquals(CLEAN, s);
    }

    @Test
    public void testRemoveTwitterEntitiesHashtags() throws Exception {
        String s = SERVICE.removeTwitterEntities(WITH_HASHTAGS, false);
        assertEquals(WITHOUT_HASHTAGS, s.trim());

        s = SERVICE.removeTwitterEntities(WITH_HASHTAGS_AND_GARBAGE, true);
        assertEquals(WITHOUT_HASHTAGS, s.trim());
    }

    @Test
    public void testRemoveTwitterEntitiesCashtags() throws Exception {
        String s = SERVICE.removeTwitterEntities(WITH_CASHTAGS, false);
        assertEquals(WITHOUT_CASHTAGS, s.trim());

        s = SERVICE.removeTwitterEntities(WITH_CASHTAGS_AND_GARBAGE, true);
        assertEquals(WITHOUT_CASHTAGS, s.trim());
    }

    @Test
    public void testRemoveTwitterEntitiesMentions() throws Exception {
        String s = SERVICE.removeTwitterEntities(WITH_MENTIONS, false);
        assertEquals(WITHOUT_MENTIONS, s.trim());

        s = SERVICE.removeTwitterEntities(WITH_MENTIONS_AND_GARBAGE, true);
        assertEquals(WITHOUT_MENTIONS, s.trim());
    }

    @Test
    public void testRemoveTwitterEntitiesUrls() throws Exception {
        String s = SERVICE.removeTwitterEntities(WITH_URLS, false);
        assertEquals(WITHOUT_URLS, s.trim());

        s = SERVICE.removeTwitterEntities(WITH_URLS_AND_GARBAGE, true);
        assertEquals(WITHOUT_URLS, s.trim());
    }

    @Test
    public void testRemoveTwitterEntitiesMixed() throws Exception {
        String s = SERVICE.removeTwitterEntities(WITH_MIXED_ENTITITES, false);
        assertEquals(WITHOUT_MIXED_ENTITITES, s.trim());

        s = SERVICE.removeTwitterEntities(WITH_MIXED_ENTITITES_AND_GARBAGE, true);
        assertEquals(WITHOUT_MIXED_ENTITITES, s.trim());
    }
}