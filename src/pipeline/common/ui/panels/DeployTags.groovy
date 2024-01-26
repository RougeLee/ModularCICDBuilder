package pipeline.common.ui.panels

import pipeline.common.ui.Panel
import pipeline.common.util.Config

class DeployTags extends Panel {
    private static final String DeployTagsParamName = 'DEPLOY_TAGS'
    private static final String DeployTagsParamDesc
    private static final String DeployTagsDefaultValue = ''

    static {
        DeployTagsParamDesc = new StringBuilder()
                .append('指定要部署的版本標籤，多個標籤請用逗號分隔。<br>')
                .append('例如：Server, Jumper, Web<br>')
                .append('如果不指定標籤，則會部署所有標籤的版本。')
                .toString()
    }

    DeployTags(Config config) {
        super(config)
    }

    ArrayList<String> getTags() {
        String tags = params[DeployTagsParamName] as String
        if (tags == null) {
            return null
        }
        if (tags.isEmpty()) {
            return []
        }
        return tags.split(',').collect { it.trim() }
    }

    DeployTags initUIParam() {
        LinkedHashMap DeployTagsMap = [:]
        DeployTagsMap[PARAMS_NAME] = DeployTagsParamName
        DeployTagsMap[PARAMS_DESCRIPTION] = DeployTagsParamDesc
        DeployTagsMap[PARAMS_DEFAULT_VALUE] = DeployTagsDefaultValue
        config.CUSTOM_PARAMETERS << string(DeployTagsMap)
        if (tags == null) {
            entryResetUIStage("${DeployTagsParamName} is null.")
        }
        return this
    }
}
