package io.github.iquote.text_processing.language_detection.util;

/*
 * Copyright (C) 2010-2014 Cybozu Labs, 2016 Konstantin Gusarov
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

import java.util.Objects;

/**
 * {@link TagExtractor} is a class which extracts inner texts of specified tag.
 * Users don't use this class directly.
 *
 * @author Nakatani Shuyo
 * @author Konstantin Gusarov
 */
public class TagExtractor {
    final int threshold;
    final String target;
    final StringBuilder sb = new StringBuilder();

    String tag;
    private int count;

    public TagExtractor(final String tag, final int threshold) {
        this.threshold = threshold;

        target = tag;
        count = 0;
        clear();
    }

    public int count() {
        return count;
    }

    public void clear() {
        sb.setLength(0);
        tag = null;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public void add(final String line) {
        if (Objects.equals(tag, target) && line != null) {
            sb.append(line);
        }
    }

    public String closeTag() {
        String st = null;
        if (Objects.equals(tag, target) && sb.length() > threshold) {
            st = sb.toString();
            ++count;
        }

        clear();
        return st;
    }
}
