package pipeline.artifact.docker.cli
/**
 * Abstract base class for configuring Docker commands.
 */
abstract class AbstractDockerConfig implements IDockerConfig {
    protected static boolean checkParameter(String parameter) {
        return parameter && !parameter.trim().isEmpty()
    }

    protected static void validateParameters(String parameterName, String parameter) {
        if (!checkParameter(parameter)) {
            throw new IllegalArgumentException("Parameter ${parameterName} is null or empty.")
        }
    }
}
