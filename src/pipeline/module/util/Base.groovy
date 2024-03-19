package pipeline.module.util

import pipeline.Pipeline

abstract class Base extends Pipeline {
    protected ArrayList requiredArgNames = []

    protected void checkRequiredArgs(LinkedHashMap argsMap) {
        for (String argName : requiredArgNames) {
            if (argsMap.containsKey(argName)) {
                continue
            }
            String message = "Required argument '$argName' is missing"
            throw new IllegalArgumentException(message)
        }
    }
}
