package com.cybozu.labs.langdetect;

import org.junit.Test;
import org.kgusarov.textprocessing.langdetect.LangProfileDocument;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.function.BiFunction;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class LanguageProfileGeneratorTest {
    private static final LanguageProfileGenerator GENERATOR = new LanguageProfileGenerator();

    @Test
    public void testLoadFromWikipediaAbstract() throws Exception {
        testProfileGeneration("lorem.xml", GENERATOR::loadFromWikipediaAbstract);
    }

    @Test
    public void testLoadFromWikipediaAbstractGzip() throws Exception {
        testProfileGeneration("lorem.xml.gz", GENERATOR::loadFromWikipediaAbstract);
    }

    @Test
    public void testLoadFromText() throws Exception {
        testProfileGeneration("lorem.txt", GENERATOR::loadFromText);
    }

    private void testProfileGeneration(final String fileName, final BiFunction<String, File, LangProfileDocument> method) {
        final File file = resolveFile(fileName);
        final LangProfileDocument document = method.apply("lr-ips", file);

        assertNotNull(document);

        final Map<String, Integer> frequencies = document.getFrequencies();
        assertThat(frequencies, hasKey("Lor"));
        assertThat(frequencies, hasKey("ore"));
        assertThat(frequencies, hasKey("rem"));
        assertThat(frequencies, hasKey("ips"));
        assertThat(frequencies, hasKey("psu"));
        assertThat(frequencies, hasKey("sum"));
    }

    private File resolveFile(final String fileName) {
        final URL resource = Thread.currentThread().getContextClassLoader().getResource(fileName);

        if (resource == null) {
            throw new NullPointerException();
        }

        final String file = resource.getFile();
        return new File(file);
    }
}