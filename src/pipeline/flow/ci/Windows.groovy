package pipeline.flow.ci

import pipeline.common.constants.BuildTypeLabel
import pipeline.common.util.Config
import pipeline.flow.util.StageFlow
import com.cloudbees.groovy.cps.NonCPS

class Windows extends StageFlow {
    Windows(Config config) {
        super(config)
        initConstructor()
    }

    @NonCPS
    private void initConstructor() {
        if (config.BUILD_TYPE == BuildTypeLabel.CONTINUOUS) {
            stageList = [
                    'CheckOutStage',
                    'MissingDetectStage',
                    'UnityBuildStage'
            ]
            return
        }
        stageList = [
                'CheckOutStage',
                'PreBuildStage',
                'BundleBuildStage',
                'BundleUploadStage',
                'UnityBuildStage',
                'PostBuildStage',
                'ArtifactStage'
        ]
        if (config.BUILD_TYPE == BuildTypeLabel.DAILY) {
            stageList << 'UnityTestStage'
        }
        if (config.BUILD_TYPE == BuildTypeLabel.PUBLISH) {
            stageList << 'DeployStage'
        }
    }
}
