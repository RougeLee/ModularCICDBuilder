package pipeline.module.util

import pipeline.Pipeline

abstract class Base extends Pipeline {
    protected ArrayList argNameList = []

    protected void verifyArgDict(LinkedHashMap argDict) {
        for (String argName : argNameList) {
            assert argDict.containsKey(argName): "argName = ${argName} is not defined"
        }
    }
}
