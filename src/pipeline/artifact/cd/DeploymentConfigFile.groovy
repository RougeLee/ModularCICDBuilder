package pipeline.artifact.cd

class DeploymentConfigFile {
    final String file
    final String fileExtension
    final String fileContent

    DeploymentConfigFile(
            String file,
            String fileExtension,
            String fileContent
    ) {
        this.file = file
        this.fileExtension = fileExtension
        this.fileContent = fileContent
    }
}
