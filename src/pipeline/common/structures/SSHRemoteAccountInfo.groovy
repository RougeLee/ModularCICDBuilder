package pipeline.common.structures

import pipeline.common.util.BaseStructure
import pipeline.common.util.Config

@SuppressWarnings('unused')
class SSHRemoteAccountInfo extends BaseStructure {
    private static String SSH_REMOTE_CREDENTIAL_ID = null

    SSHRemoteAccountInfo(LinkedHashMap config) {
        super(config)
    }

    protected void structureInitProcess() {
        SSH_REMOTE_CREDENTIAL_ID = initProperty('SSH_REMOTE_CREDENTIAL_ID',
                SSH_REMOTE_CREDENTIAL_ID)
        setConfigProperty('SSH_REMOTE_USER_NAME', SSHRemoteAccountUserNameKey)
        setConfigProperty('SSH_REMOTE_PASSWORD', SSHRemoteAccountPasswordKey)
        setConfigProperty('SSH_REMOTE_CREDENTIAL', usernamePasswordCredential)
    }

    static void resetConfigSSHRemoteAccountInfo(String sshRemoteCredentialID) {
        SSH_REMOTE_CREDENTIAL_ID = sshRemoteCredentialID
        Config.SSH_REMOTE_USER_NAME = SSHRemoteAccountUserNameKey
        Config.SSH_REMOTE_PASSWORD = SSHRemoteAccountPasswordKey
        Config.SSH_REMOTE_CREDENTIAL = usernamePasswordCredential
    }

    static ArrayList getUsernamePasswordCredential() {
        if (SSH_REMOTE_CREDENTIAL_ID == null) {
            return null
        }
        LinkedHashMap credentialMap = [:]
        credentialMap.credentialsId = SSH_REMOTE_CREDENTIAL_ID
        credentialMap.usernameVariable = SSHRemoteAccountUserNameKey
        credentialMap.passwordVariable = SSHRemoteAccountPasswordKey
        return [usernamePassword(credentialMap)]
    }

    static String getSSHRemoteAccountUserNameKey() {
        return getSSHRemoteAccountConst('Username')
    }

    static String getSSHRemoteAccountPasswordKey() {
        return getSSHRemoteAccountConst('Password')
    }

    static String getSSHRemoteAccountConst(String keyword) {
        return SSH_REMOTE_CREDENTIAL_ID ?
                "SSHRemote${SSH_REMOTE_CREDENTIAL_ID}${keyword}" :
                null
    }
}
