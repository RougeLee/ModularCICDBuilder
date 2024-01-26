package pipeline.stage.customactions

import pipeline.common.ssh.Command
import pipeline.common.util.Config
import pipeline.common.ssh.SSHRemote
import pipeline.common.util.LinuxRemoteProvider
import pipeline.stage.util.CustomAction
import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class TestRemoteCommandExecution extends CustomAction {

    void main(Config config, StageData stageData) {
        String result = SSHRemoteCommandExecution(config, stageData.StageArgs)
        stage(stageData.Desc) {
            EchoStep(result)
        }
    }

    static String SSHRemoteCommandExecution(Config config, LinkedHashMap stageArgs) {
        def cmd = stageArgs.SSHRemoteCommandArgs as String
        def isSudo = stageArgs.isSudo as boolean
        def workingDir = stageArgs.SSHRemoteWorkDir as String
        Command sshRemoteCmd = new Command(cmd, isSudo).setWorkingDir(workingDir)
        ArrayList<SSHRemote> sshRemotes = getRemotes(config, stageArgs)
        StringBuilder stringBuilder = new StringBuilder()
        sshRemotes.each { SSHRemote remote ->
            stringBuilder.append("Remote: $remote.remoteHost\n")
                    .append(remote.executeCommand(sshRemoteCmd))
                    .append('\n')
        }
        return stringBuilder.toString()
    }

    static ArrayList<SSHRemote> getRemotes(Config config, LinkedHashMap stageArgs) {
        ArrayList<String> deployTags = stageArgs.DeployTags as ArrayList<String>
        Boolean requireExactMatch = (deployTags.size() > 0) ? true : null
        return new LinuxRemoteProvider(config).getRemotes(deployTags, requireExactMatch)
    }
}
