package pipeline.common.interfaces

import pipeline.common.util.Config
import pipeline.stage.util.StageData

interface ICustomActionMain {
    void main(Config config, StageData stageData)
}
