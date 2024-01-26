package pipeline.flow.cd

import pipeline.common.constants.EStagePathType
import pipeline.common.util.Config
import pipeline.common.util.StageTuple
import pipeline.common.util.PrepareStage
import pipeline.flow.util.Base
import pipeline.stage.util.CustomAction
import pipeline.stage.util.StageData

class CustomActionFlow extends Base {
    ArrayList<StageData> stageList

    CustomActionFlow(Config config, ArrayList<StageData> stageList) {
        super(config)
        this.stageList = stageList
    }

    void stageRun() {
        for (StageTuple stageTuple in stageTupleList) {
            try {
                executeStageRun(stageTuple)
            }
            catch (Exception e) {
                exceptionHandler(e, stageTuple.stageName)
            }
        }
    }

    protected void prepareStageList() {
        stageListAddToStageTupleList(EStagePathType.custom, stageList)
    }

    private void executeStageRun(StageTuple stageTuple) {
        CustomAction customAction = getCustomAction(stageTuple)
        StageData stageData = stageTuple.stageData
        prepareStageData(stageData)
        runCustomAction(config, customAction, stageData)
    }

    private static CustomAction getCustomAction(StageTuple stageTuple) {
        def customAction = PrepareStage.createStage(stageTuple) as CustomAction
        EchoStep("ready to execute $stageTuple.stageName CustomAction.")
        return customAction
    }

    private static void runCustomAction(
            Config config,
            CustomAction customAction,
            StageData stageData
    ) {
        if (stageData.TimeoutMinutes > 0) {
            timeout(time: stageData.TimeoutMinutes, unit: 'MINUTES') {
                customAction.run(config, stageData)
            }
            return
        }
        customAction.run(config, stageData)
    }

    private static void prepareStageData(StageData stageData) {
        stageData.execInit()
        EchoStep("stageData = $stageData.StageArgs")
    }

    private static void exceptionHandler(Exception e, String stageName) {
        EchoStep("$stageName CustomAction execute failed.\n$e")
        throw e
    }
}
