package pipeline.module.lib

import pipeline.artifact.docker.compose.ComposeUnit
import pipeline.artifact.docker.compose.DockerComposeDeployer
import pipeline.artifact.docker.compose.PrepareComposeUnitList

class DockerComposeLib {
    public static final String DOCKER_COMPOSE_TOP_LEVEL = 'DockerComposeTopLevel'
    public static final String DOCKER_COMPOSE_SERVICES = 'DockerComposeServices'
    public static final String DOCKER_COMPOSE_DEPLOYER_MAP = 'DockerComposeDeployerMap'
    public static final String DOCKER_COMPOSE_EXECUTOR_MAP = 'DockerComposeExecutorMap'
    public static final String ENVIRONMENT_SETUP_SHELLS = 'EnvironmentSetupShells'
    public static final String PREPARE_STOP_SHELLS = 'PrepareStopShells'
    public static final String POST_DOWN_SHELLS = 'PostDownShells'
    public static final String TRANSFER_CONFIGURATION_FILES = 'TransferConfigurationFiles'
    public static final String PREPARE_SERVICE_LAUNCH_SHELLS = 'PrepareServiceLaunchShells'
    public static final String SERVICE_TESTING_SHELLS = 'ServiceTestingShells'

    static ArrayList<ComposeUnit> getComposeUnits(LinkedHashMap moduleArgs) {
        def services = moduleArgs[DOCKER_COMPOSE_SERVICES] as ArrayList<LinkedHashMap>
        if (services == null) {
            throw new Exception('DockerComposeServices is empty.')
        }
        return new PrepareComposeUnitList(services).getComposeUnitList()
    }

    static LinkedHashMap<ArrayList<String>, DockerComposeDeployer> getDeployers(
            LinkedHashMap moduleArgs
    ) {
        return moduleArgs[DOCKER_COMPOSE_DEPLOYER_MAP] as
                LinkedHashMap<ArrayList<String>, DockerComposeDeployer>
    }
}
