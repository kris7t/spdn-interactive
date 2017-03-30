package hu.bme.mit.inf.petridotnet.spdn.internal;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by kris on 3/29/17.
 */
public class SpdnProcess implements Closeable {
    private static final String MONO_COMMAND = "mono";
    private static final String SUCCESS_MESSAGE = "OK";
    private static final long EXIT_TIMEOUT_MS = 100;

    private Process process;
    private PrintStream standardInput;
    private BufferedReader standardOutput;

    public SpdnProcess(String executablePath, List<String> args) throws IOException {
        List<String> commandLine = new ArrayList<>(args.size() + 2);
        commandLine.add(executablePath);
        commandLine.addAll(args);
        Process startedProcess;
        try {
            startedProcess = new ProcessBuilder().command(commandLine).start();
        } catch (IOException e) {
            commandLine.add(0, MONO_COMMAND);
            startedProcess = new ProcessBuilder().command(commandLine).start();
        }
        process = startedProcess;
        standardInput = new PrintStream(process.getOutputStream());
        standardOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        receiveOk();
    }

    public boolean isRunning() {
        return process.isAlive();
    }

    public void send(String message) throws IOException {
        standardInput.println(message);
        standardInput.flush();
        receiveOk();
    }

    public String receive() throws IOException {
        String line = standardOutput.readLine();
        if (line == null) {
            // CLR/Mono might have an exception on the error stream.
            throw new RuntimeException("SPDN unexpectedly closed output stream, standard error: "
                    + readStandardError());
        }
        // Trim excess whitespace (possible '\r' under Windows).
        return line.trim();
    }

    private void receiveOk() throws IOException {
        String response = receive();
        if (!response.equals(SUCCESS_MESSAGE)) {
            throw new RuntimeException("Response from SPDN.exe: " + response);
        }
    }

    private String readStandardError() throws IOException {
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            return errorReader.lines().collect(Collectors.joining("\n"));
        }
    }

    @Override
    public void close() throws IOException {
        if (isRunning()) {
            standardInput.println("END");
            standardInput.flush();
            try {
                if (!process.waitFor(EXIT_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                    process.destroy();
                }
            } catch (InterruptedException e) {
                process.destroy();
            }
            process = null;
        }
        if (standardInput != null) {
            standardInput.close();
            standardInput = null;
        }
        if (standardOutput != null) {
            standardOutput.close();
            standardOutput = null;
        }
    }
}
