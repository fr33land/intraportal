package org.intraportal.api.service.shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service("CommandProcessHandler")
public class CommandProcessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CommandProcessHandler.class);

    private final File scriptLocationDirectory;

    public CommandProcessHandler(@Value("${shell.script-location}") String scriptLocation) {
        scriptLocationDirectory = new File(scriptLocation);
    }

    public void executeSingleCommand(Function<Exception, Exception> handleException, String... command) throws Exception {
        logger.info("in \"{}\" will execute single command: {}", scriptLocationDirectory, String.join(", ", command));

        try {
            Process process = new ProcessBuilder(command)
                    .directory(scriptLocationDirectory)
                    .start();
            var outputLines = readCommandProcessOutput(process.getInputStream());
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                outputLines.add(String.format("Error detected. Exit status: %s", process.exitValue()));
                var singleMessageOutput = joinOutputLines(outputLines);
                handleException.apply(new Exception(singleMessageOutput));
            }
        } catch (IOException ioException) {
            logger.error("Error executing [command={}]: {}", String.join(", ", command), ioException.getMessage());
            throw handleException.apply(ioException);
        }
    }

    public List<String> executeSingleCommandWithOutput(Function<Exception, Exception> handleException, String... command) throws Exception {
        logger.info("in \"{}\" will execute single command with output: {}", scriptLocationDirectory, String.join(", ", command));

        try {
            Process process = new ProcessBuilder(command)
                    .directory(scriptLocationDirectory)
                    .start();
            var outputLines = readCommandProcessOutput(process.getInputStream());
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                outputLines.add(String.format("Error detected. Exit status: %s", process.exitValue()));
                var singleMessageOutput = joinOutputLines(outputLines);
                handleException.apply(new Exception(singleMessageOutput));
            }

            return outputLines;
        } catch (IOException ioException) {
            logger.error("Error executing [command={}]: {}", String.join(", ", command), ioException.getMessage());
            throw handleException.apply(ioException);
        }
    }

    public List<String> executePipelineCommandWithOutput(Function<Exception, Exception> handleException, List<String> mainCommand, List<String> pipedCommand) throws Exception {
        var mainCommandArray = mainCommand.toArray(String[]::new);
        var pipedCommandArray = pipedCommand.toArray(String[]::new);
        logger.debug("will execute piped command set with output. Main command = {}. Piped command = {}", mainCommandArray, pipedCommandArray);

        try {
            var processBuilders = List.of(
                    new ProcessBuilder(mainCommandArray),
                    new ProcessBuilder(pipedCommandArray));
            var processes = ProcessBuilder.startPipeline(processBuilders);
            var lastProcess = processes.get(processes.size() - 1);
            var outputLines = readCommandProcessOutput(lastProcess.getInputStream());
            int exitCode = lastProcess.waitFor();

            if (exitCode != 0) {
                outputLines.add(String.format("Error detected. Exit status: %s", lastProcess.exitValue()));
                var singleMessageOutput = joinOutputLines(outputLines);
                handleException.apply(new Exception(singleMessageOutput));
            }

            return outputLines;
        } catch (IOException ioException) {
            logger.error("Error during execution of: Main command = {}. Piped command = {}. Error: {}",
                    String.join(", ", mainCommandArray), String.join(", ", pipedCommandArray), ioException.getMessage());
            throw handleException.apply(ioException);
        }
    }

    private List<String> readCommandProcessOutput(InputStream inputStream) {
        List<String> outputLines = new ArrayList<>();

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));

        String line;
        while (true) {
            try {
                if (!((line = bufferedReader.readLine()) != null)) break;
                outputLines.add(line);
            } catch (IOException ioException) {
                logger.error("Error while reading Command Process output stream: {}", ioException.getMessage());
            }
        }

        return outputLines;
    }

    private String joinOutputLines(List<String> outputLines) {
        return String.join(System.lineSeparator(), outputLines);
    }

}
