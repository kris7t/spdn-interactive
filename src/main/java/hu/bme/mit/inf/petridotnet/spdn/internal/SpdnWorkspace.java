package hu.bme.mit.inf.petridotnet.spdn.internal;

import hu.bme.mit.inf.petridotnet.spdn.SpdnAnalyzer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by kris on 3/29/17.
 */
public class SpdnWorkspace {
    private static final String ARCHIVE_FILE = "/spdn.zip";

    private File workspace;

    public SpdnWorkspace(File workspace) {
        if (workspace.exists() && !workspace.isDirectory()) {
            throw new IllegalArgumentException("workspace must be a directory");
        }
        this.workspace = workspace;
    }

    public String getWorkspacePath(String location) {
        return workspace.getAbsolutePath() + File.separator + location;
    }

    public void extract() throws IOException {
        try (ZipInputStream zipStream = new ZipInputStream(SpdnAnalyzer.class.getResourceAsStream(ARCHIVE_FILE))) {
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                try {
                    extractEntry(zipStream, entry);
                } finally {
                    zipStream.closeEntry();
                }
            }
        }
    }

    private void extractEntry(InputStream stream, ZipEntry entry) throws IOException {
        File targetFile = new File(getWorkspacePath(entry.getName()));
        long entryLastModified = entry.getLastModifiedTime().toMillis();
        if (entry.isDirectory()) {
            //noinspection ResultOfMethodCallIgnored
            targetFile.mkdirs();
        } else {
            File parent = targetFile.getParentFile();
            if (!parent.exists()) {
                //noinspection ResultOfMethodCallIgnored
                parent.mkdirs();
            }
            if (targetFile.exists()) {
                long targetLastModified = targetFile.lastModified();
                if (targetLastModified == entryLastModified) {
                    // No need to extract this file, it is already the newest version.
                    return;
                } else if (targetLastModified > entryLastModified) {
                    throw new IllegalStateException("File " + entry.getName() + " was modified in the workspace");
                } else {
                    Files.copy(stream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } else {
                Files.copy(stream, targetFile.toPath());
            }
        }
        if (!targetFile.setLastModified(entryLastModified)) {
            throw new IllegalStateException("Failed to set last modified time of " + entry.getName());
        }
    }
}
