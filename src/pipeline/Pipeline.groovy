package pipeline

import jenkinsscript.JenkinsScript
import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper

class Pipeline extends JenkinsScript {
    public RunWrapper currentBuild = (RunWrapper) getJenkinsProperty('currentBuild')
    public LinkedHashMap params = (LinkedHashMap) getJenkinsProperty('params')

    static String getJENKINS_URL() {
        return getEnvProperty('JENKINS_URL')
    }

    static String getJOB_URL() {
        return getEnvProperty('JOB_URL')
    }

    static String getJOB_NAME() {
        String jobName = getEnvProperty('JOB_NAME')
        return URLDecoder.decode(jobName, "UTF-8")
    }

    static String getJOB_BASE_NAME() {
        String jobBaseName = getEnvProperty('JOB_BASE_NAME')
        return URLDecoder.decode(jobBaseName, "UTF-8")
    }

    static String getBRANCH_NAME() {
        return getEnvProperty('BRANCH_NAME')
    }

    static String getBUILD_URL() {
        return getEnvProperty('BUILD_URL')
    }

    static String getBUILD_NUMBER() {
        return getEnvProperty('BUILD_NUMBER')
    }

    /**
     * JenkinsNode Name
     * 這會隨JenkinsNode的不同，值而所不同
     */
    static String getNODE_NAME() {
        return getEnvProperty('NODE_NAME')
    }

    /**
     * the Job WORKSPACE path's Value on the JenkinsNode
     * 這會隨JenkinsNode的不同，值而所不同
     */
    static String getWORKSPACE() {
        return getEnvProperty('WORKSPACE')
    }

    static def getEvaluate(String evaluateName) {
        def result
        try {
            result = script.evaluate(evaluateName)
        } catch (ignored) {
            return null
        }
        return result
    }
}
