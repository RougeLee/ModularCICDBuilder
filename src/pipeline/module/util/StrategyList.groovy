package pipeline.module.util

import pipeline.common.util.Config
import pipeline.common.interfaces.IGetStrategyList
import pipeline.common.util.StrategyTuple

abstract class StrategyList extends Base implements IGetStrategyList {
    protected Config config
    protected LinkedHashMap<Serializable, Serializable> moduleArgs
    protected ArrayList<StrategyTuple> strategyTuples = []

    StrategyList(Config config, LinkedHashMap<Serializable, Serializable> moduleArgs) {
        this.config = config
        this.moduleArgs = moduleArgs
    }

    protected void verifyModuleArgs() {
        checkRequiredArgs(moduleArgs)
    }
}
