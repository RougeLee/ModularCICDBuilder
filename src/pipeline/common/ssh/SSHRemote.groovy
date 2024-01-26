package pipeline.common.ssh

import pipeline.common.interfaces.ICommand
import pipeline.common.interfaces.ICommandExecutor
import pipeline.common.interfaces.IEOFCommand
import pipeline.common.interfaces.IFileTransfer
import pipeline.common.util.Config

class SSHRemote implements ICommandExecutor {
    protected final String credentialID
    protected final LinkedHashSet<String> credentialList = []
    protected final String nodeID
    protected String bastionHost
    protected String remoteHost
    protected IEOFCommand eofCommandExecutor
    protected ICommand commandExecutor
    protected IFileTransfer fileTransfer
    private Config config

    SSHRemote(
            Config config,
            String nodeID,
            String credentialID,
            String remoteHost
    ) {
        this.config = config
        this.nodeID = nodeID
        this.credentialID = credentialID
        this.credentialList.addAll(config.JENKINS_CREDENTIAL_LIST)
        this.credentialList << credentialID
        this.remoteHost = remoteHost
    }

    SSHRemote init() {
        bastionHost = SSHKeyHandler.getBastionHost(
                nodeID,
                credentialID,
                remoteHost
        )
        eofCommandExecutor = new EOFCommandExecutor(
                nodeID,
                credentialList,
                bastionHost,
                remoteHost
        )
        commandExecutor = new SSHCommandExecutor(
                nodeID,
                credentialList,
                bastionHost,
                remoteHost
        )
        fileTransfer = new SCPFileTransfer(
                nodeID,
                credentialList,
                bastionHost,
                remoteHost
        )
        return this
    }

    String getRemoteHost() {
        return remoteHost
    }

    String executeEOFCommand(EOFCommand eofCommand) {
        return eofCommandExecutor.executeEOFCommand(eofCommand)
    }

    void addCommand(Command command) {
        commandExecutor.addCommand(command)
    }

    ArrayList<ArrayList<String>> executeCommand() {
        return commandExecutor.executeCommand()
    }

    ArrayList<ArrayList<String>> executeCommand(Command command) {
        return commandExecutor.executeCommand(command)
    }

    ArrayList<ArrayList<String>> executeCommands(ArrayList<Command> commands) {
        return commandExecutor.executeCommands(commands)
    }

    String transferFile(TransferFile transferFile) {
        return fileTransfer.transferFile(transferFile)
    }
}
