package io.github.iquote.text_processing.language_detection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.iquote.text_processing.language_detection.langdetect.LangProfileDocument;
import io.github.iquote.text_processing.language_detection.util.LangProfile;
import io.github.iquote.text_processing.language_detection.util.TagExtractor;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import static javax.xml.stream.XMLStreamConstants.*;

/**
 * Load Wikipedia's abstract XML as corpus and
 * generate its language profile in JSON format.
 *
 * @author Nakatani Shuyo
 * @author Konstantin Gusarov
 */
public class LanguageProfileGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageProfileGenerator.class);
    private static final int TAG_THRESHOLD = 100;

    /**
     * Load Wikipedia abstract database file and generate its language profile
     *
     * @param lang                  Target language name
     * @param is                    Training data source
     * @param isGzip                Determines if training data is compressed
     * @return                      Language profile document instance
     * @throws LangDetectException  In case it was impossible to read training database
     */
    public LangProfileDocument loadFromWikipediaAbstract(final String lang, final InputStream is,
                                                         final boolean isGzip) throws LangDetectException {
        final LangProfile profile = new LangProfile(lang);
        final XMLInputFactory factory = XMLInputFactory.newInstance();

        try (
                final InputStream s = wrapIfNeeded(is, isGzip);
                final InputStreamReader isr = new InputStreamReader(s, StandardCharsets.UTF_8);
                final BufferedReader br = new BufferedReader(isr)
        ) {
            final XMLStreamReader reader = factory.createXMLStreamReader(br);
            final TagExtractor tagExtractor = new TagExtractor("abstract", TAG_THRESHOLD);

            while (reader.hasNext()) {
                final int next = reader.next();

                processElement(profile, reader, tagExtractor, next);
            }

            final int count = tagExtractor.count();
            LOGGER.debug(lang + ':' + count);
        } catch (final IOException e) {
            throw new LangDetectException(ErrorCode.CANNOT_OPEN_TRAIN_DATA, "Cannot open training database", e);
        } catch (final XMLStreamException e) {
            throw new LangDetectException(ErrorCode.TRAIN_DATA_FORMAT, "Training database is an invalid XML", e);
        }

        return profile.toDocument();
    }

    @SuppressWarnings({"resource", "IOResourceOpenedButNotSafelyClosed"})
    private static InputStream wrapIfNeeded(final InputStream is, final boolean isGzip) throws IOException {
        return isGzip ? new GZIPInputStream(is) : is;
    }

    private void processElement(final LangProfile profile, final XMLStreamReader reader,
                                final TagExtractor tagExtractor, final int next) {
        if (next == START_ELEMENT) {
            final String tag = reader.getName().toString();
            tagExtractor.setTag(tag);
        } else if (next == CHARACTERS) {
            final String tagText = reader.getText();
            tagExtractor.add(tagText);
        } else if (next == END_ELEMENT) {
            final String text = tagExtractor.closeTag();
            if (text != null) {
                profile.update(text);
            }
        }
    }

    /**
     * Load text file with UTF-8 and generate its language profile
     *
     * @param lang                      Target language name
     * @param is                        Training data source
     * @return                          Language profile document instance
     * @throws LangDetectException      In case it was impossible to read training database
     */
    public LangProfileDocument loadFromText(final String lang, final InputStream is) throws LangDetectException {
        final LangProfile profile = new LangProfile(lang);

        try (
                final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                final BufferedReader br = new BufferedReader(isr)
        ) {
            int count = 0;
            while (br.ready()) {
                final String line = br.readLine();
                profile.update(line);
                count++;
            }

            LOGGER.debug(lang + ':' + count);
        } catch (final IOException e) {
            throw new LangDetectException(ErrorCode.CANNOT_OPEN_TRAIN_DATA, "Cannot open training database ", e);
        }

        return profile.toDocument();
    }
}
