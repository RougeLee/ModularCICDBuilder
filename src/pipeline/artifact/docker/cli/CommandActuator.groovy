package pipeline.artifact.docker.cli

import pipeline.Pipeline

class CommandActuator extends Pipeline {
    private IDockerConfig dockerCommandConfig
    private String dockerCommand

    CommandActuator(IDockerConfig dockerRunConfig) {
        this.dockerCommandConfig = dockerRunConfig
    }

    String run() {
        generateDockerCommand()
        LinkedHashMap cmdMap = [:]
        cmdMap['script'] = dockerCommand
        cmdMap['returnStdout'] = true
        String result
        try {
            result = sh(cmdMap)
        } catch (Exception e) {
            EchoStep(errorMessage(e))
            throw e
        }
        if (!result.isEmpty()) {
            EchoStep(result)
        }
        return result
    }

    private void generateDockerCommand() {
        dockerCommand = dockerCommandConfig.generateConfig()
    }

    private String errorMessage(Exception e) {
        return new StringBuilder('CommandActuator run error:\n')
                .append("Docker command content: ${dockerCommand}\n")
                .append(e.toString())
                .toString()
    }
}
