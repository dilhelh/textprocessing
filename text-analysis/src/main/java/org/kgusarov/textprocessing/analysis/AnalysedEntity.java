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

/**
 * <p>This class represents analyzed Twitter entity such as hashtag, cashtag, mention or url</p>
 * <p>It only stores original and transliterated values</p>
 *
 * @author Konstantin Gusarov
 * @see org.kgusarov.textprocessing.analysis.TermExtractionService
 * @see org.kgusarov.textprocessing.analysis.TransliterationService
 */
public class AnalysedEntity {
    private final String originalValue;
    private final String transliteratedValue;

    /**
     * Create new {@code AnalysedEntity} instance
     *
     * @param originalValue         Original entity text
     * @param transliteratedValue   Transliterated entity text
     */
    public AnalysedEntity(final String originalValue, final String transliteratedValue) {
        this.originalValue = originalValue;
        this.transliteratedValue = transliteratedValue;
    }

    /**
     * Get original value
     *
     * @return                      Original entity text
     */
    public String getOriginalValue() {
        return originalValue;
    }

    /**
     * Get transliterated value
     *
     * @return                      Transliterated entity text
     */
    public String getTransliteratedValue() {
        return transliteratedValue;
    }
}
