package com.github.dilhelh.text_processing.analysis;

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

import com.google.common.collect.Sets;
import com.ibm.icu.text.Transliterator;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static com.ibm.icu.text.Transliterator.*;

/**
 * <p>This service is meant for performing transliteration operations using provided rules</p>
 *
 * @author Konstantin Gusarov
 */
public class TransliterationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransliterationService.class);

    private final ObjectPool<Transliterator> transliteratorPool;
    private final Set<String> registeredTransliterators = Sets.newHashSet();

    /**
     * Create new instance
     *
     * @param transliteratorConfig  String defining transliterators that will be applied
     *                              for example <i>Any-Lower; Any-Latin</i>
     * @param poolConfig            Configuration for the {@code Transliterator} pool
     * @see Transliterator
     */
    public TransliterationService(final String transliteratorConfig, final GenericObjectPoolConfig poolConfig) {
        final TransliteratorFactory factory = new TransliteratorFactory(transliteratorConfig);
        transliteratorPool = new GenericObjectPool<>(factory, poolConfig);
    }

    /**
     * <p>Registers new named transliterator</p>
     *
     * @param name                      Transliterator name
     * @param rules                     Rules for the transliteration. For example: <i>^я > ja; ^е > je; ^ю > ju;</i>
     * @throws TextAnalysisException    In case transliterator with given name is already registered
     *
     * @see Transliterator#registerInstance(Transliterator)
     * @see Transliterator#createFromRules(String, String, int)
     */
    public void addTransliteratorConfiguration(final String name, final String rules) {
        try {
            final Transliterator alreadyRegistered = getInstance(name);
            if (alreadyRegistered != null) {
                throw new TextAnalysisException("Transliterator `" + name + "` is alreaady registered");
            }
        } catch (final IllegalArgumentException ignored) {
            // This is a valid case - if there is no such transliterator registered
            // Exception is being thrown
        }

        registerInstance(createFromRules(name, rules, FORWARD));
        registeredTransliterators.add(name);
    }

    /**
     * <p>Closes the transliterator pool and prepares for the destruction</p>
     * <p>This method should be called when {@code TransliterationService} instance is not needed anymore.
     * <b>NOTE: this method will unregister all the named transliterators associated with this service</b></p>
     */
    public void shutdown() {
        transliteratorPool.close();
        registeredTransliterators.forEach(Transliterator::unregister);
    }

    /**
     * Transliterate given string
     *
     * @param input         String to be transliterated
     * @return              Transliterated string
     * @throws TextAnalysisException    Wraps original throwable
     */
    public String transliterate(final String input) {
        Transliterator transliterator = null;

        try {
            transliterator = transliteratorPool.borrowObject();
            return transliterate(input, transliterator);
        } catch (final Exception e) {
            throw new TextAnalysisException("Failed to transliterate string", e);
        } finally {
            returnToPool(transliterator);
        }
    }

    private String transliterate(final String input, final Transliterator transliterator) {
        // Transliterate to Latin
        final String result = transliterator.transliterate(input);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Transliteration ({}) = {}", input, result);
        }

        return result;
    }

    private void returnToPool(final Transliterator transliterator) {
        if (transliterator != null) {
            try {
                transliteratorPool.returnObject(transliterator);
            } catch (final Exception e) {
                LOGGER.error("Failed to return transliterator to pool", e);
            }
        }
    }
}
