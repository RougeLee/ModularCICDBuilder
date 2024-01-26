package jenkinsscript.steps

class echo extends BaseStep {

    echo(Script script) {
        super(script)
    }

    def run(Object message) {
        println("${message}")
    }
}
