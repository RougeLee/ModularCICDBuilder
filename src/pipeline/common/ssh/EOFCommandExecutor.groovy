package pipeline.common.ssh

import com.cloudbees.groovy.cps.NonCPS
import pipeline.common.interfaces.IEOFCommand

class EOFCommandExecutor extends SSHBase implements IEOFCommand {
    private static final String EOF_SH = 'EOFScript.sh'

    EOFCommandExecutor(
            String nodeID,
            LinkedHashSet<String> credentialID,
            String bastionHost,
            String remoteHost
    ) {
        super(nodeID, credentialID, bastionHost, remoteHost)
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

    String executeEOFCommand(EOFCommand eofCommand) {
        String result = null
        String workingDir = eofCommand.workingDir ?: '\$HOME'
        Closure execEOFCommandClosure = {
            result = execShellScript(getExecEOFCommand(eofCommand, workingDir))
        }
        node(nodeID) {
            try {
                sshagent(credentialList) {
                    withOpadminCredentials(execEOFCommandClosure)
                }
            } catch (Exception e) {
                EchoStep("Failed to execute EOF command\n${e}")
                throw e
            }
        }
        return result
    }

    private String getExecEOFCommand(
            EOFCommand eofCommand,
            String workingDir
    ) {
        ArrayList<String> cmdList = [
                "cat << EOF > $workingDir/$EOF_SH",
                eofCommand.shellScriptContent,
                'EOF',
                "chmod +x $workingDir/EOFScript.sh",
                getExecEOFScriptCommand(eofCommand, workingDir),
                'EXIT_CODE=$?',
                "rm -f $workingDir/$EOF_SH"
        ]
        def builder = new StringBuilder()
        cmdList.each { String cmd ->
            builder.append(cmd).append('\n')
        }
        builder.append('exit $EXIT_CODE')
        return prepareCommand(builder.toString())
    }

    private static String getExecEOFScriptCommand(
            EOFCommand eofCommand,
            String workingDir
    ) {
        return !eofCommand.isSudo ? "$workingDir/$EOF_SH" :
                "echo $envOpadminPassword | sudo -Si $workingDir/$EOF_SH"
    }

    private String prepareCommand(String cmd) {
        return "${cmdPrefix} '${cmd}'"
    }
}
