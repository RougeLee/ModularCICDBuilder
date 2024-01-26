package pipeline.stage.stagedatas

import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class PrintProjectInfraDeployState extends StageData {
    public String Desc = 'Print ProjectInfraDeployState'

    PrintProjectInfraDeployState(LinkedHashMap moduleArgs) {
        super(moduleArgs)
    }

    void init() {}
}
