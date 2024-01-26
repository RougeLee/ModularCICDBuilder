package pipeline.artifact.docker.compose

import pipeline.artifact.cd.IEnvironmentSetup
import pipeline.artifact.cd.IDeploymentPreparation
import pipeline.artifact.cd.IServiceLaunch
import pipeline.artifact.cd.IServiceVerification
import pipeline.common.ssh.EOFCommand
import pipeline.common.ssh.TransferFile
import pipeline.common.util.Config

class DeploymentSetupManager implements
        IEnvironmentSetup, IDeploymentPreparation,
        IServiceLaunch, IServiceVerification {
    private final Config config
    private final Map<ArrayList<String>, DockerComposeExecutor> executors
    private ComposeDeployAssembler deployAssembler

    DeploymentSetupManager(
            Config config,
            Map<ArrayList<String>, DockerComposeExecutor> executors,
            ComposeDeployAssembler deployAssembler
    ) {
        this.config = config
        this.executors = executors
        this.deployAssembler = deployAssembler
    }

    void checkBaseDirectory() {
        executorsEOFCommand(deployAssembler.initVarDirBash)
    }

    void checkProjectDirectory() {
        executorsEOFCommand(deployAssembler.mkdirProjectDirBash)
    }

    void environmentConfiguration() {
        executorsTagsEOFCommands(deployAssembler.environmentSetupShells)
    }

    void prepareStopActions() {
        executorsTagsEOFCommands(deployAssembler.prepareStopShells)
    }

    void stopExistingService() {
        executorsEOFCommand(deployAssembler.composeDownBash)
    }

    void postDownActions() {
        executorsTagsEOFCommands(deployAssembler.postDownShells)
    }

    void transferConfiguration() {
        tagsTransferFiles(deployAssembler.transferConfigurationFiles)
        executorsTransferDockerComposeFile(
                deployAssembler.DESTINATION_DOCKER_COMPOSE_FILE,
                deployAssembler.dockerComposeFile
        )
    }

    void prepareServiceLaunch() {
        executorsTagsEOFCommands(deployAssembler.prepareServiceLaunchShells)
    }

    void startService() {
        executorsEOFCommand(deployAssembler.composeUpBash)
    }

    void serviceTesting() {
        executorsTagsEOFCommands(deployAssembler.serviceTestingShells)
    }

    private void executorsEOFCommand(EOFCommand eofCommand) {
        executors.each { ArrayList<String> key, DockerComposeExecutor executor ->
            executor.executeEOFCommand(eofCommand)
        }
    }

    private void executorsTagsEOFCommands(
            Map<ArrayList<String>, ArrayList<EOFCommand>> tagsEOFCommands
    ) {
        executors.each { ArrayList<String> key, DockerComposeExecutor executor ->
            executor.executeTagsEOFCommands(tagsEOFCommands)
        }
    }

    private void tagsTransferFiles(
            Map<ArrayList<String>, ArrayList<TransferFile>> tagsTransferFiles
    ) {
        executors.each { ArrayList<String> key, DockerComposeExecutor executor ->
            executor.executeTagsTransferFile(tagsTransferFiles)
        }
    }

    private void executorsTransferDockerComposeFile(
            String sourceFile,
            String destFile
    ) {
        executors.each { ArrayList<String> key, DockerComposeExecutor executor ->
            executor.transferDockerComposeFile(sourceFile, destFile)
        }
    }
}
