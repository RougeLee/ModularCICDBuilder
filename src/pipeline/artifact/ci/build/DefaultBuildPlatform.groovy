package pipeline.artifact.ci.build

import pipeline.artifact.ci.BuildBase
import pipeline.common.util.Config

class DefaultBuildPlatform extends BuildBase {
    DefaultBuildPlatform(Config config) {
        super(config)
    }

    void platformArgInit() {
        config.PROJECT_LABEL = ''
    }
}
