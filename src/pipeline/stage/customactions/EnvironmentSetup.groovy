package pipeline.stage.customactions

import pipeline.artifact.cd.IEnvironmentSetup
import pipeline.common.util.Config
import pipeline.stage.util.CustomAction
import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class EnvironmentSetup extends CustomAction {

    void main(Config config, StageData stageData) {
        stage(stageData.Desc) {
            IEnvironmentSetup envSetup = getEnvironmentSetup(stageData.StageArgs)
            if (envSetup == null) {
                throw new Exception('IEnvironmentSetup is null.')
            }
            node(config.DEPLOY_NODE) {
                checkBaseDirectory(envSetup)
                checkProjectDirectory(envSetup)
                environmentConfiguration(envSetup)
            }
        }
    }

    private static IEnvironmentSetup getEnvironmentSetup(LinkedHashMap stageArgs) {
        return stageArgs['IEnvironmentSetup'] as IEnvironmentSetup
    }

    private static void checkBaseDirectory(IEnvironmentSetup envSetup) {
        EchoStep('Check base directory')
        envSetup.checkBaseDirectory()
    }

    private static void checkProjectDirectory(IEnvironmentSetup envSetup) {
        EchoStep('Check project directory')
        envSetup.checkProjectDirectory()
    }

    private static void environmentConfiguration(IEnvironmentSetup envSetup) {
        EchoStep('Environment configuration')
        envSetup.environmentConfiguration()
    }
}
