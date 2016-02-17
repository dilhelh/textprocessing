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

import com.ibm.icu.text.Transliterator;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import static com.ibm.icu.text.Transliterator.getInstance;

/**
 * <p>Pooled {@code com.ibm.icu.text.Transliterator} factory for using in
 * conjuction with {@code org.apache.commons.pool2.ObjectPool}</p>
 *
 * @author Konstantin Gusarov
 */
final class TransliteratorFactory implements PooledObjectFactory<Transliterator> {
    private final String transliteratorConfig;

    /**
     * Create new pooled {@code com.ibm.icu.text.Transliterator} factory
     *
     * @param transliteratorConfig  String defining transliterators that will be applied
     *                              for example <i>Any-Lower; Any-Latin</i>
     * @see com.ibm.icu.text.Transliterator
     */
    TransliteratorFactory(final String transliteratorConfig) {
        this.transliteratorConfig = transliteratorConfig;
    }

    @Override
    public PooledObject<Transliterator> makeObject() throws Exception {
        final Transliterator instance = getInstance(transliteratorConfig);
        return new DefaultPooledObject<>(instance);
    }

    @Override
    public void destroyObject(final PooledObject<Transliterator> p) throws Exception {
        // No additional actions required
    }

    @Override
    public boolean validateObject(final PooledObject<Transliterator> p) {
        return true;
    }

    @Override
    public void activateObject(final PooledObject<Transliterator> p) throws Exception {
        // No additional actions required
    }

    @Override
    public void passivateObject(final PooledObject<Transliterator> p) throws Exception {
        // No additional actions required
    }
}