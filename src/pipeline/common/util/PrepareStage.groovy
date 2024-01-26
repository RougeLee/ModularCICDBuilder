package pipeline.common.util

import pipeline.common.constants.EStagePathType
import pipeline.stage.util.Base as StageBase

import java.lang.reflect.Constructor

/**
 * Use the Dynamic Class Loader to load the class.
 */
class PrepareStage {
    private static final String PackageStagePath = 'pipeline.stage'
    private static final LinkedHashMap<EStagePathType, String> StagePathMap = [:]

    static {
        StagePathMap.put(EStagePathType.stage, "${PackageStagePath}.flowstages")
        StagePathMap.put(EStagePathType.custom, "${PackageStagePath}.customactions")
    }

    static StageBase createStage(StageTuple stageTuple) {
        Class stageClass = getStageClass(stageTuple)
        Constructor stageConstructor = stageClass.getDeclaredConstructor()
        return stageConstructor.newInstance() as StageBase
    }

    private static Class getStageClass(StageTuple stageTuple) {
        String className = getClassName(stageTuple)
        return StageBase.class.getClassLoader().loadClass(className)
    }

    private static String getClassName(StageTuple stageTuple) {
        String stageName = stageTuple.stageName
        String stagePathType = stageTuple.stagePathType
        String path = StagePathMap[EStagePathType[stagePathType]]
        return "$path.$stageName"
    }
}
