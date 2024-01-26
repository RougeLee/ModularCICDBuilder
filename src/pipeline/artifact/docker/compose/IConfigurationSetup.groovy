package pipeline.artifact.docker.compose

interface IConfigurationSetup {
    void initializeServices();

    void applyVariablesToServices();

    LinkedHashMap<String, LinkedHashMap> getServiceConfigMap();
}
