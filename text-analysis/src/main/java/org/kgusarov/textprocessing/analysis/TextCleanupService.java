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

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * <p>This service is meant for performing typical cleanup operations: removing unwanted chars etc.</p>
 *
 * @author Konstantin Gusarov
 */
public class TextCleanupService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextCleanupService.class);
    private static final Set<Integer> TEXT_DIRECTION_CHARS = Sets.newHashSet(
            0x200b,
            0x200c,
            0x200d,
            0x200e,
            0x200f,
            0x202a,
            0x202b,
            0x202c,
            0x202d,
            0x202e
    );

    /**
     * <p>Removes Unicode characters that are responsible for changing text direction or are simply invisible
     * but may interfere in further analysis thus leading to unexpected results</p>
     * <dl>
     *     <dt>U+200B</dt>
     *     <dd>ZERO WIDTH SPACE <a href="http://www.fileformat.info/info/unicode/char/200B/index.html">200B</a></dd>
     *
     *     <dt>U+200C</dt>
     *     <dd>ZERO WIDTH NON-JOINER <a href="http://www.fileformat.info/info/unicode/char/200C/index.html">200C</a></dd>
     *
     *     <dt>U+200D</dt>
     *     <dd>ZERO WIDTH JOINER <a href="http://www.fileformat.info/info/unicode/char/200D/index.html">200D</a></dd>
     *
     *     <dt>U+200E</dt>
     *     <dd>LEFT-TO-RIGHT MARK <a href="http://www.fileformat.info/info/unicode/char/200E/index.html">200E</a></dd>
     *
     *     <dt>U+200F</dt>
     *     <dd>RIGHT-TO-LEFT MARK <a href="http://www.fileformat.info/info/unicode/char/200F/index.html">200F</a></dd>
     *
     *     <dt>U+202A</dt>
     *     <dd>LEFT-TO-RIGHT EMBEDDING <a href="http://www.fileformat.info/info/unicode/char/202A/index.html">202A</a></dd>
     *
     *     <dt>U+202B</dt>
     *     <dd>RIGHT-TO-LEFT EMBEDDING <a href="http://www.fileformat.info/info/unicode/char/202B/index.html">202B</a></dd>
     *
     *     <dt>U+202C</dt>
     *     <dd>POP DIRECTIONAL FORMATTING <a href="http://www.fileformat.info/info/unicode/char/202C/index.html">202C</a></dd>
     *
     *     <dt>U+202D</dt>
     *     <dd>LEFT-TO-RIGHT OVERRIDE <a href="http://www.fileformat.info/info/unicode/char/202D/index.html">202D</a></dd>
     *
     *     <dt>U+202E</dt>
     *     <dd>RIGHT-TO-LEFT OVERRIDE <a href="http://www.fileformat.info/info/unicode/char/202E/index.html">202E</a></dd>
     * </dl>
     *
     * @param text      Text to cleanup
     * @return          Input string without characters that change text direction or are simply invisible
     */
    public String removeDirectionAndInvisibleChars(final String text) {
        final int length = text.length();
        final StringBuilder sb = new StringBuilder(length);

        for (final char c : text.toCharArray()) {
            final int cInt = c;
            if (TEXT_DIRECTION_CHARS.contains(cInt)) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Removing {} ({})", Character.toString(c), String.format("u%04X", cInt));
                }

                continue;
            }

            sb.append(c);
        }

        return sb.toString();
    }
}
