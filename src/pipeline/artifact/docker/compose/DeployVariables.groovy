package pipeline.artifact.docker.compose

import pipeline.artifact.docker.IVariable

class DeployVariables implements IVariable {
    protected String CUSTOM_DOCKER_IMAGE_TAG = null
    private LinkedHashMap<String, String> ENV_VARIABLES = [:]

    LinkedHashMap<String, String> getENV_VARIABLES() {
        return ENV_VARIABLES
    }

    DeployVariables setVariable(String key, Serializable value) {
        if (this.hasProperty(key)) {
            this."$key" = value
            return this
        }
        ENV_VARIABLES[key] = value as String
        return this
    }

    void validateRequiredVariables() {
    }
}
