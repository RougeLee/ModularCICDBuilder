package pipeline.flow.util

import pipeline.Pipeline
import pipeline.common.constants.EStagePathType
import pipeline.common.util.Config
import pipeline.common.util.StageTuple
import pipeline.stage.util.StageData

abstract class Base extends Pipeline {
    protected String flowName = this.class.simpleName
    protected ArrayList stageList
    protected ArrayList<StageTuple> stageTupleList = []
    protected Config config

    Base(Config config) {
        this.config = config
    }

    abstract protected void prepareStageList()

    abstract protected void stageRun()

    void main() {
        enterFlowProcess()
        prepareStageList()
        stageRun()
    }

    protected void enterFlowProcess() {
        EchoStep("${flowName} Flow Start.")
    }

    protected void stageListAddToStageTupleList(
            EStagePathType pathEnum,
            ArrayList stageList
    ) {
        stageList.each { stageObject ->
            stageObjectHandler(pathEnum, stageObject)
        }
    }

    protected void stageObjectHandler(EStagePathType pathEnum, Object stageObject) {
        String stageName
        Class stageClass = stageObject.getClass()
        switch (stageClass) {
            case String:
                stageName = stageObject
                stageTupleList << new StageTuple(pathEnum, stageName)
                break
            case StageData:
                def stageData = stageObject as StageData
                stageName = stageData.StageName
                stageTupleList << new StageTuple(pathEnum, stageName, stageData)
                break
            default:
                String error = new StringBuilder()
                        .append("stageObject: ${stageObject}, ")
                        .append("stageClass: ${stageClass} ")
                        .append('is not a instance of String or StageData.')
                        .toString()
                throw new Exception(getStepStage(error))
        }
    }
}
