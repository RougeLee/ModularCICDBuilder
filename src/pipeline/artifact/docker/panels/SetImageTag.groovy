package pipeline.artifact.docker.panels

import pipeline.common.util.Config
import pipeline.common.ui.Panel

class SetImageTag extends Panel {
    public static final String DEFAULT_TAG = 'none'
    private static final String PARAM_NAME_PREFIX = 'SET_CUSTOM_TAG='
    private LinkedHashSet<String> dockerImages

    SetImageTag(Config config, ArrayList<String> dockerImages = []) {
        super(config)
        this.dockerImages = new LinkedHashSet<String>(dockerImages)
    }

    String getImageTag(String image, isParamsName = false) {
        String name = isParamsName ? image : getParamsName(image)
        return params[name] as String
    }

    SetImageTag initUIParam() {
        if (dockerImages.size() == 0) {
            return this
        }
        applyImageTagParam()
        checkImageTagParam()
        return this
    }

    private void applyImageTagParam() {
        dockerImages.each { String image ->
            String description = new StringBuilder()
                    .append("Docker Image: ${image}<br>")
                    .append("設置 ${DEFAULT_TAG} 不建立Image Tag<br>")
                    .append("多個Tag需求，以,符號分隔 例如: 1.0.0, dev")
                    .toString()
            Map paramMap = [:]
            paramMap[PARAMS_NAME] = getParamsName(image)
            paramMap[PARAMS_DESCRIPTION] = description
            paramMap[PARAMS_DEFAULT_VALUE] = DEFAULT_TAG
            config.CUSTOM_PARAMETERS << string(paramMap)
        }
    }

    private void checkImageTagParam() {
        Map notValueMap = [:]
        for (String image : dockerImages) {
            String paramsName = getParamsName(image)
            String imageTags = getImageTag(paramsName, true)
            if (imageTags == null) {
                notValueMap[paramsName] = image
            }
        }
        if (notValueMap.size() == 0) {
            return
        }
        StringBuilder sb = new StringBuilder('Reset UI Parameters:\n')
        for (String paramsName : notValueMap.keySet()) {
            String image = notValueMap[paramsName]
            sb.append('Image: ').append(image).append(', ')
            sb.append('ParamsName: ').append(paramsName).append('\n')
        }
        entryResetUIStage(sb.toString())
    }

    private static String getParamsName(String image) {
        return new StringBuilder(PARAM_NAME_PREFIX).append(image).toString()
    }
}
