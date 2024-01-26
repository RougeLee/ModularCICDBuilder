package pipeline.common.util

import groovy.json.JsonOutput
import pipeline.Pipeline

class ToJsonFile extends Pipeline {

    static void convert(Map mapValue, String filePath) {
        String json = JsonOutput.toJson(mapValue)
        json = JsonOutput.prettyPrint(json)
        writeFile(file: filePath, text: JsonOutput.prettyPrint(json))
    }
}
