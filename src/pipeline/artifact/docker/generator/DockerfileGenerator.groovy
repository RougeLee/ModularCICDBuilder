package pipeline.artifact.docker.generator

import com.cloudbees.groovy.cps.NonCPS

/**
 * This class provides a mechanism to dynamically generate a Dockerfile by adding instructions
 * through method calls. Each method call appends a specific instruction to the Dockerfile content.
 * The generated Dockerfile content can be retrieved as a String which then can be used in building
 * Docker images.
 *
 * Example usage:
 * <pre>{@code
 * DockerfileGenerator generator = new DockerfileGenerator();
 * generator.invokeInstruction(DockerfileInstructionsMap.FROM, "ubuntu:18.04")
 *           .invokeInstruction(DockerfileInstructionsMap.RUN, "apt-get update && apt-get install -y git")
 *           .invokeInstruction(DockerfileInstructionsMap.CMD, "[\"/bin/bash\"]");
 * String dockerfileContent = generator.generate();
 *}</pre>
 */
@SuppressWarnings('unused')
class DockerfileGenerator {
    private static final ArrayList<EDockerfileInstructionsMap> instructions = EDockerfileInstructionsMap.values()
    private StringBuilder dockerfileContent = new StringBuilder()
    private Map<EDockerfileInstructionsMap, Closure> dockerfileMethodsMap = [:]

    /**
     * Initializes the DockerfileGenerator and sets up the instruction method map.
     */
    DockerfileGenerator() {
        initMethodMap()
    }

    DockerfileGenerator applyEnvs(Closure closure) {
        closure(this)
        return this
    }

    DockerfileGenerator comment(String comment) {
        dockerfileContent
                .append("\n# ")
                .append(comment)
                .append("\n")
        return this
    }

    DockerfileGenerator from(String image, String alias = null) {
        String fromInstruction = image
        if (alias) {
            fromInstruction += " AS $alias"
        }
        invokeInstruction(EDockerfileInstructionsMap.FROM, fromInstruction)
        return this
    }

    DockerfileGenerator workdir(String path) {
        invokeInstruction(EDockerfileInstructionsMap.WORKDIR, path)
        return this
    }

    DockerfileGenerator run(String command) {
        invokeInstruction(EDockerfileInstructionsMap.RUN, command)
        return this
    }

    DockerfileGenerator cmd(String command) {
        invokeInstruction(EDockerfileInstructionsMap.CMD, command)
        return this
    }

    DockerfileGenerator label(String key, String value) {
        String labelInstruction = "$key=$value"
        invokeInstruction(EDockerfileInstructionsMap.LABEL, labelInstruction)
        return this
    }

    DockerfileGenerator expose(String port) {
        invokeInstruction(EDockerfileInstructionsMap.EXPOSE, port)
        return this
    }

    DockerfileGenerator arg(String arg) {
        invokeInstruction(EDockerfileInstructionsMap.ARG, arg)
        return this
    }

    DockerfileGenerator env(String key, String value) {
        String envInstruction = "$key=$value"
        invokeInstruction(EDockerfileInstructionsMap.ENV, envInstruction)
        return this
    }

    DockerfileGenerator add(String src, String dest) {
        String addInstruction = "$src $dest"
        invokeInstruction(EDockerfileInstructionsMap.ADD, addInstruction)
        return this
    }

    DockerfileGenerator copy(String src, String dest) {
        String copyInstruction = "$src $dest"
        invokeInstruction(EDockerfileInstructionsMap.COPY, copyInstruction)
        return this
    }

    DockerfileGenerator entrypoint(String command) {
        invokeInstruction(EDockerfileInstructionsMap.ENTRYPOINT, command)
        return this
    }

    DockerfileGenerator volume(String volume) {
        invokeInstruction(EDockerfileInstructionsMap.VOLUME, volume)
        return this
    }

    DockerfileGenerator user(String user) {
        invokeInstruction(EDockerfileInstructionsMap.USER, user)
        return this
    }

    DockerfileGenerator healthcheck(String command) {
        invokeInstruction(EDockerfileInstructionsMap.HEALTHCHECK, command)
        return this
    }

    DockerfileGenerator onbuild(String command) {
        invokeInstruction(EDockerfileInstructionsMap.ONBUILD, command)
        return this
    }

    DockerfileGenerator stopsignal(String signal) {
        invokeInstruction(EDockerfileInstructionsMap.STOPSIGNAL, signal)
        return this
    }

    DockerfileGenerator shell(String shell) {
        invokeInstruction(EDockerfileInstructionsMap.SHELL, shell)
        return this
    }

    /**
     * Invokes a Dockerfile instruction with the provided argument. If the instruction is not supported,
     * an IllegalArgumentException is thrown.
     *
     * @param instruction The DockerfileInstructionsMap enum value representing the Docker instruction.
     * @param argument The argument for the Docker instruction, such as an image name for FROM.
     * @return The instance of DockerfileGenerator for chaining method calls.
     */
    DockerfileGenerator invokeInstruction(EDockerfileInstructionsMap instruction, String argument) {
        if (argument == null || argument.trim().isEmpty()) {
            throw new IllegalArgumentException("Argument for instruction ${instruction.name()} cannot be null or empty")
        }
        Closure method = dockerfileMethodsMap[instruction]
        if (method == null) {
            throw new IllegalArgumentException("Unsupported instruction: ${instruction.name()}")
        }
        method.call(argument)
        return this
    }

    /**
     * Generates the final content of the Dockerfile as a String.
     *
     * @return The complete content of the Dockerfile.
     */
    String generate() {
        return dockerfileContent.toString()
    }

    /**
     * Non-CPS method to initialize the map of Dockerfile instructions to their respective closures.
     * Each closure appends the correct Dockerfile instruction and argument to the content.
     */
    @NonCPS
    private void initMethodMap() {
        instructions.each { EDockerfileInstructionsMap instructionEnum ->
            dockerfileMethodsMap[instructionEnum] = { String argument ->
                dockerfileContent
                        .append(instructionEnum.getInstruction())
                        .append(' ')
                        .append(argument)
                        .append('\n')
                return this
            }
        }
    }
}
