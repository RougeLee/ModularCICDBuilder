package pipeline.artifact.docker.cli.compose

import pipeline.artifact.docker.cli.AbstractDockerConfig

/**
 * Class for configuring Docker Compose CLI command.
 * This class provides methods to configure various parameters for Docker Compose command.
 */
@SuppressWarnings('unused')
class ComposeConfig extends AbstractDockerConfig {
    private static final String DOCKER_COMPOSE = 'docker compose '
    private String projectDirectory
    private String projectName
    private List<String> files = []
    private List<String> profiles = []
    private String compatibility
    private String dryRun
    private String envFile
    private String progress
    private String ansi
    private String parallel
    private ComposeCommandConfig commandConfig

    /**
     * Constructor to set command configuration.
     */
    ComposeConfig(ComposeCommandConfig commandConfig) {
        this.commandConfig = commandConfig
    }

    /**
     * Set the project directory for the Docker Compose command.
     * Overrides the default base path for relative paths in the Compose files.
     */
    ComposeConfig setProjectDirectory(String projectDirectory) {
        this.projectDirectory = projectDirectory
        return this
    }

    /**
     * Set the project name for the Docker Compose command.
     * This name is used as a prefix for resources created by Compose.
     */
    ComposeConfig setProjectName(String projectName) {
        this.projectName = projectName
        return this
    }

    /**
     * Add a Compose file to the configuration.
     * Multiple files can be specified for multi-file configurations.
     */
    ComposeConfig addFile(String file) {
        this.files.add(file)
        return this
    }

    /**
     * Add a profile to the Docker Compose configuration.
     * Profiles are used to enable or disable services defined in Compose files.
     */
    ComposeConfig addProfile(String profile) {
        this.profiles.add(profile)
        return this
    }

    /**
     * Enable backward compatibility mode for the Docker Compose command.
     */
    ComposeConfig setCompatibility(String compatibility) {
        this.compatibility = compatibility
        return this
    }

    /**
     * Set the Docker Compose command to dry run mode.
     * In this mode, the command is tested without affecting the application stack state.
     */
    ComposeConfig setDryRun(String dryRun) {
        this.dryRun = dryRun
        return this
    }

    /**
     * Specify an alternate environment file for the Docker Compose command.
     */
    ComposeConfig setEnvFile(String envFile) {
        this.envFile = envFile
        return this
    }

    /**
     * Set the type of progress output for the Docker Compose command.
     */
    ComposeConfig setProgress(String progress) {
        this.progress = progress
        return this
    }

    /**
     * Control when to print ANSI control characters in the Docker Compose command output.
     */
    ComposeConfig setAnsi(String ansi) {
        this.ansi = ansi
        return this
    }

    /**
     * Set the maximum level of parallelism for concurrent operations in the Docker Compose command.
     */
    ComposeConfig setParallel(String parallel) {
        this.parallel = parallel
        return this
    }

    /**
     * Generate the configuration string for the Docker Compose command.
     * This method builds the complete Docker Compose command based on the set configuration.
     */
    String generateConfig() {
        validateParameters('commandConfig', commandConfig.command)
        def dockerComposeSB = new StringBuilder()
        dockerComposeSB.append(DOCKER_COMPOSE)
        if (projectDirectory) {
            dockerComposeSB.append("--project-directory ${projectDirectory} ")
        }
        if (projectName) {
            dockerComposeSB.append("-p ${projectName} ")
        }
        files.each { String file ->
            dockerComposeSB.append("-f ${file} ")
        }
        profiles.each { String profile ->
            dockerComposeSB.append("--profile ${profile} ")
        }
        if (compatibility) {
            dockerComposeSB.append("--compatibility ${compatibility} ")
        }
        if (dryRun) {
            dockerComposeSB.append("--dry-run ${dryRun} ")
        }
        if (envFile) {
            dockerComposeSB.append("--env-file ${envFile} ")
        }
        if (progress) {
            dockerComposeSB.append("--progress ${progress} ")
        }
        if (ansi) {
            dockerComposeSB.append("--ansi ${ansi} ")
        }
        if (parallel) {
            dockerComposeSB.append("--parallel ${parallel} ")
        }
        dockerComposeSB.append(commandConfig.command)
        commandConfig.getOptions().each { String option ->
            dockerComposeSB.append(' ').append(option)
        }
        return dockerComposeSB.toString()
    }
}
