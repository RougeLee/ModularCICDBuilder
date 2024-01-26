package pipeline.stage.stagedatas

import pipeline.artifact.cd.IEnvironmentSetup
import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class EnvironmentSetup extends StageData {
    public String Desc = 'Environment Setup'
    private IEnvironmentSetup envSetup

    EnvironmentSetup(
            LinkedHashMap moduleArgs,
            IEnvironmentSetup envSetup
    ) {
        super(moduleArgs)
        this.envSetup = envSetup
    }

    void init() {
        if (envSetup == null) {
            throw new Exception('IEnvironmentSetup is null.')
        }
        StageArgs['IEnvironmentSetup'] = envSetup
    }
}
