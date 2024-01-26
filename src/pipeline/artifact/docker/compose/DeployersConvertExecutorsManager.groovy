package pipeline.artifact.docker.compose


import pipeline.artifact.cd.IRemoteConnectionManager
import pipeline.common.ssh.SSHRemote

class DeployersConvertExecutorsManager implements IRemoteConnectionManager {
    LinkedHashMap<ArrayList<String>, DockerComposeExecutor> executors
    LinkedHashMap<ArrayList<String>, DockerComposeDeployer> deployers

    DeployersConvertExecutorsManager(
            LinkedHashMap<ArrayList<String>, DockerComposeExecutor> executors,
            LinkedHashMap<ArrayList<String>, DockerComposeDeployer> deployers
    ) {
        this.executors = executors
        this.deployers = deployers
    }

    ArrayList<ArrayList<String>> getDeployerTagsList() {
        return deployers.collect {
            ArrayList<String> tags, DockerComposeDeployer deployer ->
                tags
        }
    }

    void applyDeployerTagsRemotes(
            LinkedHashMap<ArrayList<String>, ArrayList<SSHRemote>> deployerTagsRemotesMap
    ) {
        deployerTagsRemotesMap.each { ArrayList<String> tags, ArrayList<SSHRemote> remotes ->
            DockerComposeDeployer deployer = deployers[tags]
            executors[tags] = new DockerComposeExecutor(
                    tags,
                    remotes,
                    deployer.dockerComposeYamlGenerator
            )
        }
    }
}
