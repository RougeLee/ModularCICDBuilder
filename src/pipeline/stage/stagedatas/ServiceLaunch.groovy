package pipeline.stage.stagedatas

import pipeline.artifact.cd.IServiceLaunch
import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class ServiceLaunch extends StageData {
    public String Desc = 'Service Launch'
    private IServiceLaunch serviceLaunch

    ServiceLaunch(
            LinkedHashMap moduleArgs,
            IServiceLaunch serviceLaunch
    ) {
        super(moduleArgs)
        this.serviceLaunch = serviceLaunch
    }

    void init() {
        if (serviceLaunch == null) {
            throw new Exception('IServiceLaunch is null.')
        }
        StageArgs['IServiceLaunch'] = serviceLaunch
    }
}
