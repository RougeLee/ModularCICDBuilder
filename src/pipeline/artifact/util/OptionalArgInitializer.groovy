package pipeline.artifact.util

import pipeline.Pipeline
import pipeline.common.constants.AppMode
import pipeline.common.util.Config

class OptionalArgInitializer extends Pipeline implements IInitializeProcessData {
    private Config config

    OptionalArgInitializer(Config config) {
        this.config = config
    }

    void initialize() {
        initAppMode()
    }

    private void initAppMode() {
        switch (config.APP_MODE) {
            case AppMode.APP_MODE_NONE:
                break
            case null:
                config.APP_MODE = AppMode.APP_MODE_NONE
                break
            default:
                if (!AppMode.APP_MODE_LIST.contains(config.APP_MODE)) {
                    throw new Exception(getStepStage("invalid config.APP_MODE = ${config.APP_MODE}"))
                }
                break
        }
    }
}
