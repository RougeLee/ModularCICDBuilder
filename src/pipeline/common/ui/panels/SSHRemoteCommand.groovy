package pipeline.common.ui.panels

import pipeline.common.ui.Panel
import pipeline.common.util.Config

class SSHRemoteCommand extends Panel {
    private static final String sshRemoteCommandParamName = 'SSH_REMOTE_COMMAND'
    private static final String sshRemoteCommandParamDesc = 'SSH遠端指令'

    SSHRemoteCommand(Config config) {
        super(config)
    }

    String getRemoteCommand() {
        return params[sshRemoteCommandParamName] as String
    }

    SSHRemoteCommand initUIParam() {
        LinkedHashMap sshRemoteCommandMap = [:]
        sshRemoteCommandMap[PARAMS_NAME] = sshRemoteCommandParamName
        sshRemoteCommandMap[PARAMS_DESCRIPTION] = sshRemoteCommandParamDesc
        config.CUSTOM_PARAMETERS << string(sshRemoteCommandMap)
        if (remoteCommand == null) {
            entryResetUIStage("${sshRemoteCommandParamName} is null.")
        }
        return this
    }
}
