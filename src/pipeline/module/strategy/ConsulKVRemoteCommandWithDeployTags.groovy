package pipeline.module.strategy

import pipeline.common.ui.panels.DeployTags
import pipeline.common.ui.panels.SSHRemoteCommand
import pipeline.common.ui.panels.SSHRemoteWorkDir
import pipeline.common.ui.panels.SudoChoice
import pipeline.common.util.Config
import pipeline.common.util.StrategyTuple
import pipeline.module.lib.ConsulKVStrategyTuple
import pipeline.module.lib.Department
import pipeline.module.util.StrategyList
import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class ConsulKVRemoteCommandWithDeployTags extends StrategyList {
    private ArrayList<StrategyTuple> strategyList = []

    ConsulKVRemoteCommandWithDeployTags(
            Config config,
            LinkedHashMap<Serializable, Serializable> moduleArgs
    ) {
        super(config, moduleArgs)
        argNameList.addAll([
                Department.CREDENTIAL_ID,
                Department.PROJECT_NAME,
                Department.INFRA_NAME,
                Department.CONSUL_KV_TOKEN
        ])
    }

    ArrayList<StrategyTuple> getStrategyList() {
        verifyArgDict(moduleArgs)
        applyConsulKVStrategyTuple()
        applySSHRemoteCommandExecution()
        return strategyList
    }

    private void applyConsulKVStrategyTuple() {
        ConsulKVStrategyTuple.applyConsulKVStrategyTuple(
                strategyList,
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
        strategyList << new StrategyTuple([sshRemoteCMD])
    }
}
