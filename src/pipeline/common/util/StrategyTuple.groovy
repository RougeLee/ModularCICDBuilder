package pipeline.common.util

import pipeline.stage.util.StageData

class StrategyTuple extends Tuple {
    ArrayList<StageData> stageDataList

    StrategyTuple(ArrayList<StageData> stageDataList) {
        super(stageDataList)
        this.stageDataList = stageDataList
    }
}
