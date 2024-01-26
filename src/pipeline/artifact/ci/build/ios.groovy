package pipeline.artifact.ci.build

import pipeline.artifact.ci.BuildBase
import pipeline.common.constants.BuildTypeLabel
import pipeline.common.util.Config
import pipeline.flow.ci.IOS

@SuppressWarnings('unused')
class ios extends BuildBase {

    ios(Config config) {
        super(config)
        ciFlow = new IOS(config)
    }

    void platformArgInit() {
        config.BUILD_LABEL = (config.BUILD_TYPE == BuildTypeLabel.PUBLISH) ? 'publish_mac' : 'macOS'
        config.PROJECT_LABEL = "${config.PROJECT_CODE}_mac"
    }
}
