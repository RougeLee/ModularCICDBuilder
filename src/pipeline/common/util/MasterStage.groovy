package pipeline.common.util

import pipeline.common.constants.JenkinsNodes
import pipeline.Pipeline
import pipeline.common.constants.CodeType
import pipeline.common.constants.FlowType

abstract class MasterStage extends Pipeline {
    static final ArrayList<Closure> endClosures = []
    protected TimeoutHandler timeoutHandler
    protected int limitCount = 5
    protected Config config
    protected String stageName = 'Master init'

    MasterStage(Config config) {
        this.config = config
        timeoutHandler = new TimeoutHandler(config)
    }

    abstract void customInit()

    abstract void catchException(Throwable e)

    void limitedTimeExecution(Closure body) {
        timeoutHandler.setTimeout()
        timeoutHandler.call(body)
    }

    void init() {
        stage(stageName) {
            customInit()
            syncConfigFieldsToMap()
        }
    }

    static void onEnd() {
        for (Closure closure : endClosures) {
            closure()
        }
    }

    protected void syncConfigFieldsToMap() {
        config.fillingConfigFieldsToMapValue()
        EchoStep(config.getConstConfig())
    }

    protected void platformLibInit() {
        NodeTypeInit()
        flowTypeInit()
    }

    protected void NodeTypeInit() {
        config.BUILD_NODE = JenkinsNodes.CONTROL
        config.DEPLOY_NODE = JenkinsNodes.CONTROL
    }

    protected void flowTypeInit() {
        switch (config.CODE_TYPE) {
            case CodeType.INFRA:
                config.FLOW = FlowType.INFRA
                break
            case CodeType.ARTIFACT:
                if (config.ARTIFACT_TYPE == FlowType.CI) {
                    config.FLOW = FlowType.CI
                }
                break
            case CodeType.CONFIG:
                config.FLOW = FlowType.CONFIG
                break
            default:
                config.FLOW = FlowType.NONE
                break
        }
    }

    protected void echoException(Throwable e) {
        def stackTraceMessageBuilder = new StringBuilder(e.toString())
        ArrayList stackTrace = e.getStackTrace()
        if (stackTrace.size() > 0) {
            stackTraceMessageBuilder.append("\n\nError StackTrace :")
            int limit = (stackTrace.size() > limitCount) ?
                    limitCount :
                    stackTrace.size() - 1
            for (int i = 0; i < limit; i++) {
                stackTraceMessageBuilder.append("\n${stackTrace[i]}")
            }
        }
        stackTraceMessageBuilder.append("\n")
        error(stackTraceMessageBuilder.toString())
    }

    protected void settingProperties() {
        if (config.CUSTOM_PARAMETERS.size() > 0) {
            config.CUSTOM_PROPERTIES.add(parameters(config.CUSTOM_PARAMETERS))
        } else {
            EchoStep('allParameters.size() = 0, skipped.')
            return
        }
        //noinspection GroovyAssignabilityCheck
        properties(config.CUSTOM_PROPERTIES)
    }
}
