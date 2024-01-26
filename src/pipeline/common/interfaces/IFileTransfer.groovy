package pipeline.common.interfaces

import pipeline.common.ssh.TransferFile

interface IFileTransfer {
    String transferFile(TransferFile transferFile)
}
