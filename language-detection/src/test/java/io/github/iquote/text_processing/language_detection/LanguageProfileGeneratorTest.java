package io.github.iquote.text_processing.language_detection;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.function.BiFunction;

import org.junit.Test;

import io.github.iquote.text_processing.language_detection.langdetect.LangProfileDocument;

public class LanguageProfileGeneratorTest {
    private static final LanguageProfileGenerator GENERATOR = new LanguageProfileGenerator();

    @Test
    public void testLoadFromWikipediaAbstract() throws Exception {
        testProfileGeneration("lorem.xml", this::wikipediaRawTrain);
    }

    @Test
    public void testLoadFromWikipediaAbstractGzip() throws Exception {
        testProfileGeneration("lorem.xml.gz", this::wikipediaGzipTrain);
    }

    @Test
    public void testLoadFromText() throws Exception {
        testProfileGeneration("lorem.txt", GENERATOR::loadFromText);
    }

    private LangProfileDocument wikipediaGzipTrain(final String lang, final InputStream is) {
        return GENERATOR.loadFromWikipediaAbstract(lang, is, true);
    }

    private LangProfileDocument wikipediaRawTrain(final String lang, final InputStream is) {
        return GENERATOR.loadFromWikipediaAbstract(lang, is, false);
    }

    private void testProfileGeneration(final String fileName, final BiFunction<String, InputStream, LangProfileDocument> method) throws IOException {
        final InputStream is = open(fileName);
        final LangProfileDocument document = method.apply("lr-ips", is);

        assertNotNull(document);

        final Map<String, Integer> frequencies = document.getFrequencies();
        assertThat(frequencies, hasKey("Lor"));
        assertThat(frequencies, hasKey("ore"));
        assertThat(frequencies, hasKey("rem"));
        assertThat(frequencies, hasKey("ips"));
        assertThat(frequencies, hasKey("psu"));
        assertThat(frequencies, hasKey("sum"));
    }

    private InputStream open(final String fileName) throws IOException {
        final URL resource = Thread.currentThread().getContextClassLoader().getResource(fileName);

        if (resource == null) {
            throw new NullPointerException();
        }

        return resource.openStream();
    }
}