package pipeline.common.ui.panels

import pipeline.common.ui.Panel
import pipeline.common.util.Config

class SudoChoice extends Panel {
    private static final String sudoChoiceParamName = 'SUDO_CHOICE'
    private static final String sudoChoiceParamDesc = '選擇是否使用sudo執行指令'
    private static final ArrayList<Boolean> sudoChoiceList = [false, true]

    SudoChoice(Config config) {
        super(config)
    }

    Boolean getSudoChoice() {
        return params[sudoChoiceParamName] as Boolean
    }

    SudoChoice initUIParam() {
        LinkedHashMap sudoChoiceMap = [:]
        sudoChoiceMap[PARAMS_NAME] = sudoChoiceParamName
        sudoChoiceMap[PARAMS_DESCRIPTION] = sudoChoiceParamDesc
        sudoChoiceMap[PARAMS_CHOICES] = sudoChoiceList
        config.CUSTOM_PARAMETERS << choice(sudoChoiceMap)
        if (sudoChoice == null) {
            entryResetUIStage("${sudoChoiceParamName} is null.")
        }
        return this
    }
}
