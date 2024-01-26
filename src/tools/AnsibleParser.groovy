package tools

import com.cloudbees.groovy.cps.NonCPS
import net.sf.json.groovy.JsonSlurper

class AnsibleParser {
    private static final String PLAY_TASK_REGEX = /PLAY |TASK /
    private static final String NEW_LINE_SPLIT_REGEX = /\n|\r\n/
    private static final String LINE_REGEX = /([^]]+): \[([^]]+)] => ([^]]+)/
    private static final String MSG_KEY_WORD = "\"msg\": "
    private static final int MSG_KEY_WORD_LENGTH = MSG_KEY_WORD.length()
    private static final String OK = 'ok'
    private String targetTaskName
    private String ansiblePlaybookResult
    private String targetTask
    private LinkedHashMap resultMap = [:]

    AnsibleParser(String targetTaskName, String ansiblePlaybookResult) {
        this.targetTaskName = targetTaskName
        this.ansiblePlaybookResult = ansiblePlaybookResult
        setTargetTask()
    }

    void reset(String targetTaskName, String ansiblePlaybookResult) {
        this.targetTaskName = targetTaskName
        this.ansiblePlaybookResult = ansiblePlaybookResult
        setTargetTask()
        resultMap = [:]
    }

    @NonCPS
    void setTargetTask() {
        targetTask = /TASK \[.*:?.*${targetTaskName}.*\].*/
    }

    LinkedHashMap executeParse() {
        if (ansiblePlaybookResult.length() == 0) {
            return null
        }
        ArrayList<String> targetTaskLines = []
        String[] resultLines = ansiblePlaybookResult.split(NEW_LINE_SPLIT_REGEX)
        boolean isTargetTask = false
        for (int i = 0; i < resultLines.length; i++) {
            String line = resultLines[i]
            if (line.matches(targetTask)) {
                isTargetTask = true
                continue
            }
            if (!isTargetTask) {
                continue
            }
            if (line =~ PLAY_TASK_REGEX) {
                break
            }
            targetTaskLines << line.trim()
        }
        parseTargetTaskLines(targetTaskLines)
        return resultMap
    }

    private parseTargetTaskLines(ArrayList<String> targetTaskLines) {
        LinkedHashMap<String, StringBuilder> hostOutputMap = [:]
        processLine(targetTaskLines, hostOutputMap)
        hostOutputMap.each { String host, StringBuilder output ->
            resultMap[host] = getProcessHostOutput(output.toString())
        }
    }

    private static void processLine(
            ArrayList<String> targetTaskLines,
            LinkedHashMap<String, StringBuilder> hostOutputMap
    ) {
        StringBuilder currentBuilder = null
        for (String line : targetTaskLines) {
            def matcher = line =~ LINE_REGEX
            def size = matcher.size()
            if (currentBuilder && size == 0) {
                currentBuilder.append(line)
                continue
            }
            for (int i = 0; i < size; i++) {
                def match = matcher[i] as String[]
                if (match[1] != OK) {
                    currentBuilder = null
                    break
                }
                String currentHost = match[2]
                currentBuilder = new StringBuilder()
                hostOutputMap[currentHost] = currentBuilder
                currentBuilder.append(match[3])
            }
        }
    }

    private static def getProcessHostOutput(String output) {
        int index = output.indexOf(MSG_KEY_WORD)
        if (index == -1) {
            return
        }
        int startIndex = index + MSG_KEY_WORD_LENGTH
        int endIndex = output.endsWith("}") ? output.length() - 1 : output.length()
        String msgJsonString = output.substring(startIndex, endIndex)
        return getParseJsonString(msgJsonString)
    }

    private static def getParseJsonString(String jsonString) {
        try {
            LinkedHashMap currentMap = [:]
            currentMap << new JsonSlurper().parseText(jsonString)
            return currentMap
        } catch (ignored) {
            return jsonString
        }
    }
}
