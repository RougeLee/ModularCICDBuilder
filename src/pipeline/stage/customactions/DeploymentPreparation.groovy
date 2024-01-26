package pipeline.stage.customactions

import pipeline.artifact.cd.IDeploymentPreparation
import pipeline.common.util.Config
import pipeline.stage.util.CustomAction
import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class DeploymentPreparation extends CustomAction {

    void main(Config config, StageData stageData) {
        stage(stageData.Desc) {
            IDeploymentPreparation preparation
            preparation = getDeploymentPreparation(stageData.StageArgs)
            if (preparation == null) {
                throw new Exception('IDeploymentPreparation is null.')
            }
            node(config.DEPLOY_NODE) {
                prepareStopActions(preparation)
                stopExistingService(preparation)
                postDownActions(preparation)
            }
        }
    }

    private static IDeploymentPreparation getDeploymentPreparation(LinkedHashMap stageArgs) {
        return stageArgs['IDeploymentPreparation'] as IDeploymentPreparation
    }

    private static void prepareStopActions(IDeploymentPreparation deploymentPreparation) {
        EchoStep('Prepare stop actions')
        deploymentPreparation.prepareStopActions()
    }

    private static void stopExistingService(IDeploymentPreparation deploymentPreparation) {
        EchoStep('Stop existing service')
        deploymentPreparation.stopExistingService()
    }

    private static void postDownActions(IDeploymentPreparation deploymentPreparation) {
        EchoStep('Post down actions')
        deploymentPreparation.postDownActions()
    }
}
