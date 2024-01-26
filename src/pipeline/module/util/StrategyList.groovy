package pipeline.module.util

import pipeline.common.util.Config
import pipeline.common.interfaces.IGetStrategyList
import pipeline.common.util.StrategyTuple
import pipeline.stage.util.StageData

class StrategyList extends Base implements IGetStrategyList {
    protected Config config
    protected LinkedHashMap<Serializable, Serializable> moduleArgs

    StrategyList(Config config, LinkedHashMap<Serializable, Serializable> moduleArgs) {
        this.config = config
        this.moduleArgs = moduleArgs
    }

    ArrayList<StrategyTuple> getStrategyList() {
        ArrayList<StageData> stageList = []
        StrategyTuple defaultStageTuple = new StrategyTuple(stageList)
        return [defaultStageTuple]
    }
}
