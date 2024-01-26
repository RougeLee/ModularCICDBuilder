package pipeline.artifact.docker.cli.build

import pipeline.artifact.docker.cli.AbstractDockerConfig

/**
 * Class for configuring Docker build command.
 */
@SuppressWarnings('unused')
class BuildConfig extends AbstractDockerConfig {
    private static final String DOCKER_BUILD = 'docker build '
    private String dockerfilePath = '.'
    private String buildContext = '.'
    private String imageName
    private List<String> tags = []
    private Map<String, String> buildArgs = [:]

    BuildConfig(String imageName) {
        this.imageName = imageName
    }

    /**
     * Set the image name for the build.
     *
     * @param imageName The name of the image.
     * @return The BuildConfig instance for chaining.
     */
    BuildConfig setImageName(String imageName) {
        validateParameters('imageName', imageName)
        this.imageName = imageName
        return this
    }

    /**
     * Add a tag for the image.
     *
     * @param tag The tag to add for the image.
     * @return The BuildConfig instance for chaining.
     */
    BuildConfig addTag(String tag) {
        validateParameters('tag', tag)
        this.tags << tag
        return this
    }

    /**
     * Set the path of the Dockerfile.
     *
     * @param dockerfilePath The Dockerfile path.
     * @return The BuildConfig instance for chaining.
     */
    BuildConfig setDockerfilePath(String dockerfilePath) {
        validateParameters('dockerfilePath', dockerfilePath)
        this.dockerfilePath = dockerfilePath
        return this
    }

    /**
     * Set the build context path.
     *
     * @param buildContext The build context path.
     * @return The BuildConfig instance for chaining.
     */
    BuildConfig setBuildContext(String buildContext) {
        validateParameters('buildContext', buildContext)
        this.buildContext = buildContext
        return this
    }

    /**
     * Add a build argument.
     *
     * @param key The argument key.
     * @param value The argument value.
     * @return The BuildConfig instance for chaining.
     */
    BuildConfig addBuildArg(String key, String value) {
        validateParameters('buildArgKey', key)
        validateParameters('buildArgValue', value)
        buildArgs.put(key, value)
        return this
    }

    String generateConfig() {
        validateParameters('imageName', imageName)
        def dockerBuildSB = new StringBuffer()
        dockerBuildSB.append(DOCKER_BUILD)
        if (tags.size() == 0) {
            dockerBuildSB.append("-t ${imageName} ")
        } else {
            tags.each { String tag ->
                dockerBuildSB.append("-t ${imageName}:${tag} ")
            }
        }
        buildArgs.each { String key, value ->
            dockerBuildSB.append("--build-arg ${key}=${value} ")
        }
        dockerBuildSB.append("-f ${dockerfilePath} ${buildContext}")
        return dockerBuildSB.toString()
    }
}
