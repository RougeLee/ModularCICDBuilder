package pipeline.common.structures

import pipeline.artifact.util.DoBuildHandler
import pipeline.common.util.BaseStructure

@SuppressWarnings('unused')
class CommonArg extends BaseStructure {
    private static final String JENKINSFILE_DO_BUILD = 'DO_BUILD'
    private static final String JENKINSFILE_OPEN_BUILD_UI = 'OPEN_BUILD_UI'

    CommonArg(LinkedHashMap config) {
        super(config)
    }

    protected void structureInitProcess() {
        initProperty('CRON_EXPRESSION', 'H H(0-6) * * *')
        boolean doBuild = (config.containsKey(JENKINSFILE_DO_BUILD)) ?
                config[JENKINSFILE_DO_BUILD] as boolean : false
        boolean openBuildUI = (config.containsKey(JENKINSFILE_OPEN_BUILD_UI)) ?
                config[JENKINSFILE_OPEN_BUILD_UI] as boolean : false
        setConfigProperty('DO_BUILD_HANDLER', new DoBuildHandler(doBuild, openBuildUI))
    }
}
