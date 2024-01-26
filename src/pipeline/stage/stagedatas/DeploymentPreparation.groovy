package pipeline.stage.stagedatas

import pipeline.artifact.cd.IDeploymentPreparation
import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class DeploymentPreparation extends StageData {
    public String Desc = 'Deployment Preparation'
    private IDeploymentPreparation deploymentPreparation

    DeploymentPreparation(
            LinkedHashMap moduleArgs,
            IDeploymentPreparation deploymentPreparation
    ) {
        super(moduleArgs)
        this.deploymentPreparation = deploymentPreparation
    }

    void init() {
        if (deploymentPreparation == null) {
            throw new Exception('IDeploymentPreparation is null.')
        }
        StageArgs['IDeploymentPreparation'] = deploymentPreparation
    }
}
