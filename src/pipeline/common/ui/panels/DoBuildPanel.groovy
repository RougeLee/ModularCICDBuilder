package pipeline.common.ui.panels

import pipeline.common.ui.Panel
import pipeline.common.util.Config

class DoBuildPanel extends Panel {
    private static final String doBuildUIParamName = 'DoBuild'
    private static final String doBuildUIParamDesc = 'Perform a build of the project.'
    private boolean jenkinsfileDoBuild = false

    DoBuildPanel(Config config, boolean jenkinsfileDoBuild) {
        super(config)
        this.jenkinsfileDoBuild = jenkinsfileDoBuild
    }

    Boolean getDoBuild() {
        return params[doBuildUIParamName] as Boolean
    }

    DoBuildPanel initUIParam() {
        LinkedHashMap doBuildBooleanMap = [:]
        doBuildBooleanMap[PARAMS_NAME] = doBuildUIParamName
        doBuildBooleanMap[PARAMS_DESCRIPTION] = doBuildUIParamDesc
        doBuildBooleanMap[PARAMS_DEFAULT_VALUE] = jenkinsfileDoBuild
        config.CUSTOM_PARAMETERS << booleanParam(doBuildBooleanMap)
        if (doBuild == null) {
            entryResetUIStage("${doBuildUIParamName} is null.")
        }
        return this
    }
}
