package pipeline.artifact.docker.cli.push

import pipeline.artifact.docker.cli.AbstractDockerConfig

/**
 * Class for configuring Docker push command.
 */
@SuppressWarnings('unused')
class PushConfig extends AbstractDockerConfig {
    private static final String DOCKER_PUSH = 'docker push '
    private String imageName
    private String tag

    /**
     * Constructor to set the image name.
     *
     * @param imageName The name of the Docker image to push.
     */
    PushConfig(String imageName) {
        this.imageName = imageName
    }

    /**
     * Set the tag of the Docker image.
     *
     * @param tag The tag of the Docker image.
     * @return The DockerPushConfig instance for chaining.
     */
    PushConfig setTag(String tag) {
        validateParameters('tag', tag)
        this.tag = tag
        return this
    }

    String generateConfig() {
        validateParameters('imageName', imageName)
        def dockerPushConfig = new StringBuffer()
        dockerPushConfig.append(DOCKER_PUSH)
        dockerPushConfig.append(imageName)
        if (tag) {
            dockerPushConfig.append(":${tag}")
        }
        return dockerPushConfig.toString()
    }
}
