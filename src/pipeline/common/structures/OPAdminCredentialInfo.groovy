package pipeline.common.structures

import pipeline.common.util.BaseStructure

@SuppressWarnings('unused')
class OPAdminCredentialInfo extends BaseStructure {
    private String OPADMIN_CREDENTIAL_ID = null
    private String OPADMIN_USER_NAME = 'SSHRemoteOPAdminUsername'
    private String OPADMIN_PASSWORD = 'SSHRemoteOPAdminPassword'

    OPAdminCredentialInfo(LinkedHashMap config) {
        super(config)
    }

    protected void initProcess() {
        OPADMIN_CREDENTIAL_ID = initProperty(
                'OPADMIN_CREDENTIAL_ID',
                OPADMIN_CREDENTIAL_ID
        )
        ArrayList opadminCredential = OPADMIN_CREDENTIAL_ID != null ? [
                usernamePassword(
                        credentialsId: OPADMIN_CREDENTIAL_ID,
                        usernameVariable: OPADMIN_USER_NAME,
                        passwordVariable: OPADMIN_PASSWORD
                )
        ] : null
        setConfigProperty('OPADMIN_USER_NAME', OPADMIN_USER_NAME)
        setConfigProperty('OPADMIN_PASSWORD', OPADMIN_PASSWORD)
        setConfigProperty('OPADMIN_CREDENTIAL', opadminCredential)
    }
}
