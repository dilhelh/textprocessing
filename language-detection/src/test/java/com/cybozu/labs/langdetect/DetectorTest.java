package com.cybozu.labs.langdetect;

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

import com.cybozu.labs.langdetect.util.LangProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.kgusarov.textprocessing.langdetect.LangProfileDocument;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Nakatani Shuyo
 * @author Konstantin Gusarov
 */
public class DetectorTest {
    private static final String TRAINING_EN = "a a a b b c c d e";
    private static final String TRAINING_FR = "a b b c c c d d d";
    private static final String TRAINING_JA = "\u3042 \u3042 \u3042 \u3044 \u3046 \u3048 \u3048";
    private static final String PROFILE_TEMPLATE = "{\"name\": \"%s\", \"n_words\": [0,0,0], \"freq\": {}}";

    private DetectorFactory detectorFactory;

    @Before
    public void setUp() throws Exception {
        detectorFactory = new DetectorFactory();
        final ObjectMapper mapper = new ObjectMapper();

        LangProfileDocument lpd = mapper.readValue(String.format(PROFILE_TEMPLATE, "en"), LangProfileDocument.class);
        final LangProfile en = new LangProfile(lpd);
        for (final String w : TRAINING_EN.split(" ")) {
            en.add(w);
        }
        detectorFactory.addProfile(en, 0, 3);

        lpd = mapper.readValue(String.format(PROFILE_TEMPLATE, "fr"), LangProfileDocument.class);
        final LangProfile fr = new LangProfile(lpd);
        for (final String w : TRAINING_FR.split(" ")) {
            fr.add(w);
        }
        detectorFactory.addProfile(fr, 1, 3);

        lpd = mapper.readValue(String.format(PROFILE_TEMPLATE, "ja"), LangProfileDocument.class);
        final LangProfile ja = new LangProfile(lpd);
        for (final String w : TRAINING_JA.split(" ")) {
            ja.add(w);
        }
        detectorFactory.addProfile(ja, 2, 3);
    }

    @Test
    public final void testDetector1() throws LangDetectException {
        final Detector detector = detectorFactory.create();

        detector.append("a");
        assertEquals("en", detector.detect());
    }

    @Test
    public final void testDetector2() throws LangDetectException {
        final Detector detector = detectorFactory.create();

        detector.append("b d");
        assertEquals("fr", detector.detect());
    }

    @Test
    public final void testDetector3() throws LangDetectException {
        final Detector detector = detectorFactory.create();

        detector.append("d e");
        assertEquals("en", detector.detect());
    }

    @Test
    public final void testDetector4() throws LangDetectException {
        final Detector detector = detectorFactory.create();

        detector.append("\u3042\u3042\u3042\u3042a");
        assertEquals("ja", detector.detect());
    }

    @Test
    public final void testLangList() throws LangDetectException {
        final List<String> langList = detectorFactory.getLangList();

        assertEquals(3, langList.size());
        assertEquals("en", langList.get(0));
        assertEquals("fr", langList.get(1));
        assertEquals("ja", langList.get(2));
    }

    @Test(expected = UnsupportedOperationException.class)
    public final void testLangListException() throws LangDetectException {
        final List<String> langList = detectorFactory.getLangList();

        langList.add("hoge");
    }
}