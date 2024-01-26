package pipeline.common.ui.panels

import pipeline.common.ui.Panel
import pipeline.common.util.Config

import java.text.SimpleDateFormat

class DefaultPanel extends Panel {
    private static final String defaultUIParamName = '預設面板'
    private static final String defaultUIParamDesc = '進行建置，下方資料可隨意輸入或採用預設值'

    DefaultPanel(Config config) {
        super(config)
    }

    String getLastValue() {
        return params[defaultUIParamName] as String
    }

    DefaultPanel initUIParam() {
        String description = lastValue ? "上次建置輸入資料為: ${lastValue}，下方資料可隨意輸入或採用預設值" : defaultUIParamDesc
        LinkedHashMap paramMap = [:]
        paramMap.put('name', defaultUIParamName)
        paramMap.put('defaultValue', new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()))
        paramMap.put('description', description)
        config.CUSTOM_PARAMETERS << string(paramMap)
        return this
    }
}
