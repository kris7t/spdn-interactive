package hu.bme.mit.inf.petridotnet.spdn;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kris on 3/29/17.
 */
public class AnalysisBuilder {
    private final SpdnAnalyzer analyzer;
    private final Map<Parameter, Double> parameterBindings = new HashMap<>();
    private final Map<Reward, List<Parameter>> rewardsToCalculate = new HashMap<>();

    AnalysisBuilder(SpdnAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    public AnalysisBuilder withParameter(Parameter parameter, double value) {
        parameterBindings.put(parameter, value);
        return this;
    }

    public AnalysisBuilder withReward(Reward reward, Parameter... sensitivityParameters) {
        return withReward(reward, Arrays.asList(sensitivityParameters));
    }

    public AnalysisBuilder withReward(Reward reward, List<Parameter> sensitivityParameters) {
        rewardsToCalculate.put(reward, sensitivityParameters);
        return this;
    }

    public AnalysisResult run() {
        return new AnalysisResult(analyzer.analyze(parameterBindings, rewardsToCalculate));
    }
}
