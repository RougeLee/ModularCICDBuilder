package pipeline.module.strategy

import pipeline.common.ui.panels.DeployTags
import pipeline.common.ui.panels.SSHRemoteCommand
import pipeline.common.ui.panels.SSHRemoteWorkDir
import pipeline.common.ui.panels.SudoChoice
import pipeline.common.util.Config
import pipeline.common.util.StrategyTuple
import pipeline.module.lib.ConsulKVStrategyTuple
import pipeline.module.util.StrategyList
import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class ConsulKVRemoteCommandWithDeployTags extends StrategyList {

    ConsulKVRemoteCommandWithDeployTags(
            Config config,
            LinkedHashMap<Serializable, Serializable> moduleArgs
    ) {
        super(config, moduleArgs)
    }

    ArrayList<StrategyTuple> getStrategyList() {
        verifyModuleArgs()
        applyConsulKVStrategyTuple()
        applySSHRemoteCommandExecution()
        return strategyTuples
    }

    private void applyConsulKVStrategyTuple() {
        ConsulKVStrategyTuple.applyConsulKVStrategyTuple(
                strategyTuples,
                config,
                moduleArgs
        )
    }

    private void applySSHRemoteCommandExecution() {
        String remoteCommand = new SSHRemoteCommand(config).remoteCommand
        if (remoteCommand == null || remoteCommand.isEmpty()) {
            return
        }
        def sshRemoteCMD = new StageData('TestRemoteCommandExecution', [
                DeployTags          : new DeployTags(config).tags,
                SSHRemoteCommandArgs: remoteCommand,
                SSHRemoteWorkDir    : new SSHRemoteWorkDir(config).remoteWorkDir,
                isSudo              : new SudoChoice(config).sudoChoice
        ])
        sshRemoteCMD.Desc = 'Execute SSHRemoteCommand'
        strategyTuples << new StrategyTuple([sshRemoteCMD])
    }
}
