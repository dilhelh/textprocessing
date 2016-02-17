package cukes;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(format = {"junit:target/output", "html:target/cucumber", "json:target/cucumber.json"})
@SuppressWarnings("squid:S2187")
public class CucumberTest {
}
