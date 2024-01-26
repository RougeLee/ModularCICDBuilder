package pipeline.common.ssh

import com.cloudbees.groovy.cps.NonCPS
import pipeline.common.interfaces.IFileTransfer

class SCPFileTransfer extends SSHBase implements IFileTransfer {

    SCPFileTransfer(
            String nodeID,
            LinkedHashSet<String> credentialList,
            String bastionHost,
            String remoteHost
    ) {
        super(nodeID, credentialList, bastionHost, remoteHost)
    }

    String transferFile(TransferFile transferFile) {
        String resultCommand = null
        node(nodeID) {
            transferFile.prepareSourceFileClosure?.call()
            String transferCommand = buildTransferCommand(transferFile)
            try {
                sshagent(credentialList) {
                    withOpadminCredentials {
                        resultCommand = execShellScript(transferCommand)
                    }
                }
            } catch (Exception e) {
                EchoStep(errorMessage(e, transferFile))
                throw e
            } finally {
                transferFile.afterTransferFileClosure?.call()
            }
        }
        EchoStep(successMessage(transferFile))
        return resultCommand
    }

    @NonCPS
    protected void getSSHCommandPrefix() {
        String cmdPrefix = "scp -o ProxyJump=${bastionHost} "
        if (opadminCredential == null) {
            this.cmdPrefix = cmdPrefix
        } else {
            this.cmdPrefix = "sshpass -p \$${OPADMIN_PASSWORD} ${cmdPrefix}"
        }
    }

    private String buildTransferCommand(TransferFile transferFile) {
        return new StringBuilder()
                .append(cmdPrefix)
                .append(transferFile.localFilePath)
                .append(' ')
                .append(remoteHost)
                .append(':')
                .append(transferFile.remoteFilePath)
                .toString()
    }

    private static String successMessage(TransferFile transferFile) {
        return new StringBuilder()
                .append('Transfer file from ')
                .append(transferFile.localFilePath)
                .append(' to ')
                .append(transferFile.remoteFilePath)
                .append(' successfully.')
                .toString()
    }

    private static String errorMessage(Exception e, TransferFile transferFile) {
        return new StringBuilder()
                .append('Failed to transfer file from ')
                .append(transferFile.localFilePath)
                .append(' to ')
                .append(transferFile.remoteFilePath)
                .append("\n${e}")
                .toString()
    }
}
