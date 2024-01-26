package pipeline.artifact.docker.cli.pull

import pipeline.artifact.docker.cli.AbstractDockerConfig

/**
 * Class for configuring Docker pull command.
 */
@SuppressWarnings('unused')
class PullConfig extends AbstractDockerConfig {
    private static final String DOCKER_PULL = 'docker pull '
    private String imageName
    private String tag

    /**
     * Constructor to set the image name.
     *
     * @param imageName The name of the Docker image to pull.
     */
    PullConfig(String imageName) {
        this.imageName = imageName
    }

    /**
     * Set the tag of the Docker image.
     *
     * @param tag The tag of the Docker image.
     * @return The DockerPullConfig instance for chaining.
     */
    PullConfig setTag(String tag) {
        validateParameters('tag', tag)
        this.tag = tag
        return this
    }

    String generateConfig() {
        validateParameters('imageName', imageName)
        def dockerPullConfig = new StringBuffer()
        dockerPullConfig.append(DOCKER_PULL)
        dockerPullConfig.append(imageName)
        if (tag) {
            dockerPullConfig.append(":${tag}")
        }
        return dockerPullConfig.toString()
    }
}
