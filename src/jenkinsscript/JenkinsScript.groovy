package jenkinsscript

import jenkinsscript.jenkins.Steps
import jenkinsscript.mock.scripts.Default
import com.cloudbees.groovy.cps.NonCPS

class JenkinsScript extends Steps {

    static {
        Default script = new Default()
        this.script = script
        env = script.env
    }

    @NonCPS
    static def methodMissing(String methodName, args) {
        return $static_methodMissing(methodName, args)
    }

    @NonCPS
    static def $static_methodMissing(String methodName, args) {
        return callMethod(methodName, args)
    }
}
