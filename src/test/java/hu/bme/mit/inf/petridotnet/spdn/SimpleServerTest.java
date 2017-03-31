package hu.bme.mit.inf.petridotnet.spdn;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.InputStream;

/**
 * Created by kris on 3/31/17.
 */
public class SimpleServerTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void simpleServerTest() {
        Spdn spdn = new Spdn(temporaryFolder.getRoot().getAbsolutePath());
        InputStream model = SimpleServerTest.class.getResourceAsStream("/models/simple-server.pnml");
        try (SpdnAnalyzer analyzer = spdn.openModel(model, AnalysisConfiguration.DEFAULT)) {
            Parameter requestRate = Parameter.ofName("requestRate");
            Parameter serviceTime = Parameter.ofName("serviceTime");
            Reward idle = Reward.instantaneous("Idle");
            Reward servedRequests = Reward.instantaneous("ServedRequests");
            AnalysisResult result = analyzer.createAnalysisBuilder()
                    .withParameter(requestRate, 1.5)
                    .withParameter(serviceTime, 0.25)
                    .withReward(idle, requestRate, serviceTime)
                    .withReward(servedRequests, requestRate, serviceTime)
                    .run();
            result.getValue(idle);
            result.getSensitivity(idle, requestRate);
            
            System.out.println(result);
        }
    }
}
