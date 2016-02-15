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
import com.cybozu.labs.langdetect.util.TagExtractor;
import org.kgusarov.textprocessing.langdetect.LangProfileDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * Load Wikipedia's abstract XML as corpus and
 * generate its language profile in JSON format.
 * 
 * @author Nakatani Shuyo
 * @author Konstantin Gusarov
 */
public class LanguageProfileGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageProfileGenerator.class);


    /**
     * Load Wikipedia abstract database file and generate its language profile
     *
     * @param lang          Target language name
     * @param file          Target database file path
     * @return              Language profile document instance
     * @throws LangDetectException 
     */
    public LangProfileDocument loadFromWikipediaAbstract(final String lang, final File file) throws LangDetectException {

        final LangProfile profile = new LangProfile(lang);

        BufferedReader br = null;
        try {
            InputStream is = new FileInputStream(file);
            if (file.getName().endsWith(".gz")) is = new GZIPInputStream(is);
            br = new BufferedReader(new InputStreamReader(is, "utf-8"));

            final TagExtractor tagextractor = new TagExtractor("abstract", 100);

            XMLStreamReader reader = null;
            try {
                final XMLInputFactory factory = XMLInputFactory.newInstance();
                reader = factory.createXMLStreamReader(br);
                while (reader.hasNext()) {
                    switch (reader.next()) {
                    case XMLStreamReader.START_ELEMENT:
                        tagextractor.setTag(reader.getName().toString());
                        break;
                    case XMLStreamReader.CHARACTERS:
                        tagextractor.add(reader.getText());
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        final String text = tagextractor.closeTag();
                        if (text != null) profile.update(text);
                        break;
                    }
                }
            } catch (final XMLStreamException e) {
                throw new LangDetectException(ErrorCode.TRAIN_DATA_FORMAT, "Training database file '" + file.getName() + "' is an invalid XML.");
            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (final XMLStreamException e) {}
            }
            System.out.println(lang + ":" + tagextractor.count());

        } catch (final IOException e) {
            throw new LangDetectException(ErrorCode.CANNOT_OPEN_TRAIN_DATA, "Can't open training database file '" + file.getName() + "'");
        } finally {
            try {
                if (br != null) br.close();
            } catch (final IOException e) {}
        }
        return profile.toDocument();
    }


    /**
     * Load text file with UTF-8 and generate its language profile
     *
     * @param lang              Target language name
     * @param file              Target file path
     * @return                  Language profile document instance
     * @throws LangDetectException 
     */
    public static LangProfileDocument loadFromText(final String lang, final File file) throws LangDetectException {

        final LangProfile profile = new LangProfile(lang);

        BufferedReader is = null;
        try {
            is = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));

            int count = 0;
            while (is.ready()) {
                final String line = is.readLine();
                profile.update(line);
                ++count;
            }

            System.out.println(lang + ":" + count);

        } catch (final IOException e) {
            throw new LangDetectException(ErrorCode.CANNOT_OPEN_TRAIN_DATA, "Can't open training database file '" + file.getName() + "'");
        } finally {
            try {
                if (is != null) is.close();
            } catch (final IOException e) {}
        }
        return profile.toDocument();
    }
}
