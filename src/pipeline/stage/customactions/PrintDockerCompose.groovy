package pipeline.stage.customactions

import pipeline.artifact.docker.compose.DockerComposeDeployer
import pipeline.common.util.Config
import pipeline.module.lib.DockerComposeLib
import pipeline.stage.util.CustomAction
import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class PrintDockerCompose extends CustomAction {

    void main(Config config, StageData stageData) {
        stage(stageData.Desc) {
            Map<ArrayList<String>, DockerComposeDeployer> deployers
            deployers = DockerComposeLib.getDeployers(stageData.StageArgs)
            if (deployers == null) {
                throw new Exception('DockerComposeDeployerMap is null.')
            }
            deployers.each { ArrayList<String> tags, DockerComposeDeployer deployer ->
                EchoStep(new StringBuilder('\n')
                        .append("DeployTags: ${tags}\n")
                        .append("DockerComposeContent:\n")
                        .append(deployer.dockerComposeYamlGenerator.dockerComposeYamlContent)
                        .toString()
                )
            }
        }
    }
}
