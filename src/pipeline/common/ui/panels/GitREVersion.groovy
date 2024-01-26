package pipeline.common.ui.panels

import pipeline.common.ui.Panel
import pipeline.common.util.Config

class GitREVersion extends Panel {
    private static final String gitREVersionParamName
    private static final String gitREVersionParamDesc
    private static final String defaultGitREVersion = 'none'

    static {
        gitREVersionParamName = 'GIT_REVISION'
        gitREVersionParamDesc = new StringBuilder()
                .append('Specify a branch name or commit hash for checkout.')
                .append(" Default: 'none' to disable.")
                .toString()
    }

    GitREVersion(Config config) {
        super(config)
    }

    boolean getIsGitREVersionNone() {
        return defaultGitREVersion == gitREVersion
    }

    String getGitREVersion() {
        return params[gitREVersionParamName] as String
    }

    GitREVersion initUIParam() {
        Map paramMap = [:]
        paramMap[PARAMS_NAME] = gitREVersionParamName
        paramMap[PARAMS_DESCRIPTION] = gitREVersionParamDesc
        paramMap[PARAMS_DEFAULT_VALUE] = defaultGitREVersion
        config.CUSTOM_PARAMETERS << string(paramMap)
        if (gitREVersion == null) {
            entryResetUIStage("${gitREVersionParamName} is null.")
        }
        return this
    }
}
