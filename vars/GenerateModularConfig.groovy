import jenkinsscript.JenkinsScript
import pipeline.common.util.Config

static Config getConfig(Script script, Closure body) {
    JenkinsScript.setJenkinsMode(script)
    LinkedHashMap bodyToMap = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = bodyToMap
    body()
    checkConfigMap(bodyToMap)
    def config = new Config(bodyToMap)
    config.initialize()
    return config
}

private static void checkConfigMap(Map config) {
    def stringBuilder = new StringBuilder("Source Config:\n")
    config.each { key, value ->
        stringBuilder.append("${key} = ${value}\n")
    }
    JenkinsScript.EchoStep(stringBuilder.toString())
}
