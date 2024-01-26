package pipeline.stage.util

import pipeline.common.interfaces.IStageMain
import pipeline.common.util.Config

abstract class Stage extends Base implements IStageMain {

    void run(Config config) {
        main(config)
    }
}
