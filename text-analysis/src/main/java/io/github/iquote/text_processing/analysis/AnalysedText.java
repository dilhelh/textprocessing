package io.github.iquote.text_processing.analysis;

/*
 * Copyright (C) 2016 Konstantin Gusarov
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

import com.google.common.collect.Lists;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;
import java.util.function.Function;

import static com.twitter.Extractor.Entity;
import static java.util.Collections.unmodifiableList;

/**
 * <p>This class stores text analysis result</p>
 * <p>It contains following information:</p>
 * <ul>
 * <li>Original text</li>
 * <li>Original transliterated text</li>
 * <li>Terms from text that were extracted with the help of {@code org.kgusarov.textprocessing.analysis.TermExtractionService}</li>
 * <li>Transliterated terms</li>
 * <li>Twitter entities</li>
 * <li>Transliterated twitter entities</li>
 * </ul>
 * <p>Entities and terms are stored as {@code java.util.List}</p>
 * <p>Texts are stored in a {@code java.lang.String} form</p>
 *
 * @author Konstantin Gusarov
 * @see io.github.iquote.text_processing.analysis.TermExtractionService
 * @see io.github.iquote.text_processing.analysis.TextCleanupService
 * @see io.github.iquote.text_processing.analysis.TransliterationService
 */
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification = "Inner collections are actually unmodifiable")
public class AnalysedText {
    private final String originalText;
    private final String transliteratedText;

    private final List<String> originalTerms;
    private final List<String> transliteratedTerms;

    private final List<AnalysedEntity> hashtags;
    private final List<AnalysedEntity> cashtags;
    private final List<AnalysedEntity> mentions;
    private final List<AnalysedEntity> urls;

    /**
     * Create instance of the {@code AnalysedText}
     *
     * @param originalText              Original text
     * @param transliteratedText        Original transliterated text
     * @param originalTerms             Terms extracted from text
     * @param transliteratedTerms       Transliterated terms extracted from text
     * @param entities                  Entities encountered in text
     * @param getEntityValue            Function that transforms entity into text
     * @param transliterateEntityValue  Function that transforms entity into text and transliterates it
     */
    public AnalysedText(final String originalText, final String transliteratedText, final List<String> originalTerms,
                        final List<String> transliteratedTerms, final List<Entity> entities,
                        final Function<Entity, String> getEntityValue,
                        final Function<Entity, String> transliterateEntityValue) {

        this.originalText = originalText;
        this.transliteratedText = transliteratedText;
        this.originalTerms = unmodifiableList(originalTerms);
        this.transliteratedTerms = unmodifiableList(transliteratedTerms);

        final List<AnalysedEntity> hashtagList = Lists.newArrayList();
        final List<AnalysedEntity> cashtagList = Lists.newArrayList();
        final List<AnalysedEntity> mentionList = Lists.newArrayList();
        final List<AnalysedEntity> urlList = Lists.newArrayList();

        for (final Entity entity : entities) {
            processEntity(entity, getEntityValue, transliterateEntityValue, hashtagList, cashtagList, mentionList, urlList);
        }

        urls = unmodifiableList(urlList);
        hashtags = unmodifiableList(hashtagList);
        cashtags = unmodifiableList(cashtagList);
        mentions = unmodifiableList(mentionList);
    }

    /**
     * Get original text that was analysed
     *
     * @return      Original text
     */
    public String getOriginalText() {
        return originalText;
    }

    /**
     * Get transliterated text
     *
     * @return      Transliterated text
     */
    public String getTransliteratedText() {
        return transliteratedText;
    }

    /**
     * Get terms extracted from the analysed text
     *
     * @return      Term list
     */
    public List<String> getOriginalTerms() {
        return originalTerms;
    }

    /**
     * Get transliterated terms extracted from the analysed text
     *
     * @return      Transliterated term list
     */
    public List<String> getTransliteratedTerms() {
        return transliteratedTerms;
    }

    /**
     * Get hashtags extracted from the analysed text
     *
     * @return      Hashtag list
     */
    public List<AnalysedEntity> getHashtags() {
        return hashtags;
    }

    /**
     * Get cashtags extracted from the analysed text
     *
     * @return      Cashtag list
     */
    public List<AnalysedEntity> getCashtags() {
        return cashtags;
    }

    /**
     * Get mentions extracted from the analysed text
     *
     * @return      Mention list
     */
    public List<AnalysedEntity> getMentions() {
        return mentions;
    }

    /**
     * Get urls extracted from the analysed text
     *
     * @return      URL list
     */
    public List<AnalysedEntity> getUrls() {
        return urls;
    }

    private static void processEntity(final Entity entity, final Function<Entity, String> getEntityValue,
                               final Function<Entity, String> transliterateEntityValue,
                               final List<AnalysedEntity> hashtagList, final List<AnalysedEntity> cashtagList,
                               final List<AnalysedEntity> mentionList, final List<AnalysedEntity> urlList) {
        final Entity.Type type = entity.getType();
        final String value = getEntityValue.apply(entity);
        final Integer start = entity.getStart();
        final Integer end = entity.getEnd();

        if (type == Entity.Type.URL) {
            final AnalysedEntity analysedEntity = new AnalysedEntity(value, null, start, end);
            urlList.add(analysedEntity);
        } else {
            final String transliterated = transliterateEntityValue.apply(entity);
            final AnalysedEntity analysedEntity = new AnalysedEntity(value, transliterated, start, end);

            switch (type) {
                case HASHTAG:
                    hashtagList.add(analysedEntity);
                    break;
                case MENTION:
                    mentionList.add(analysedEntity);
                    break;
                case CASHTAG:
                    cashtagList.add(analysedEntity);
                    break;
                default:
                    // Do nothing - this is entity unknown to us...
            }
        }
    }
}
