package hu.bme.mit.inf.petridotnet.spdn;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by kris on 3/29/17.
 */
public class AnalysisResult {
    private final Map<String, Double> results;

    AnalysisResult(Map<String, Double> results) {
        this.results = results;
    }

    public double getValue(Reward reward) {
        String key = reward.getConfigurationName() + "_" + reward.getKind().getShortName();
        return getByKey(key);
    }

    public double getSensitivity(Reward reward, Parameter parameter) {
        String key = reward.getConfigurationName() + "_sens_" + parameter.getName();
        return getByKey(key);
    }

    private double getByKey(String key) {
        Double value = results.get(key);
        if (value == null) {
            throw new IllegalArgumentException("AnalysisBuilder result " + key + " was not found");
        } else {
            return value;
        }
    }

    @Override
    public String toString() {
        return "{" + results.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", ")) + "}";
    }
}
