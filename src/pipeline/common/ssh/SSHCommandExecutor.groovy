package pipeline.common.ssh

import com.cloudbees.groovy.cps.NonCPS
import pipeline.common.interfaces.ICommand

class SSHCommandExecutor extends SSHBase implements ICommand {
    private ArrayList<Command> commandList

    SSHCommandExecutor(
            String nodeID,
            LinkedHashSet<String> credentialID,
            String bastionHost,
            String remoteHost
    ) {
        super(nodeID, credentialID, bastionHost, remoteHost)
        commandList = []
    }

    void addCommand(Command command) {
        if (command.isSudo) {
            prepareCommandWithWorkingDir(command)
        } else {
            prepareCommand(command)
        }
        commandList << command
    }

    ArrayList<ArrayList<String>> executeCommand() {
        ArrayList<ArrayList<String>> cmdResultMap = []
        node(nodeID) {
            def cmdResult = new StringBuffer()
            sshagent(credentialList) {
                withOpadminCredentials {
                    commandList.each(createCmdClosure(cmdResult, cmdResultMap))
                    echo(cmdResult as String)
                }
            }
        }
        commandList = []
        return cmdResultMap
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

    private static Closure createCmdClosure(
            StringBuffer cmdResult,
            ArrayList<ArrayList<String>> cmdResultMap
    ) {
        return { Command command ->
            try {
                cmdResult.append("==========CMD<< ${command.originalCmd} >>==========\n")
                String result = execShellScript(command.cmd, command.skipReturnCode)
                cmdResult.append("${result}\n")
                cmdResultMap.add([command.originalCmd, result])
            } catch (Exception e) {
                cmdResult.append("${e.message}\n")
                echo(cmdResult as String)
                throw e
            }
        }
    }

    private String prepareCommandWithWorkingDir(Command command) {
        String workingDir = command.workingDir ?: '$(pwd)'
        String prefix = command.isSudo ? sudoPrefix : ""
        String cmd = "sh -c \"cd ${workingDir}; ${prefix}${command.originalCmd}\""
        return prepareCommand(command, cmd)
    }

    private static String getSudoPrefix() {
        return opadminCredential ? sudoWithPasswordPrefix : "sudo -i "
    }

    private static String getSudoWithPasswordPrefix() {
        return withOpadminCredentials {
            "echo ${getEnvProperty(OPADMIN_PASSWORD)} | sudo -Si "
        }
    }

    private void prepareCommand(Command command, String cmd = null) {
        command.cmd = (cmd == null) ?
                prepareCommand(command.originalCmd) :
                prepareCommand(cmd)
    }

    private String prepareCommand(String cmd) {
        return "${cmdPrefix} '${cmd}'"
    }

    @NonCPS
    protected void getSSHCommandPrefix() {
        String cmdPrefix = "ssh -J ${bastionHost} ${remoteHost}"
        if (opadminCredential == null) {
            this.cmdPrefix = cmdPrefix
        } else {
            this.cmdPrefix = "sshpass -p \$${OPADMIN_PASSWORD} ${cmdPrefix}"
        }
    }
}
