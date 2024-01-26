package pipeline.common.interfaces

import pipeline.common.ssh.EOFCommand

interface IEOFCommand {
    String executeEOFCommand(EOFCommand eofCommand)
}
