package org.kgusarov.textprocessing.analysis;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TransliterationServiceTest {
    private TransliterationService transliterationService;

    @Before
    public void setUp() throws Exception {
        transliterationService = TransliterationServiceFactory.create();
    }

    @After
    public void tearDown() throws Exception {
        transliterationService.shutdown();
    }

    @Test(expected = TextAnalysisException.class)
    public void testAddTransliteratorConfiguration() throws Exception {
        transliterationService.addTransliteratorConfiguration("ForTesting", "k > cjk");
        transliterationService.addTransliteratorConfiguration("ForTesting", "k > cjk");
    }
}