package pipeline.artifact.docker.compose

import pipeline.artifact.cd.DeploymentConfigManager
import pipeline.artifact.docker.DockerImageNameMaker
import pipeline.artifact.docker.panels.SpecifyImageTag

class ServiceConfigurationSetup implements IConfigurationSetup {
    private ArrayList<ComposeUnit> composeUnits
    private DockerImageNameMaker imageNameMaker
    private SpecifyImageTag specifyImageTag
    private DeploymentConfigManager deploymentConfigManager
    private LinkedHashMap<String, ComposeServiceUnit> composeServiceUnits = [:]

    ServiceConfigurationSetup(
            ArrayList<ComposeUnit> composeUnits,
            DockerImageNameMaker imageNameMaker,
            SpecifyImageTag specifyImageTag,
            DeploymentConfigManager deploymentConfigManager
    ) {
        this.composeUnits = composeUnits
        this.imageNameMaker = imageNameMaker
        this.specifyImageTag = specifyImageTag
        this.deploymentConfigManager = deploymentConfigManager
    }

    static String getServiceName(ComposeUnit composeUnit) {
        String serviceYamlFile = composeUnit.service
        return serviceYamlFile.substring(
                serviceYamlFile.lastIndexOf('/') + 1,
                serviceYamlFile.lastIndexOf('.')
        ).toLowerCase()
    }

    void initializeServices() {
        composeUnits.each { ComposeUnit composeUnit ->
            String serviceName = getServiceName(composeUnit)
            ComposeServiceUnit serviceUnit = getComposeServiceUnit(composeUnit)
            composeServiceUnits[serviceName] = serviceUnit
        }
    }

    void applyVariablesToServices() {
        composeServiceUnits.each {
            String serviceName, ComposeServiceUnit serviceUnit ->
                ComposeService composeService = serviceUnit.composeService
                serviceUnit.deployVariables.ENV_VARIABLES.each {
                    String key, String value ->
                        composeService.setEnvironmentVariable(key, value)
                }
                new ArtifactImageManager(
                        imageNameMaker,
                        specifyImageTag,
                        serviceUnit
                ).checkArtifactImage()
                composeService.validateRequiredVariables()
        }
    }

    LinkedHashMap<String, LinkedHashMap> getServiceConfigMap() {
        LinkedHashMap<String, LinkedHashMap> composeServiceMap = [:]
        composeServiceUnits.each { String serviceName, ComposeServiceUnit serviceUnit ->
            composeServiceMap[serviceName] = serviceUnit.composeService.service
        }
        return composeServiceMap
    }

    private ComposeServiceUnit getComposeServiceUnit(ComposeUnit composeUnit) {
        DeployVariables deployVariables = new DeployVariables()
        ComposeService composeService = new ComposeService()
        if (composeUnit.deploy != null) {
            deploymentConfigManager
                    .readDeploymentConfigFile(composeUnit.deploy)
                    .delegateConfigToClosure(composeUnit.deploy, deployVariables)
                    .verifyRequiredVariables(deployVariables)
        }
        deploymentConfigManager
                .readDeploymentConfigFile(composeUnit.service)
                .delegateConfigToClosure(composeUnit.service, composeService)
        return new ComposeServiceUnit(deployVariables, composeUnit, composeService)
    }
}
