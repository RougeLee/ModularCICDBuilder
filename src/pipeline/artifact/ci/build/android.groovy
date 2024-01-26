package pipeline.artifact.ci.build

import pipeline.artifact.ci.BuildBase
import pipeline.common.util.Config
import pipeline.flow.ci.Android

@SuppressWarnings('unused')
class android extends BuildBase {

    android(Config config) {
        super(config)
        ciFlow = new Android(config)
    }

    void platformArgInit() {
        config.BUILD_LABEL = getClientBuildLabel()
        config.PROJECT_LABEL = "${config.PROJECT_CODE}_win"
    }
}
