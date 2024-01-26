package pipeline.common.ui

import pipeline.common.util.Config
import pipeline.Pipeline

abstract class Panel extends Pipeline {
    protected static final String PARAMS_NAME = 'name'
    protected static final String PARAMS_DESCRIPTION = 'description'
    protected static final String PARAMS_DEFAULT_VALUE = 'defaultValue'
    protected static final String PARAMS_CHOICES = 'choices'

    protected final Config config

    Panel(Config config) {
        this.config = config
    }

    abstract initUIParam()

    protected void entryResetUIStage(String message) {
        stage('Reset UI Parameters') {
            EchoStep(message)
            config.SKIP_ALL_FLOW = true
        }
    }
}
