package pipeline.common.ssh

import pipeline.Pipeline
import pipeline.common.interfaces.ICommandExecutor

class SSHRemoteExecutor extends Pipeline implements ICommandExecutor {
    protected ArrayList<SSHRemote> sshRemoteList

    SSHRemoteExecutor(ArrayList<SSHRemote> sshRemoteList) {
        this.sshRemoteList = sshRemoteList
    }

    String executeEOFCommand(EOFCommand eofCommand) {
        StringBuilder result = new StringBuilder()
        for (SSHRemote sshRemote : sshRemoteList) {
            result.append(sshRemote.getRemoteHost()).append(":\n")
            result.append(sshRemote.executeEOFCommand(eofCommand))
            result.append('\n')
        }
        return result.toString()
    }

    void addCommand(Command command) {
        sshRemoteList.each { SSHRemote sshRemote ->
            sshRemote.addCommand(command)
        }
    }

    ArrayList<ArrayList<String>> executeCommand() {
        ArrayList<ArrayList<String>> totalResult = []
        sshRemoteList.each { SSHRemote sshRemote ->
            ArrayList<ArrayList<String>> result = sshRemote.executeCommand()
            totalResult.addAll(result)
        }
        return totalResult
    }

    ArrayList<ArrayList<String>> executeCommand(Command command) {
        addCommand(command)
        return executeCommand()
    }

    ArrayList<ArrayList<String>> executeCommands(ArrayList<Command> commands) {
        commands.each { Command command ->
            addCommand(command)
        }
        return executeCommand()
    }

    String transferFile(TransferFile transferFile) {
        sshRemoteList.each { SSHRemote sshRemote ->
            sshRemote.transferFile(transferFile)
        }
    }
}
