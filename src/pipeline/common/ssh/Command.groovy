package pipeline.common.ssh

class Command {
    String originalCmd
    boolean isSudo
    String cmd = null
    String workingDir = null
    boolean skipReturnCode = false

    Command(String cmd, boolean isSudo = false) {
        this.originalCmd = cmd
        this.isSudo = isSudo
    }

    Command setCmd(String cmd) {
        this.cmd = cmd
        return this
    }

    Command setWorkingDir(String workingDir) {
        this.workingDir = workingDir
        return this
    }

    Command setSkipReturnCode(boolean skipReturnCode) {
        this.skipReturnCode = skipReturnCode
        return this
    }
}
