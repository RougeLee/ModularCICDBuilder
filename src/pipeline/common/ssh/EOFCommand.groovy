package pipeline.common.ssh

import com.cloudbees.groovy.cps.NonCPS

class EOFCommand {
    protected static final String NEW_LINE_SYMBOL = '\n'
    final String shellScriptContent
    boolean isSudo = false
    String workingDir = null

    EOFCommand(String shellScriptContent) {
        this.shellScriptContent = escapeShellScript(shellScriptContent)
    }

    EOFCommand(String shellScriptContent, boolean isSudo) {
        this.shellScriptContent = shellScriptContent
        this.isSudo = isSudo
    }

    EOFCommand(List<String> codes) {
        this.shellScriptContent = eachEscapeCodes(codes)
    }

    EOFCommand(List<String> codes, boolean isSudo) {
        this.shellScriptContent = eachEscapeCodes(codes)
        this.isSudo = isSudo
    }

    EOFCommand setWorkingDir(String workingDir) {
        this.workingDir = workingDir
        return this
    }

    @NonCPS
    private static String eachEscapeCodes(List<String> codes) {
        StringBuilder sb = new StringBuilder()
        codes.each { String code ->
            sb.append(escapeShellScript(code))
            sb.append(NEW_LINE_SYMBOL)
        }
        return sb.toString().trim()
    }

    @NonCPS
    private static String escapeShellScript(String scriptContent) {
        return scriptContent
                .replaceAll('\\$', '\\\\\\$')
                .replaceAll("`", "\\\\`")
                .replaceAll("!", "\\\\!")
    }
}
