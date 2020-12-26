package com.github.iquote.text_processing.language_detection.util;

import org.junit.Test;

import com.github.iquote.text_processing.language_detection.util.LangProfile;
import com.github.iquote.text_processing.language_detection.util.TagExtractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TagExtractorTest {
    @Test
    public final void testTagExtractor() {
        final TagExtractor extractor = new TagExtractor(null, 0);
        assertNull(extractor.target);
        assertEquals(0, extractor.threshold);

        final TagExtractor extractor2 = new TagExtractor("abstract", 10);
        assertEquals("abstract", extractor2.target);
        assertEquals(10, extractor2.threshold);
    }

    @Test
    public final void testSetTag() {
        final TagExtractor extractor = new TagExtractor(null, 0);

        extractor.setTag("");
        assertEquals("", extractor.tag);

        extractor.setTag(null);
        assertNull(extractor.tag);
    }

    @Test
    public final void testNormalScenario() {
        final TagExtractor extractor = new TagExtractor("abstract", 10);
        assertEquals(0, extractor.count());

        final LangProfile profile = new TestLangProfile(new int[]{0, 0, 0});

        // normal
        extractor.setTag("abstract");
        extractor.add("This is a sample text.");
        profile.update(extractor.closeTag());
        assertEquals(1, extractor.count());

        // Thisisasampletext
        assertEquals(17, profile.nGramCount[0]);
        
        // _T, Th, hi, ...
        assertEquals(22, profile.nGramCount[1]);

        // _Th, Thi, his, ...
        assertEquals(17, profile.nGramCount[2]);

        // too short
        extractor.setTag("abstract");
        extractor.add("sample");
        profile.update(extractor.closeTag());
        assertEquals(1, extractor.count());

        // other tags
        extractor.setTag("div");
        extractor.add("This is a sample text which is enough long.");
        profile.update(extractor.closeTag());
        assertEquals(1, extractor.count());
    }
    
    @Test
    public final void testClear() {
        final TagExtractor extractor = new TagExtractor("abstract", 10);
        extractor.setTag("abstract");
        extractor.add("This is a sample text.");
        assertEquals("This is a sample text.", extractor.sb.toString());
        assertEquals("abstract", extractor.tag);

        extractor.clear();
        assertEquals("", extractor.sb.toString());
        assertNull(extractor.tag);
    }
}