package pipeline.common.ssh

import com.cloudbees.groovy.cps.NonCPS
import pipeline.Pipeline
import pipeline.common.util.Config

abstract class SSHBase extends Pipeline {
    protected static final String OPADMIN_PASSWORD = Config.OPADMIN_PASSWORD
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

    protected static def withOpadminCredentials(Closure closure) {
        if (opadminCredential == null) {
            closure()
        }
        return withCredentials(opadminCredential, closure)
    }

    @NonCPS
    protected static ArrayList getOpadminCredential() {
        return Config.OPADMIN_CREDENTIAL
    }

    protected static String getEnvOpadminPassword() {
        return getEnvProperty(OPADMIN_PASSWORD)
    }
}
