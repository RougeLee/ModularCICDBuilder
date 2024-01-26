package pipeline.common.ui.panels

import pipeline.common.ui.Panel
import pipeline.common.util.Config

class SSHRemoteWorkDir extends Panel {
    private static final String sshRemoteWorkDirParamName = 'SSH_REMOTE_WORK_DIR'
    private static final String sshRemoteWorkDirParamDesc = '指定遠端指令的工作目錄'

    SSHRemoteWorkDir(Config config) {
        super(config)
    }

    String getRemoteWorkDir() {
        return params[sshRemoteWorkDirParamName] as String
    }

    SSHRemoteWorkDir initUIParam() {
        LinkedHashMap sshRemoteWorkDirMap = [:]
        sshRemoteWorkDirMap[PARAMS_NAME] = sshRemoteWorkDirParamName
        sshRemoteWorkDirMap[PARAMS_DESCRIPTION] = sshRemoteWorkDirParamDesc
        config.CUSTOM_PARAMETERS << string(sshRemoteWorkDirMap)
        if (remoteWorkDir == null) {
            entryResetUIStage("${sshRemoteWorkDirParamName} is null.")
        }
        return this
    }
}
