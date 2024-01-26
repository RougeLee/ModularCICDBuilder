package jenkinsscript.steps

abstract class BaseStep {
    Script script

    BaseStep(Script script) {
        this.script = script
    }

    abstract def run(args)
}
