package pipeline.common.ssh

import pipeline.Pipeline
import pipeline.common.util.Config

abstract class SSHBase extends Pipeline {
    protected final ArrayList SSHRemoteCredential = Config.SSH_REMOTE_CREDENTIAL
    protected final String SSHRemotePasswordKey = Config.SSH_REMOTE_PASSWORD
    protected final ArrayList<String> credentialList
    protected final String nodeID
    protected final String bastionHost
    protected final String remoteHost
    protected String cmdPrefix

    SSHBase(
            String nodeID,
            LinkedHashSet<String> credentialList,
            String bastionHost,
            String remoteHost
    ) {
        this.nodeID = nodeID
        this.credentialList = credentialList as ArrayList<String>
        this.bastionHost = bastionHost
        this.remoteHost = remoteHost
        getSSHCommandPrefix()
    }

    abstract protected void getSSHCommandPrefix()

    protected static String execShellScript(
            String cmd,
            boolean skipReturnCode = false
    ) {
        LinkedHashMap cmdMap = [:]
        cmdMap.script = cmd
        cmdMap.returnStdout = true
        try {
            return sh(cmdMap)
        } catch (e) {
            if (skipReturnCode) {
                return null
            }
            throw e
        }
    }

    protected def withOpadminCredentials(Closure closure) {
        if (SSHRemoteCredential == null) {
            closure()
        }
        return withCredentials(SSHRemoteCredential, closure)
    }

    protected String getENVRemotePassword() {
        return getEnvProperty(SSHRemotePasswordKey)
    }
}
