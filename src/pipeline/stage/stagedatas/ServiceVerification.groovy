package pipeline.stage.stagedatas

import pipeline.artifact.cd.IServiceVerification
import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class ServiceVerification extends StageData {
    public String Desc = 'Service Verification'
    IServiceVerification serviceVerification

    ServiceVerification(
            LinkedHashMap moduleArgs,
            IServiceVerification serviceVerification
    ) {
        super(moduleArgs)
        this.serviceVerification = serviceVerification
    }

    void init() {
        if (serviceVerification == null) {
            throw new Exception('IServiceVerification is null.')
        }
        StageArgs['IServiceVerification'] = serviceVerification
    }
}
