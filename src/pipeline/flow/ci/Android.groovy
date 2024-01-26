package pipeline.flow.ci

import pipeline.common.constants.BuildTypeLabel
import pipeline.common.util.Config
import pipeline.flow.util.StageFlow
import com.cloudbees.groovy.cps.NonCPS

class Android extends StageFlow {

    Android(Config config) {
        super(config)
        initConstructor()
    }

    @NonCPS
    private void initConstructor() {
        if (config.BUILD_TYPE == BuildTypeLabel.CONTINUOUS) {
            String logMessage = getStepStage('no Continuous BUILD_TYPE')
            throw new Exception(logMessage)
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
