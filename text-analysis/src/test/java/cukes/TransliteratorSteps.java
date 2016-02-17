package cukes;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.kgusarov.textprocessing.analysis.TextAnalysisException;
import org.kgusarov.textprocessing.analysis.TransliterationService;
import org.kgusarov.textprocessing.analysis.TransliterationServiceFactory;

import static org.junit.Assert.assertEquals;

public class TransliteratorSteps {
    private TransliterationService transliterationService;
    private String transliterated;

    @Before
    public void init() {
        transliterationService = TransliterationServiceFactory.create();
    }

    @After
    public void tearDown() {
        transliterationService.shutdown();
    }

    @When("^I transliterate (.*)$")
    public void normalizeString(final String input) throws TextAnalysisException {
        transliterated = transliterationService.transliterate(input);
    }

    @Then("^Result should be (.*)$")
    public void checkNormalizedString(final String expected) throws TextAnalysisException {
        assertEquals(expected, transliterated);
    }
}
