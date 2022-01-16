package io.github.iquote.text_processing.language_detection.util;

import java.util.Collections;

import io.github.iquote.text_processing.language_detection.util.LangProfile;

class TestLangProfile extends LangProfile {
    TestLangProfile(final int[] nGramCount) {
        super("en", Collections.emptyMap(), nGramCount);
    }

    static TestLangProfile construct(final int[] nGramCount) {
        return new TestLangProfile(nGramCount);
    }
}
