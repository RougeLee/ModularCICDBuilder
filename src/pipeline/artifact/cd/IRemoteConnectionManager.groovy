package pipeline.artifact.cd

import pipeline.common.ssh.SSHRemote

interface IRemoteConnectionManager {
    ArrayList<ArrayList<String>> getDeployerTagsList()

    void applyDeployerTagsRemotes(LinkedHashMap<ArrayList<String>, ArrayList<SSHRemote>> deployerTagsRemotesMap)
}
