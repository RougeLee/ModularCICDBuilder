package pipeline.Infra

import pipeline.Pipeline

class BastionInfoProvider extends Pipeline {
    private static final String Bastion_HOST_SUFFIX = '-HOST'

    static String getBastionHost(String envVarId) {
        String envKey = "${envVarId}${Bastion_HOST_SUFFIX}"
        String bastionHost = getEnvProperty(envKey)
        checkBastionHost(bastionHost, envVarId)
        return bastionHost
    }

    static void checkBastionHost(String bastionHost, String envVarId) {
        if (bastionHost == null) {
            EchoStep("envVarId: $envVarId not found in Jenkins ENV_VAR.")
            return
        }
        EchoStep("BastionHost: $bastionHost")
    }
}
