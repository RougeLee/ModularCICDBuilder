package pipeline.artifact.ci.build

import pipeline.artifact.ci.BuildBase
import pipeline.common.constants.BuildTypeLabel
import pipeline.common.util.Config
import pipeline.flow.ci.Server

@SuppressWarnings('unused')
class server extends BuildBase {

    server(Config config) {
        super(config)
        ciFlow = new Server(config)
    }

    void platformArgInit() {
        config.BUILD_LABEL = (config.BUILD_TYPE == BuildTypeLabel.CONTINUOUS) ? 'continuous_server_build' : 'server_build'
        config.PROJECT_LABEL = "${config.PROJECT_CODE}_win"
    }
}
