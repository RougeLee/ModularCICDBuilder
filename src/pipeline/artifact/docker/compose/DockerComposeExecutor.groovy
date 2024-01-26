package pipeline.artifact.docker.compose

import pipeline.Pipeline
import pipeline.common.ssh.EOFCommand
import pipeline.common.ssh.SSHRemote
import pipeline.common.ssh.SSHRemoteExecutor
import pipeline.common.ssh.TransferFile

class DockerComposeExecutor extends Pipeline {
    private ArrayList<String> deployTags
    private SSHRemoteExecutor sshRemoteExecutor
    private int remoteSize
    private ComposeYamlGenerator generator

    DockerComposeExecutor(
            ArrayList<String> deployTags,
            ArrayList<SSHRemote> sshRemoteList,
            ComposeYamlGenerator generator
    ) {
        this.deployTags = deployTags
        sshRemoteExecutor = new SSHRemoteExecutor(sshRemoteList)
        remoteSize = sshRemoteList.size()
        this.generator = generator
    }

    void executeTagsEOFCommands(
            Map<ArrayList<String>, ArrayList<EOFCommand>> tagsEOFCommands
    ) {
        tagsEOFCommands.each {
            ArrayList<String> tags, ArrayList<EOFCommand> eofCommands ->
                if (!doesTagMatchRequireCondition(tags)) {
                    return
                }
                executeEOFCommands(eofCommands)
        }
    }

    void executeEOFCommands(ArrayList<EOFCommand> eofCommands) {
        eofCommands.each { EOFCommand eofCommand ->
            executeEOFCommand(eofCommand)
        }
    }

    void executeEOFCommand(EOFCommand eofCommand) {
        sshRemoteExecutor.executeEOFCommand(eofCommand)
    }

    void executeTagsTransferFile(
            Map<ArrayList<String>, ArrayList<TransferFile>> tagsTransferFiles
    ) {
        tagsTransferFiles.each {
            ArrayList<String> tags, ArrayList<TransferFile> transferFiles ->
                if (!doesTagMatchRequireCondition(tags)) {
                    return
                }
                executeTransferFile(transferFiles)
        }
    }

    void executeTransferFile(ArrayList<TransferFile> transferFiles) {
        transferFiles.each { TransferFile transferFile ->
            sshRemoteExecutor.transferFile(transferFile)
        }
    }

    void transferDockerComposeFile(String sourceFile, String destFile) {
        String composeFileContent = generator.dockerComposeYamlContent
        int count = 0
        TransferFile transferFile = new TransferFile(sourceFile, destFile)
                .configureTransferCallbacks {
                    if (count == 0 && fileExists(sourceFile)) {
                        sh "rm -f ${sourceFile}"
                    }
                    writeFile file: sourceFile, text: composeFileContent
                } {
                    count++
                    if (count == remoteSize && fileExists(sourceFile)) {
                        sh "rm -f ${sourceFile}"
                    }
                }
        sshRemoteExecutor.transferFile(transferFile)
    }

    private boolean doesTagMatchRequireCondition(ArrayList<String> tags) {
        return tags.containsAll(deployTags) && deployTags.containsAll(tags)
    }
}
