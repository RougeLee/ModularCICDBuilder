package pipeline.common.ui.panels

import pipeline.common.ui.Panel
import pipeline.common.util.Config

class BuildNode extends Panel {
    public static final String buildNodeParamName = 'SELECT_BUILD_NODE'
    private static final String buildNodeParamDesc = '選擇要建置的Jenkins服務器節點'

    BuildNode(Config config) {
        super(config)
    }

    String getSelectedNode() {
        return params[buildNodeParamName] as String
    }

    BuildNode initUIParam() {
        LinkedHashMap buildNodeChoiceMap = [:]
        buildNodeChoiceMap[PARAMS_NAME] = buildNodeParamName
        buildNodeChoiceMap[PARAMS_CHOICES] = config.NODE_LIST.join('\n')
        buildNodeChoiceMap[PARAMS_DESCRIPTION] = buildNodeParamDesc
        config.CUSTOM_PARAMETERS << choice(buildNodeChoiceMap)
        if (selectedNode == null) {
            entryResetUIStage("${buildNodeParamName} is null.")
        }
        return this
    }
}
