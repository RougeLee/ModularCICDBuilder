package pipeline.module.ui

import pipeline.common.ui.panels.DeployTags
import pipeline.common.ui.panels.SSHRemoteCommand
import pipeline.common.ui.panels.SSHRemoteWorkDir
import pipeline.common.ui.panels.SudoChoice
import pipeline.common.util.Config
import pipeline.module.util.SetupUI

@SuppressWarnings('unused')
class ConsulKVRemoteCommandWithDeployTags extends SetupUI {

    ConsulKVRemoteCommandWithDeployTags(
            Config config,
            LinkedHashMap<Serializable, Serializable> moduleArgs
    ) {
        super(config, moduleArgs)
    }

    void setupUI() {
        new DeployTags(config).initUIParam()
        new SSHRemoteCommand(config).initUIParam()
        new SSHRemoteWorkDir(config).initUIParam()
        new SudoChoice(config).initUIParam()
    }
}
