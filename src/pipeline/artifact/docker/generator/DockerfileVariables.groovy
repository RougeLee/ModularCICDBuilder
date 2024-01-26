package pipeline.artifact.docker.generator

import pipeline.artifact.docker.IVariable

class DockerfileVariables implements IVariable {
    private LinkedHashMap<String, String> ENV_VARIABLES = [:]
    private String FROM_IMAGE
    private String PROJECT_DIR

    LinkedHashMap<String, String> getENV_VARIABLES() {
        return ENV_VARIABLES
    }

    String getFROM_IMAGE() {
        return FROM_IMAGE
    }

    String getPROJECT_DIR() {
        return PROJECT_DIR
    }

    DockerfileVariables setVariable(String key, Serializable value) {
        if (this.hasProperty(key)) {
            this."$key" = value
            return this
        }
        ENV_VARIABLES[key] = value as String
        return this
    }

    void validateRequiredVariables() {
        if (FROM_IMAGE == null || FROM_IMAGE.isEmpty()) {
            throw new Exception('FROM_IMAGE is null.')
        }
        if (PROJECT_DIR == null || PROJECT_DIR.isEmpty()) {
            throw new Exception('PROJECT_DIR is null.')
        }
    }
}
