package pipeline.stage.customactions

import pipeline.common.util.Config
import pipeline.common.util.ProjectInfraState
import pipeline.stage.util.CustomAction
import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class PrintProjectInfraDeployState extends CustomAction {

    void main(Config config, StageData stageData) {
        stage(stageData.Desc) {
            ProjectInfraState state = config.PROJECT_INFRA_STATE
            List<String> resultList = []
            resultList << 'Project infrastructure deployment state:'
            resultList << 'OS Types:'
            getMapSplicerContent(resultList, state.osTypes)
            resultList << 'Deploy Tags:'
            getMapSplicerContent(resultList, state.deployTags)
            resultList << 'Infra List:'
            getMapSplicerContent(resultList, state.infraList)
            EchoStep(resultList.join('\n'))
            if (state.osTypes.size() == 0) {
                throw new Exception('No OS Types found.')
            }
            if (state.deployTags.size() == 0) {
                throw new Exception('No Deploy Tags found.')
            }
            if (state.infraList.size() == 0) {
                throw new Exception('No Infra List found.')
            }
        }
    }

    static void getMapSplicerContent(
            List<String> resultList,
            LinkedHashMap dataMap
    ) {
        dataMap.each { key, value ->
            resultList << new StringBuilder()
                    .append(key)
                    .append(': ')
                    .append(value)
                    .toString()
        }
    }
}
