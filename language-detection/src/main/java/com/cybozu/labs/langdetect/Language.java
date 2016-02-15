package com.cybozu.labs.langdetect;

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
 * {@link Language} is to store the detected language.
 * {@link Detector#getProbabilities()} returns an {@link java.util.List} of {@link Language}s.
 *
 * @author Nakatani Shuyo
 * @author Konstantin Gusarov
 * @see Detector#getProbabilities()
 */
public class Language {
    private String language;
    private double probability;

    public Language(final String language, final double probability) {
        this.language = language;
        this.probability = probability;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(final double probability) {
        this.probability = probability;
    }

    @Override
    public String toString() {
        return "Language{" +
                "language='" + language + '\'' +
                ", probability=" + probability +
                '}';
    }
}
