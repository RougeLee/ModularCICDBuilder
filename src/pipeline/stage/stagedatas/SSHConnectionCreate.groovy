package pipeline.stage.stagedatas

import pipeline.artifact.cd.IRemoteConnectionManager
import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class SSHConnectionCreate extends StageData {
    public String Desc = 'Create SSH Connection'
    private IRemoteConnectionManager remoteConnectionManager

    SSHConnectionCreate(
            LinkedHashMap moduleArgs,
            IRemoteConnectionManager remoteConnectionManager
    ) {
        super(moduleArgs)
        this.remoteConnectionManager = remoteConnectionManager
    }

    void init() {
        if (remoteConnectionManager == null) {
            throw new Exception('IRemoteConnectionManager is null.')
        }
        StageArgs['IRemoteConnectionManager'] = remoteConnectionManager
    }
}
