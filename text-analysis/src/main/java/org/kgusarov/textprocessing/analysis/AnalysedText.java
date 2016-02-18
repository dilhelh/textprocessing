package org.kgusarov.textprocessing.analysis;

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

import java.util.List;
import java.util.function.Function;

import static com.twitter.Extractor.Entity;

/**
 * <p>This class stores text analysis result</p>
 * <p>It contains following information:</p>
 * <ul>
 * <li>Original text with no text direction symbols or invisible spaces</li>
 * <li>Original transliterated text with no text direction symbols or invisible spaces</li>
 * <li>Terms from text that were extracted with the help of {@code org.kgusarov.textprocessing.analysis.TermExtractionService}</li>
 * <li>Transliterated terms</li>
 * <li>Twitter entities</li>
 * <li>Transliterated twitter entities</li>
 * </ul>
 * <p>Entities and terms are stored as {@code java.util.List}</p>
 * <p>Texts are stored in a {@code java.lang.String} form</p>
 *
 * @author Konstantin Gusarov
 * @see org.kgusarov.textprocessing.analysis.TermExtractionService
 * @see org.kgusarov.textprocessing.analysis.TextCleanupService
 * @see org.kgusarov.textprocessing.analysis.TransliterationService
 */
public class AnalysedText {
    private final String originalText;
    private final String transliteratedText;

    private final List<String> originalTerms;
    private final List<String> transliteratedTerms;

    /*private final List<AnalysedEntity> hashtags;
    private final List<AnalysedEntity> cashtags;
    private final List<AnalysedEntity> mentions;

    // Transliterated URLs make no sense...
    private final List<String> urls;*/

    /**
     * Create instance of the {@code AnalysedText}
     *
     * @param originalText              Original text with no text direction symbols or invisible spaces
     * @param transliteratedText        Original transliterated text with no text direction symbols or invisible spaces
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
        this.originalTerms = originalTerms;
        this.transliteratedTerms = transliteratedTerms;

        final List<AnalysedEntity> hashtagList = Lists.newArrayList();
        final List<AnalysedEntity> cashtagList = Lists.newArrayList();
        final List<AnalysedEntity> mentionList = Lists.newArrayList();
        final List<String> urlList = Lists.newArrayList();

        for (final Entity entity : entities) {

        }

    }
}
