package hu.bme.mit.inf.petridotnet.spdn;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Created by kris on 3/30/17.
 */
public class SpdnAnalyzerTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testHybridCloud() {
        Spdn spdn = new Spdn(temporaryFolder.getRoot().getAbsolutePath());
        InputStream model = SpdnAnalyzerTest.class.getResourceAsStream("/models/hybrid-cloud.pnml");
        try (SpdnAnalyzer analyzer = spdn.openModel(model, AnalysisConfiguration.DEFAULT)) {
            Parameter incRate = Parameter.ofName("incRate");
            Reward jobsProcessing = Reward.instantanenous("JobsProcessing");
            AnalysisResult result = runHybridClound(analyzer, 1);
            assertEquals(19.7500689578786, result.getValue(jobsProcessing), 1e-8);
            assertEquals(0.0624792285292783, result.getSensitivity(jobsProcessing, incRate), 1e-8);
        }
    }

    private AnalysisResult runHybridClound(SpdnAnalyzer analyzer, int i) {
        Parameter incRate = Parameter.ofName("incRate");
        Reward jobsProcessing = Reward.instantanenous("JobsProcessing");
        return analyzer.createAnalysisBuilder()
                .withParameter(incRate, 5)
                .withParameter(Parameter.ofName("p"), 0.75)
                .withParameter(Parameter.ofName("lbTime"), 1.0002)
                .withParameter(Parameter.ofName("execTime1"), 0.2)
                .withParameter(Parameter.ofName("execTime2"), 0.1)
                .withParameter(Parameter.ofName("failRate"), 0.0002)
                .withParameter(Parameter.ofName("idleFactor"), 0.1)
                .withParameter(Parameter.ofName("repairTime"), 24)
                .withParameter(Parameter.ofName("publicRent"), 0.8)
                .withParameter(Parameter.ofName("runPower"), 0.3)
                .withParameter(Parameter.ofName("idlePower"), 0.1)
                .withParameter(Parameter.ofName("repairCost"), 1000)
                .withReward(jobsProcessing, incRate)
                .run();
    }

    @Test
    public void testHybridCloudMultipleCalculations() {
        Spdn spdn = new Spdn(temporaryFolder.getRoot().getAbsolutePath());
        InputStream model = SpdnAnalyzerTest.class.getResourceAsStream("/models/hybrid-cloud.pnml");
        try (SpdnAnalyzer analyzer = spdn.openModel(model, AnalysisConfiguration.DEFAULT)) {
            Parameter incRate = Parameter.ofName("incRate");
            Reward jobsProcessing = Reward.instantanenous("JobsProcessing");

            // Ignore the result of this calculation.
            for (int i = 1; i <= 10; i++) {
                AnalysisResult result = runHybridClound(analyzer, i);
            }

            AnalysisResult result = runHybridClound(analyzer, 1);
            assertEquals(19.7500689578786, result.getValue(jobsProcessing), 1e-8);
            assertEquals(0.0624792285292783, result.getSensitivity(jobsProcessing, incRate), 1e-8);
        }
    }

    @Test
    public void testHybridCloudMultipleSpdn() {
       testHybridCloud();
       testHybridCloud();
    }
}