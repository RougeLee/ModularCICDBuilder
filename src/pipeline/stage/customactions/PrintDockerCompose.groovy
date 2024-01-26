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
                String content
                try {
                    content = deployer.dockerComposeYamlGenerator.dockerComposeYamlContent
                } catch (Exception e) {
                    EchoStep("DeployTags: $tags\n$e")
                    throw e
                }
                EchoStep("DeployTags: $tags\nDockerComposeContent:\n$content")
            }
        }
    }
}
