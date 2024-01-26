package pipeline.flow.ci

import pipeline.common.constants.BuildTypeLabel
import pipeline.common.util.Config
import pipeline.flow.util.StageFlow
import com.cloudbees.groovy.cps.NonCPS

class Server extends StageFlow {

    Server(Config config) {
        super(config)
        initConstructor()
    }

    @NonCPS
    private void initConstructor() {
        if (config.BUILD_TYPE == BuildTypeLabel.CONTINUOUS) {
            stageList = ['CheckOutStage', 'ServerBuildStage']
            return
        }
        stageList = [
                'CheckOutStage',
                'ServerPreBuildStage',
                'ServerBuildStage',
                'ServerPostBuildStage'
        ]
    }
}
