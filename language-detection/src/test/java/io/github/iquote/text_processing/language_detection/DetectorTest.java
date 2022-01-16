package io.github.iquote.text_processing.language_detection;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.iquote.text_processing.language_detection.Detector;
import io.github.iquote.text_processing.language_detection.DetectorFactory;
import io.github.iquote.text_processing.language_detection.LangDetectException;
import io.github.iquote.text_processing.language_detection.langdetect.LangProfileDocument;
import io.github.iquote.text_processing.language_detection.util.LangProfile;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DetectorTest {
    private static final String TRAINING_ENG = "a a a b b c c d e";
    private static final String TRAINING_FRA = "a b b c c c d d d";
    private static final String TRAINING_JPN = "\u3042 \u3042 \u3042 \u3044 \u3046 \u3048 \u3048";
    private static final String PROFILE_TEMPLATE = "{\"name\": \"%s\", \"n_words\": [0,0,0], \"freq\": {}}";

    private DetectorFactory detectorFactory;

    @Before
    public void setUp() throws Exception {
        detectorFactory = new DetectorFactory();
        final ObjectMapper mapper = new ObjectMapper();

        LangProfileDocument lpd = mapper.readValue(String.format(PROFILE_TEMPLATE, "eng"), LangProfileDocument.class);
        final LangProfile eng = new LangProfile(lpd);
        for (final String w : TRAINING_ENG.split(" ")) {
            eng.add(w);
        }
        detectorFactory.addProfile(eng, 0, 3);

        lpd = mapper.readValue(String.format(PROFILE_TEMPLATE, "fra"), LangProfileDocument.class);
        final LangProfile fra = new LangProfile(lpd);
        for (final String w : TRAINING_FRA.split(" ")) {
            fra.add(w);
        }
        detectorFactory.addProfile(fra, 1, 3);

        lpd = mapper.readValue(String.format(PROFILE_TEMPLATE, "jpn"), LangProfileDocument.class);
        final LangProfile jpn = new LangProfile(lpd);
        for (final String w : TRAINING_JPN.split(" ")) {
            jpn.add(w);
        }
        detectorFactory.addProfile(jpn, 2, 3);
    }

    @Test
    public final void testDetector1() throws LangDetectException {
        final Detector detector = detectorFactory.create();

        detector.append("a");
        assertEquals("eng", detector.detect());
    }

    @Test
    public final void testDetector2() throws LangDetectException {
        final Detector detector = detectorFactory.create();

        detector.append("b d");
        assertEquals("fra", detector.detect());
    }

    @Test
    public final void testDetector3() throws LangDetectException {
        final Detector detector = detectorFactory.create();

        detector.append("d e");
        assertEquals("eng", detector.detect());
    }

    @Test
    public final void testDetector4() throws LangDetectException {
        final Detector detector = detectorFactory.create();

        detector.append("\u3042\u3042\u3042\u3042a");
        assertEquals("jpn", detector.detect());
    }

    @Test
    public final void testLangList() throws LangDetectException {
        final List<String> langList = detectorFactory.getLangList();

        assertEquals(3, langList.size());
        assertEquals("eng", langList.get(0));
        assertEquals("fra", langList.get(1));
        assertEquals("jpn", langList.get(2));
    }

    @Test(expected = UnsupportedOperationException.class)
    public final void testLangListException() throws LangDetectException {
        final List<String> langList = detectorFactory.getLangList();

        langList.add("hoge");
    }
}