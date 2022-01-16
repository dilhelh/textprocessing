package io.github.iquote.text_processing.language_detection.langdetect;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Map;

/**
 * @author Konstantin Gusarov
 */
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class LangProfileDocument {
    @JsonProperty("name")
    private String name;

    @JsonProperty("n_words")
    private int[] nGramCount;

    @JsonProperty("freq")
    private Map<String, Integer> frequencies;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int[] getnGramCount() {
        return nGramCount;
    }

    public void setnGramCount(final int[] nGramCount) {
        this.nGramCount = nGramCount;
    }

    public Map<String, Integer> getFrequencies() {
        return frequencies;
    }

    public void setFrequencies(final Map<String, Integer> frequencies) {
        this.frequencies = frequencies;
    }
}
