package pipeline.flow.ci

import pipeline.common.util.Config
import pipeline.flow.util.StageFlow

class Docker extends StageFlow {

    Docker(Config config) {
        super(config)
        stageList = []
        stageList << 'CheckOutStage'
        stageList << 'DockerPreBuildStage'
        stageList << 'DockerBuildStage'
        stageList << 'DockerImagePush'
    }
}
