package pipeline.artifact.docker.panels

import pipeline.common.util.Config
import pipeline.common.ui.Panel

class SpecifyImageTag extends Panel {
    public static final String DEFAULT_TAG = 'UseJenkinsfileSetting'
    private static final String PARAM_NAME_PREFIX = 'SPECIFY_CUSTOM_TAG='
    private LinkedHashSet<String> artifactServices

    SpecifyImageTag(Config config, LinkedHashSet<String> artifactServices = []) {
        super(config)
        this.artifactServices = artifactServices
    }

    String getArtifactTag(String service, isParamsName = false) {
        String name = isParamsName ? service : getParamsName(service)
        return params[name] as String
    }

    SpecifyImageTag initUIParam() {
        if (artifactServices.size() == 0) {
            return this
        }
        applyImageTagParam()
        checkImageTagParam()
        return this
    }

    private void applyImageTagParam() {
        artifactServices.each { String artifactService ->
            String description = new StringBuilder()
                    .append("Artifact Service: ${artifactService}<br>")
                    .append("設置 ${DEFAULT_TAG} 不指定Image Tag來啟動服務<br>")
                    .toString()
            Map paramMap = [:]
            paramMap[PARAMS_NAME] = getParamsName(artifactService)
            paramMap[PARAMS_DESCRIPTION] = description
            paramMap[PARAMS_DEFAULT_VALUE] = DEFAULT_TAG
            config.CUSTOM_PARAMETERS << string(paramMap)
        }
    }

    private void checkImageTagParam() {
        Map notValueMap = [:]
        for (String service : artifactServices) {
            String paramsName = getParamsName(service)
            String serviceTag = getArtifactTag(paramsName, true)
            if (serviceTag == null) {
                notValueMap[paramsName] = service
            }
        }
        checkEntryResetUIStage(notValueMap)
    }

    private void checkEntryResetUIStage(Map notValueMap) {
        if (notValueMap.size() == 0) {
            return
        }
        StringBuilder sb = new StringBuilder('Reset UI Parameters:\n')
        for (String paramsName : notValueMap.keySet()) {
            String service = notValueMap[paramsName]
            sb.append('Service: ').append(service).append(', ')
            sb.append('ParamsName: ').append(paramsName).append('\n')
        }
        entryResetUIStage(sb.toString())
    }

    private static String getParamsName(String image) {
        return new StringBuilder(PARAM_NAME_PREFIX).append(image).toString()
    }
}
