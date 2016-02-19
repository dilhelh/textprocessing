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
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import static com.twitter.Extractor.Entity;

/**
 * <p>This service performs analysis of the text.</p>
 * <p>It utilizes other service found in this module to produce
 * {@code org.kgusarov.textprocessing.analysis.AnalysedText}</p>
 *
 * @author Konstantin Gusarov
 */
public class TextAnalysisService {
    private final TermExtractionService termExtractionService;
    private final TextCleanupService textCleanupService;
    private final TransliterationService transliterationService;

    /**
     * Create new instance of {@code TextAnalysisService}
     *
     * @param termExtractionService     Term extraction service to be used
     * @param textCleanupService        Text cleanup service to be used
     * @param transliterationService    Transliteration service to be used
     */
    public TextAnalysisService(final TermExtractionService termExtractionService,
                               final TextCleanupService textCleanupService,
                               final TransliterationService transliterationService) {

        this.termExtractionService = termExtractionService;
        this.textCleanupService = textCleanupService;
        this.transliterationService = transliterationService;
    }

    /**
     * Performs text analysis for the given input
     *
     * @param text          Text to be analysed
     * @param cleanup       Should text direction and invisible space symbols be removed from text before analysis
     * @return              Text analysis result
     */
    public AnalysedText analyse(final String text, final boolean cleanup) {
        final String input = cleanup? textCleanupService.removeDirectionAndInvisibleChars(text) : text;

        final Pair<String, List<Entity>> textAndEntities = textCleanupService.extractTwitterEntities(input, false);
        final String cleanedUp = textAndEntities.getLeft();
        final List<Entity> entities = textAndEntities.getRight();

        final List<String> terms = termExtractionService.getTerms(cleanedUp);
        final List<String> transliteratedTerms = Lists.newArrayList();

        terms.stream()
                .map(transliterationService::transliterate)
                .forEach(transliteratedTerms::add);

        final String transliterated = transliterationService.transliterate(input);

        return new AnalysedText(input, transliterated, terms, transliteratedTerms, entities,
                this::getEntityValue, this::transliterateEntityValue);
    }

    private String getEntityValue(final Entity entity) {
        final String value = entity.getValue();
        return value.toLowerCase();
    }

    private String transliterateEntityValue(final Entity entity) {
        final String value = entity.getValue();
        return transliterationService.transliterate(value);
    }
}
