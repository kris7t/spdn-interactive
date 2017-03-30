package hu.bme.mit.inf.petridotnet.spdn;

import hu.bme.mit.inf.petridotnet.spdn.internal.SpdnWorkspace;

import java.io.File;

/**
 * Created by kris on 3/29/17.
 */
public class AnalysisConfiguration {
    private static final String CONFIG_DIRECTORY = "configs";

    public static final AnalysisConfiguration SPARSE_PARALLEL_BICGSTAB = fromBuiltin("SP_PAR_BICG.txt");
    public static final AnalysisConfiguration DEFAULT = SPARSE_PARALLEL_BICGSTAB;

    private final boolean builtin;
    private final String configurationPath;

    private AnalysisConfiguration(boolean builtin, String configurationPath) {
        this.builtin = builtin;
        this.configurationPath = configurationPath;
    }

    public static AnalysisConfiguration fromFile(String configurationPath) {
        return new AnalysisConfiguration(false, configurationPath);
    }

    private static AnalysisConfiguration fromBuiltin(String configurationPath) {
        return new AnalysisConfiguration(true, configurationPath);
    }

    String toAbsolutePath(SpdnWorkspace workspace) {
        if (builtin) {
            return workspace.getWorkspacePath(CONFIG_DIRECTORY + File.separator + configurationPath);
        } else {
            return new File(configurationPath).getAbsolutePath();
        }
    }
}
