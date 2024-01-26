package pipeline.artifact.docker.cli

/**
 * Interface for configuring Docker commands.
 */
@SuppressWarnings('unused')
interface IDockerConfig {
    /**
     * Generate the Docker command configuration.
     *
     * @return The generated Docker command as a String.
     */
    String generateConfig()
}
