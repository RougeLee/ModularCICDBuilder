package pipeline.stage.stagedatas

import pipeline.module.lib.DockerComposeLib
import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class DockerComposeAssembly extends StageData {
    public String Desc = 'Docker Compose Assembly'

    DockerComposeAssembly(LinkedHashMap moduleArgs) {
        super(moduleArgs)
        assertKeys << DockerComposeLib.DOCKER_COMPOSE_DEPLOYER_MAP
        assertKeys << DockerComposeLib.DOCKER_COMPOSE_TOP_LEVEL
        assertKeys << DockerComposeLib.DOCKER_COMPOSE_SERVICES
    }

    void init() {
        moduleArgsMapping()
    }
}
