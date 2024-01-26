package pipeline.stage.flowstages

import pipeline.common.util.Config
import pipeline.stage.util.Stage
import pipeline.artifact.docker.ImageBuildWorkflowManager

@SuppressWarnings('unused')
class DockerBuildStage extends Stage {

    void main(Config config) {
        stage('Docker Build') {
            timestamps {
                ImageBuildWorkflowManager.buildAll()
            }
        }
    }
}
