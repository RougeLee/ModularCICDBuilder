package pipeline.common.ssh

import pipeline.Pipeline
import pipeline.common.util.MasterStage
import pipeline.Infra.BastionInfoProvider

class SSHKeyHandler extends Pipeline {
    private static final String KEYGEN = 'ssh-keygen'
    private static final String KEYSCAN = 'ssh-keyscan'
    private static final String KNOWN_HOSTS = '~/.ssh/known_hosts'
    private static final LinkedHashMap<String, ArrayList<String>> remoteHostMap = [:]
    private static final LinkedHashMap<String, String> bastionInfoMap = [:]
    private static boolean isRegisterEnd = false

    static String getBastionHost(
            String nodeId,
            String credentialID,
            String remoteHost
    ) {
        checkRegisterMasterEnd()
        if (!bastionInfoMap.containsKey(credentialID)) {
            checkBastionHostSSHKey(nodeId, credentialID)
        }
        registerRemoteHostSSHKey(nodeId, credentialID, remoteHost)
        return bastionInfoMap[credentialID]
    }

    private static void checkRegisterMasterEnd() {
        if (isRegisterEnd) {
            return
        }
        registerMasterEnd()
        isRegisterEnd = true
    }

    private static void registerMasterEnd() {
        Closure removeSSHKeyClosure = {
            String nodeId, ArrayList<String> remoteHostMap ->
                node(nodeId) {
                    remoteHostMap.each { String remoteHost ->
                        removeRemoteHostSSHKey(remoteHost)
                    }
                }
        }
        MasterStage.endClosures << { remoteHostMap.each(removeSSHKeyClosure) }
    }

    private static void checkBastionHostSSHKey(String nodeId, String credentialID) {
        String bastionHost = BastionInfoProvider.getBastionHost(credentialID)
        bastionInfoMap[credentialID] = bastionHost
        if (bastionHost == null) {
            return
        }
        node(nodeId) {
            if (shStdout("$KEYGEN -F $bastionHost || true") != null) {
                return
            }
            String cmd = "$KEYSCAN -t rsa $bastionHost"
            if (shStdout(cmd) == null) {
                throw new Exception("BastionHost: $bastionHost $KEYSCAN Result is null.")
            }
            sh "${cmd} >> $KNOWN_HOSTS"
            if (shStdout("$KEYGEN -F $bastionHost || true") == null) {
                throw new Exception("BastionHost: $bastionHost $KEYGEN -F Result is null.")
            }
        }
    }

    private static void registerRemoteHostSSHKey(
            String nodeId,
            String credentialID,
            String remoteHost
    ) {
        if (remoteHostMap[nodeId]?.contains(remoteHost)) {
            return
        }
        node(nodeId, getRegisterRemoteHostSSHKeyClosure(
                credentialID,
                bastionInfoMap[credentialID],
                remoteHost
        ))
        if (!remoteHostMap.containsKey(nodeId)) {
            remoteHostMap[nodeId] = new ArrayList<String>()
        }
        remoteHostMap[nodeId].add(remoteHost)
    }

    private static Closure getRegisterRemoteHostSSHKeyClosure(
            String bastionCredentialId,
            String bastionHost,
            String remoteHost
    ) {
        return {
            removeRemoteHostSSHKey(remoteHost)
            RegisterRemoteHost(bastionCredentialId, bastionHost, remoteHost)
            if (shStdout("$KEYGEN -F $remoteHost || true") == null) {
                throw new Exception("RemoteHost: $remoteHost $KEYGEN -F Result is null.")
            }
        }
    }

    private static void RegisterRemoteHost(
            String bastionCredentialId,
            String bastionHost,
            String remoteHost
    ) {
        String cmd = "$KEYSCAN -t rsa $remoteHost"
        if (bastionHost == null) {
            executeRegisterRemoteHost(cmd, remoteHost)
            return
        }
        sshagent([bastionCredentialId]) {
            cmd = "ssh $bastionHost \"$cmd\""
            executeRegisterRemoteHost(cmd, remoteHost)
        }
    }

    private static void executeRegisterRemoteHost(String cmd, String remoteHost) {
        if (shStdout(cmd) == null) {
            throw new Exception("RemoteHost: $remoteHost $KEYSCAN Result is null.")
        }
        sh "$cmd >> $KNOWN_HOSTS"
    }

    private static void removeRemoteHostSSHKey(String remoteHost) {
        shStdout("$KEYGEN -R $remoteHost 2>&1")
    }

    private static String shStdout(String cmd) {
        try {
            String cmdResult = sh script: cmd, returnStdout: true
            if (cmdResult == '') {
                cmdResult = null
            }
            return cmdResult
        } catch (ignore) {
            return null
        }
    }
}
