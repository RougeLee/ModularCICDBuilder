import pipeline.artifact.util.Master

def call(Closure body) {
    def config = GenerateModularConfig.getConfig(this, body)
    def master = new Master(config)
    try {
        master.init()
        master.limitedTimeExecution {
            master.runCIFlow()
            master.runCDFlow()
        }
    } catch (error) {
        master.catchException(error)
    } finally {
        master.onEnd()
    }
}
