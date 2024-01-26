package pipeline.module.lib

import pipeline.common.util.Config
import pipeline.common.util.StrategyTuple
import pipeline.stage.stagedatas.ConsulKVStage
import pipeline.stage.stagedatas.PrintProjectInfraDeployState

class ConsulKVStrategyTuple {

    static void applyConsulKVStrategyTuple(
            ArrayList<StrategyTuple> strategyList,
            Config config,
            LinkedHashMap moduleArgs
    ) {
        LinkedHashMap departmentArgs = getDepartmentArgs(moduleArgs)
        ArrayList consulKVStageList = []
        consulKVStageList << new ConsulKVStage(departmentArgs, config.CONSUL_KV)
        consulKVStageList << new PrintProjectInfraDeployState(departmentArgs)
        strategyList << new StrategyTuple(consulKVStageList)
    }

    private static LinkedHashMap getDepartmentArgs(LinkedHashMap moduleArgs) {
        return Department.getDepartmentArgs(moduleArgs)
    }
}
