package com.github.dilhelh.text_processing.analysis;

import com.github.iquote.text_processing.language_detection.Detector;
import com.github.iquote.text_processing.language_detection.DetectorFactory;
import com.github.iquote.text_processing.language_detection.LangDetectException;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bg.BulgarianAnalyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.ca.CatalanAnalyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.da.DanishAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.el.GreekAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.analysis.fi.FinnishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.hi.HindiAnalyzer;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.lv.LatvianAnalyzer;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;
import org.apache.lucene.analysis.th.ThaiAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tr.TurkishAnalyzer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter.GENERATE_WORD_PARTS;
import static org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter.PRESERVE_ORIGINAL;

/**
 * <p>This service is meant for term extraction from arbitary text by using {@code com.cybozu.labs.langdetect.Detector}
 * and {@code org.apache.lucene.analysis.Analyzer}</p>
 * <p>It performs multiple actions in the following order:</p>
 * <ul>
 *     <li>Tries to identify the language text is written in by using {@code com.cybozu.labs.langdetect.Detector}</li>
 *     <li>
 *          In case language was successfully detected and appropriate {@code org.apache.lucene.analysis.Analyzer}
 *          can be found it is being used to produce term stream that is considered result of the term extraction
 *     </li>
 *     <li>
 *          Otherwise {@code org.apache.lucene.analysis.standard.StandardAnalyzer} is being used for term stream production
 *     </li>
 * </ul>
 *
 * @author Konstantin Gusarov
 */
public class TermExtractionService {
    private static final Map<String, Supplier<Analyzer>> ANALYZERS_BY_LANGUAGE = Maps.newHashMap();
    static {
        ANALYZERS_BY_LANGUAGE.put("ara", ArabicAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("bul", BulgarianAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("por", BrazilianAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("cat", CatalanAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("ces", CzechAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("dan", DanishAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("deu", GermanAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("ell", GreekAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("eng", EnglishAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("spa", SpanishAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("fas", PersianAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("fin", FinnishAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("fra", FrenchAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("hin", HindiAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("hun", HungarianAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("ind", IndonesianAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("ita", ItalianAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("lav", LatvianAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("nld", DutchAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("nor", NorwegianAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("por", PortugueseAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("ron", RomanianAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("rus", RussianAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("swe", SwedishAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("tha", ThaiAnalyzer::new);
        ANALYZERS_BY_LANGUAGE.put("tur", TurkishAnalyzer::new);
    }

    private final DetectorFactory detectorFactory;
    private final Map<String, Supplier<Analyzer>> analyzers;

    /**
     * Create new {@code TermExtractionService}
     *
     * @param shortMessages             Should this term extraction service use short message profiles
     * @throws TextAnalysisException    In case language {@code com.cybozu.labs.langdetect.DetectorFactory} fails to initialize
     */
    public TermExtractionService(final boolean shortMessages) {
        try {
            detectorFactory = new DetectorFactory(shortMessages);
            analyzers = initializeAnalyzers();
        } catch (final LangDetectException e) {
            throw new TextAnalysisException("Failed to create language detector factory", e);
        }
    }

    /**
     * Extracts terms from the given text
     *
     * @param text                      Text to extract terms from
     * @return                          List of extracted terms
     * @throws TextAnalysisException    In case term extraction failed which can be due to language detection or analysis
     *                                  itself
     */
    public List<String> getTerms(final String text) {
        final String language;

        try {
            final Detector detector = detectorFactory.create();
            detector.append(text);

            language = detector.detect();
        } catch (final LangDetectException e) {
            throw new TextAnalysisException("Failed to detect language for " + text, e);
        }

        final Supplier<Analyzer> analyzerSupplier = analyzers.getOrDefault(language, StandardAnalyzer::new);
        return tokenizeString(analyzerSupplier, text);
    }

    private static List<String> tokenizeString(final Supplier<Analyzer> analyzerSupplier, final String string) {
        final List<String> result = Lists.newArrayList();

        try (
                final Reader sr = new StringReader(string);
                final Analyzer analyzer = analyzerSupplier.get();
                final TokenStream s = analyzer.tokenStream(null, sr);
                final TokenStream stream = new WordDelimiterFilter(s, GENERATE_WORD_PARTS | PRESERVE_ORIGINAL, null);
        ) {
            stream.reset();
            while (stream.incrementToken()) {
                final CharTermAttribute attr = stream.getAttribute(CharTermAttribute.class);
                final String term = attr.toString();

                result.add(term);
            }
        } catch (final IOException e) {
            throw new TextAnalysisException("Failed to tokenize string: " + string, e);
        }

        return result;
    }


    private Map<String, Supplier<Analyzer>> initializeAnalyzers() {
        final Map<String, Supplier<Analyzer>> result = Maps.newHashMap();
        final List<String> languages = detectorFactory.getLangList();

        for (final String language : languages) {
            final Supplier<Analyzer> analyzer = ANALYZERS_BY_LANGUAGE.getOrDefault(language, StandardAnalyzer::new);
            result.put(language, analyzer);
        }

        return Collections.unmodifiableMap(result);
    }
}
