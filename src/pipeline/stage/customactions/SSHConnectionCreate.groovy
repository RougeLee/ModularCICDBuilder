package pipeline.stage.customactions

import pipeline.artifact.cd.IRemoteConnectionManager
import pipeline.common.ssh.Command
import pipeline.common.util.Config
import pipeline.common.ssh.SSHRemote
import pipeline.common.util.LinuxRemoteProvider
import pipeline.stage.util.CustomAction
import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class SSHConnectionCreate extends CustomAction {

    void main(Config config, StageData stageData) {
        stage(stageData.Desc) {
            if (config.PROJECT_INFRA_STATE == null) {
                throw new Exception('ProjectInfraDeployState is null.')
            }
            IRemoteConnectionManager manager = getManager(stageData.StageArgs)
            applyDeployerTagsRemotes(manager, getDeployerTagsMap(config, manager))
        }
    }

    private static IRemoteConnectionManager getManager(
            LinkedHashMap stageArgs
    ) {
        return stageArgs['IRemoteConnectionManager'] as IRemoteConnectionManager
    }

    private static LinkedHashMap<ArrayList<String>, ArrayList<SSHRemote>> getDeployerTagsMap(
            Config config,
            IRemoteConnectionManager remoteConnectionManager
    ) {
        LinuxRemoteProvider remoteProvider = new LinuxRemoteProvider(config)
        boolean requireExactMatch = true
        LinkedHashMap<ArrayList<String>, ArrayList<SSHRemote>> deployerTagsMap = [:]
        for (ArrayList<String> tags : remoteConnectionManager.deployerTagsList) {
            ArrayList<SSHRemote> remotes = remoteProvider.getRemotes(tags, requireExactMatch)
            if (remotes.size() == 0) {
                continue
            }
            try {
                if (!execTestConnection(remotes)) {
                    throw new Exception('test connection failed.')
                }
            } catch (Exception e) {
                throw error(e, tags)
            }
            deployerTagsMap[tags] = remotes
        }
        return deployerTagsMap
    }

    private static boolean execTestConnection(ArrayList<SSHRemote> remotes) {
        if (remotes == null || remotes.size() == 0) {
            return false
        }
        remotes.each { SSHRemote remote ->
            try {
                remote.executeCommand new Command('exit')
            } catch (e) {
                EchoStep("${remote.remoteHost} test connection failed: $e")
                throw e
            }
        }
        return true
    }

    private static void applyDeployerTagsRemotes(
            IRemoteConnectionManager remoteConnectionManager,
            LinkedHashMap<ArrayList<String>, ArrayList<SSHRemote>> deployerTagsMap
    ) {
        remoteConnectionManager.applyDeployerTagsRemotes(deployerTagsMap)
    }

    private static Exception error(Exception e, ArrayList<String> tags) {
        EchoStep("SSHConnectionCreate failed:\nDeployTags: $tags\n$e")
        return e
    }
}
