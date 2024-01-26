package pipeline.common.util

import pipeline.Pipeline

class TimeoutHandler extends Pipeline {
    private Config config
    private int timeout = 1

    TimeoutHandler(Config config) {
        this.config = config
    }

    void setTimeout(int timeout = this.timeout, boolean isForce = false) {
        if (config.PIPELINE_TIMEOUT > 0 && !isForce) {
            timeout = config.PIPELINE_TIMEOUT
        }
        this.timeout = timeout
    }

    void call(Closure body) {
        LinkedHashMap timeoutConfigMap = [
                time: timeout,
                unit: 'HOURS'
        ]
        EchoStep("Pipeline execute time limit: ${timeout} Hours")
        timeout(timeoutConfigMap, body)
    }
}
