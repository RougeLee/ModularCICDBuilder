package pipeline.Infra

import pipeline.Pipeline

class BastionInfoProvider extends Pipeline {
    private static final String Bastion_HOST_SUFFIX = '_HOST'

    static String getBastionHost(String envVarId) {
        String envBastionHost = envVarId + Bastion_HOST_SUFFIX
        String bastionHost = getEnvProperty(envBastionHost)
        checkBastionHost(bastionHost, envVarId)
        return bastionHost
    }

    static checkBastionHost(String bastionHost, String envVarId) {
        if (bastionHost != null) {
            return
        }
        String message = new StringBuilder("envVarId: ")
                .append(envVarId)
                .append("BastionHost not found in Jenkins ENV_VAR.")
                .toString()
        EchoStep(message)
    }
}
