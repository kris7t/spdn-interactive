package hu.bme.mit.inf.petridotnet.spdn;

import hu.bme.mit.inf.petridotnet.spdn.internal.SpdnProcess;
import hu.bme.mit.inf.petridotnet.spdn.internal.SpdnWorkspace;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by kris on 3/29/17.
 */
public class SpdnAnalyzer implements AutoCloseable {

    private static final String EXECUTABLE = "SPDN.exe";
    private static final String ERROR_OUTPUT_PREFIX = "ERROR: ";
    private static final Pattern VALID_OUTPUT = Pattern.compile("^(.+=.+)(;.+=.+)*$");

    private final Object lock = new Object();
    private final SpdnWorkspace workspace;
    private final String modelLocation;
    private final AnalysisConfiguration analysisConfiguration;

    private SpdnProcess process;

    SpdnAnalyzer(SpdnWorkspace workspace, String modelLocation, AnalysisConfiguration analysisConfiguration) {
        this.workspace = workspace;
        this.modelLocation = modelLocation;
        this.analysisConfiguration = analysisConfiguration;
    }

    public AnalysisBuilder createAnalysisBuilder() {
        return new AnalysisBuilder(this);
    }

    Map<String, Double> analyze(Map<Parameter, Double> parameterValues, Map<Reward, List<Parameter>> rewardsToCalculate) {
        synchronized (lock) {
            if (process == null || !process.isRunning()) {
                try {
                    workspace.extract();
                    process = new SpdnProcess(getExecutableLocation(), getArguments());
                } catch (IOException e) {
                    throw new RuntimeException("Error while starting SPDN", e);
                }
            }
            try {
                process.send(getParameterBindingString(parameterValues));
                process.send(getRewardsToCalculateString(rewardsToCalculate));
                return parseAnalysisResult(process.receive());
            } catch (IOException e) {
                throw new RuntimeException("Error while communicating with SPDN", e);
            }
        }
    }

    private String getExecutableLocation() {
        return workspace.getWorkspacePath(EXECUTABLE);
    }

    private List<String> getArguments() {
        return Arrays.asList(
                "reward",
                "-m",
                modelLocation,
                "-c",
                analysisConfiguration.toAbsolutePath(workspace),
                "--interactive"
        );
    }

    private String getParameterBindingString(Map<Parameter, Double> parameterBindings) {
        return parameterBindings.entrySet().stream()
                .map(entry -> entry.getKey().getName() + "=" + entry.getValue())
                .collect(Collectors.joining(";"));
    }

    private String getRewardsToCalculateString(Map<Reward, List<Parameter>> rewardsToCalculate) {
        return rewardsToCalculate.entrySet().stream()
                .map(entry -> {
                    Reward reward = entry.getKey();
                    StringBuilder sb = new StringBuilder();
                    sb.append("<").append(reward.getConfigurationName()).append(">[")
                            .append(reward.getKind().getShortName()).append(']');
                    List<Parameter> parameterList = entry.getValue();
                    if (!parameterList.isEmpty()) {
                        String parameters = entry.getValue().stream()
                                .map(Parameter::getName)
                                .collect(Collectors.joining(","));
                        sb.append("(").append(parameters).append(")");
                    }
                    return sb.toString();
                })
                .collect(Collectors.joining("|"));
    }

    private Map<String, Double> parseAnalysisResult(String response) {
        if (response.startsWith(ERROR_OUTPUT_PREFIX)) {
            throw new RuntimeException("SDPN analysis has failed: " + response);
        }
        if (!VALID_OUTPUT.matcher(response).find()) {
            throw new RuntimeException("Malformatted SPDN output: " + response);
        }
        return Arrays.stream(response.split(";"))
                .map(i -> i.split("="))
                .collect(Collectors.toMap(array -> array[0], array -> Double.parseDouble(array[1])));
    }

    @Override
    public void close() {
        if (process != null) {
            try {
                process.close();
            } catch (IOException e) {
                throw new RuntimeException("Failed to close SPDN", e);
            }
        }
    }
}
