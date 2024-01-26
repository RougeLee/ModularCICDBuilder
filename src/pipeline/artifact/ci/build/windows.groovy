package pipeline.artifact.ci.build

import pipeline.artifact.ci.BuildBase
import pipeline.common.util.Config
import pipeline.flow.ci.Windows

@SuppressWarnings('unused')
class windows extends BuildBase {

    windows(Config config) {
        super(config)
        ciFlow = new Windows(config)
    }

    void platformArgInit() {
        config.BUILD_LABEL = getClientBuildLabel()
        config.PROJECT_LABEL = "${config.PROJECT_CODE}_win"
    }
}
