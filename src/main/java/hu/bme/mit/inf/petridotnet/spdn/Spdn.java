package hu.bme.mit.inf.petridotnet.spdn;

import hu.bme.mit.inf.petridotnet.spdn.internal.SpdnWorkspace;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

/**
 * Created by kris on 3/30/17.
 */
public class Spdn {
    private final SpdnWorkspace workspace;

    public Spdn(String workspaceLocation) {
        workspace = new SpdnWorkspace(new File(workspaceLocation));
    }

    public SpdnAnalyzer openModel(String modelLocation) {
        return openModel(modelLocation, AnalysisConfiguration.DEFAULT);
    }

    public SpdnAnalyzer openModel(String modelLocation, AnalysisConfiguration analysisConfiguration) {
        return new SpdnAnalyzer(workspace, modelLocation, analysisConfiguration);
    }

    public SpdnAnalyzer openModel(InputStream stream, AnalysisConfiguration analysisConfiguration) {
        File tempFile;
        try {
            do {
                String tempFileName = UUID.randomUUID().toString() + ".pnml";
                tempFile = new File(workspace.getWorkspacePath(tempFileName));
            } while (tempFile.exists());
            Files.copy(stream, tempFile.toPath());
        } catch (IOException e) {
            throw new SpdnException("Could not write PNML to workspace", e);
        }
        return openModel(tempFile.getAbsolutePath(), analysisConfiguration);
    }
}
