package pipeline.artifact.docker.compose

import pipeline.artifact.docker.IVariable
import pipeline.Pipeline

class ComposeService extends Pipeline implements IVariable {
    private static final String serviceLevelKeyRegex = /^[a-z0-9]+(_[a-z0-9]+)*$/
    private static final ArrayList<EDockerComposeServiceLevelKey> serviceKeys
    private static final String environmentKey
    private final LinkedHashMap<String, Serializable> service = [:]

    static {
        serviceKeys = EDockerComposeServiceLevelKey.values()
        environmentKey = EDockerComposeServiceLevelKey.environment.name()
    }

    LinkedHashMap<String, Serializable> getService() {
        return service
    }

    ComposeService setVariable(String key, Serializable value) {
        if (!key.matches(serviceLevelKeyRegex)) {
            throw new IllegalArgumentException("Invalid key: ${key}")
        }
        if (value == null || value.toString().isEmpty()) {
            throw new IllegalArgumentException("Invalid value: ${value}")
        }
        if (!checkServiceKey(key)) {
            EchoStep("Warning: ${key} is not a valid service key.")
        }
        service[key] = prepareComposeValue(value)
        return this
    }

    void validateRequiredVariables() {
        if (service[EDockerComposeServiceLevelKey.image as String] == null &&
                service[EDockerComposeServiceLevelKey.build as String] == null) {
            throw new Exception("image or build is null.\n${service}")
        }
    }

    ComposeService setEnvironmentVariable(String key, String value) {
        def environment = service[environmentKey] as Map<String, String>
        if (environment == null) {
            environment = [:]
            service[environmentKey] = environment
        }
        environment[key] = value
        return this
    }

    private static Serializable prepareComposeValue(def value) {
        switch (value.getClass()) {
            case String:
                return ensureQuoted(value as String)
            case List:
                return value.collect {
                    it instanceof String ? ensureQuoted(it) : it as Serializable
                } as ArrayList
            case Map:
                return value.collectEntries { String key, def val ->
                    [(key): (val instanceof String ?
                            ensureQuoted(val) :
                            val as Serializable)]
                }
            default:
                return value
        }
    }

    private static String ensureQuoted(String str) {
        return isQuoted(str) ? str : "\"${str}\""
    }

    private static boolean isQuoted(String str) {
        str = str.trim()
        return str.startsWith("\"") && str.endsWith("\"")
    }

    private static boolean checkServiceKey(String key) {
        for (EDockerComposeServiceLevelKey serviceKey : serviceKeys) {
            if (serviceKey.name() == key) {
                return true
            }
        }
        return false
    }
}
