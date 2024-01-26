package pipeline.common.util

import pipeline.common.constants.EStagePathType
import pipeline.stage.util.StageData

class StageTuple extends Tuple {
    String stagePathType
    String stageName
    StageData stageData = null

    StageTuple(EStagePathType stagePathType, String stageName) {
        super(stagePathType, stageName)
        this.stagePathType = stagePathType
        this.stageName = stageName
    }

    StageTuple(EStagePathType stagePathType, String stageName, StageData stageData) {
        super(stagePathType, stageName, stageData)
        this.stagePathType = stagePathType
        this.stageName = stageName
        this.stageData = stageData
    }
}
