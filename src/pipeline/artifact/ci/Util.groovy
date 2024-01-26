package pipeline.artifact.ci

import com.cloudbees.groovy.cps.NonCPS
import jenkins.model.Jenkins
import pipeline.Pipeline

class Util extends Pipeline {

    static List createChoicesWithPreviousChoice(List defaultChoices, String previousChoice) {
        if (previousChoice == null) {
            return defaultChoices
        }
        def choices = defaultChoices - previousChoice
        choices.add(0, previousChoice)
        return choices
    }

    /*
    @NonCPS
    static def searchNodeByName(strRegex) {
        Jenkins.instance.slaves
                .grep { it.name ==~ ~"${strRegex}" }
                .collect { it.name.toString() }
    }
    */

    @NonCPS
    static def searchNodeByLabel(strRegex) {
        Jenkins.instanceOrNull.nodes
                .grep { it.labelString ==~ ~"${strRegex}" }
                .collect { it.name.toString() }
    }
}
