package pipeline.artifact.docker

interface IVariable {
    IVariable setVariable(String key, Serializable value)

    void validateRequiredVariables()
}
