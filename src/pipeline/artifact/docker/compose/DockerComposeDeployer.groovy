package pipeline.artifact.docker.compose

class DockerComposeDeployer {
    private static final String VERSION = EDockerComposeTopLevelKey.version.name()
    private static final String NETWORKS = EDockerComposeTopLevelKey.networks.name()
    private static final String VOLUMES = EDockerComposeTopLevelKey.volumes.name()
    private static final String CONFIGS = EDockerComposeTopLevelKey.configs.name()
    private static final String SECRETS = EDockerComposeTopLevelKey.secrets.name()
    private static final ArrayList<String> CUSTOM_TOP_LEVEL_KEYS = [
            NETWORKS, VOLUMES, CONFIGS, SECRETS
    ]
    private LinkedHashMap<String, Serializable> dockerComposeTopLevel
    private IConfigurationSetup composeServiceSetup
    private ComposeYamlGenerator composeYamlGenerator

    DockerComposeDeployer(
            LinkedHashMap<String, Serializable> dockerComposeTopLevel,
            IConfigurationSetup composeServiceSetup
    ) {
        this.dockerComposeTopLevel = dockerComposeTopLevel
        this.composeServiceSetup = composeServiceSetup
    }

    DockerComposeDeployer deployComposeServiceSetup() {
        composeServiceSetup.initializeServices()
        composeServiceSetup.applyVariablesToServices()
        return this
    }

    ComposeYamlGenerator getDockerComposeYamlGenerator() {
        if (composeYamlGenerator == null) {
            initDockerComposeYamlGenerator()
        }
        return composeYamlGenerator
    }

    private void initDockerComposeYamlGenerator() {
        this.composeYamlGenerator = new ComposeYamlGenerator()
        setVersion()
        addTopLevelArgsToYamlGenerator()
        addServiceConfiguration()
    }

    private void setVersion() {
        String version = dockerComposeTopLevel[VERSION] as String
        composeYamlGenerator.setVersion(version)
    }

    private void addTopLevelArgsToYamlGenerator() {
        CUSTOM_TOP_LEVEL_KEYS.each { String topLevelKey ->
            if (!dockerComposeTopLevel.containsKey(topLevelKey)) {
                return
            }
            def topLevelValue = dockerComposeTopLevel[topLevelKey] as
                    LinkedHashMap<String, Serializable>
            composeYamlGenerator.addTopLevelValues(topLevelKey, topLevelValue)
        }
    }

    private void addServiceConfiguration() {
        composeServiceSetup.serviceConfigMap.each {
            String serviceName, LinkedHashMap composeServiceMap ->
                composeYamlGenerator.addService(
                        serviceName,
                        composeServiceMap
                )
        }
    }
}
