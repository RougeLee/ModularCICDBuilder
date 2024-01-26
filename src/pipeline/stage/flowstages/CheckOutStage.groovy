package pipeline.stage.flowstages

import pipeline.common.constants.ESCM
import pipeline.common.scm.Git
import pipeline.common.util.Config
import pipeline.stage.util.Stage

@SuppressWarnings('unused')
class CheckOutStage extends Stage {
    public String STAGE_NAME = 'Checkout'
    private static final String SCM_GIT = ESCM.GIT.name()
    private boolean isCIFlow = true

    void main(Config config) {
        stage(STAGE_NAME) {
            EchoStep("SCM: ${config.SCM}, BUILD_PLATFORM: ${config.BUILD_PLATFORM}")
            switch (config.SCM) {
                case SCM_GIT:
                    Git.checkout(config, isCIFlow)
                    break
                default:
                    EchoStep("Not support SCM: ${config.SCM}")
                    break
            }
        }
    }

    CheckOutStage setIsCIFlow(boolean isCIFlow) {
        this.isCIFlow = isCIFlow
        return this
    }
}
