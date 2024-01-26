package pipeline.flow.util

import pipeline.common.constants.EStagePathType
import pipeline.common.interfaces.IStageMain
import pipeline.common.util.Config
import pipeline.common.util.PrepareStage
import pipeline.stage.util.Stage

class StageFlow extends Base {

    StageFlow(Config config) {
        super(config)
    }

    void stageRun() {
        for (stageTuple in stageTupleList) {
            try {
                IStageMain stage = PrepareStage.createStage(stageTuple) as Stage
                stage.run(config)
            }
            catch (Exception e) {
                EchoStep("${stageTuple.stageName} Stage execute failed.\n${e}")
                throw e
            }
        }
    }

    protected void prepareStageList() {
        stageListAddToStageTupleList(EStagePathType.stage, stageList)
    }
}
