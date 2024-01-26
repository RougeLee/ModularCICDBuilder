package pipeline.stage.stagedatas

import pipeline.module.lib.DockerComposeLib
import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class PrintDockerCompose extends StageData {
    public String Desc = 'Print Generate DockerComposeFile'

    PrintDockerCompose(LinkedHashMap moduleArgs) {
        super(moduleArgs)
        assertKeys << DockerComposeLib.DOCKER_COMPOSE_DEPLOYER_MAP
    }

    void init() {
        moduleArgsMapping()
    }
}
