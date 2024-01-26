package pipeline.common.ssh

class TransferFile {
    final String localFilePath
    final String remoteFilePath
    Closure prepareSourceFileClosure = null
    Closure afterTransferFileClosure = null

    TransferFile(String localFilePath, String remoteFilePath) {
        this.localFilePath = localFilePath
        this.remoteFilePath = remoteFilePath
    }

    TransferFile setPrepareSourceFileClosure(Closure prepareSourceFileClosure) {
        this.prepareSourceFileClosure = prepareSourceFileClosure
        return this
    }

    TransferFile setAfterTransferFileClosure(Closure afterTransferFileClosure) {
        this.afterTransferFileClosure = afterTransferFileClosure
        return this
    }

    TransferFile configureTransferCallbacks(Closure prepareSourceFileClosure, Closure afterTransferFileClosure) {
        setPrepareSourceFileClosure prepareSourceFileClosure
        setAfterTransferFileClosure afterTransferFileClosure
        return this
    }
}
