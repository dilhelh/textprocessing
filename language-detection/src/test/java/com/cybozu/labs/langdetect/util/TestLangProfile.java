package com.cybozu.labs.langdetect.util;

import java.util.Collections;

class TestLangProfile extends LangProfile {
    TestLangProfile(final int[] nGramCount) {
        super("en", Collections.emptyMap(), nGramCount);
    }

    static TestLangProfile construct(final int[] nGramCount) {
        return new TestLangProfile(nGramCount);
    }
}
