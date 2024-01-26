package pipeline.artifact.docker.compose

class ComposeServiceUnit {
    final DeployVariables deployVariables
    final ComposeUnit composeUnit
    final ComposeService composeService

    ComposeServiceUnit(DeployVariables deployVariables, ComposeUnit composeUnit, ComposeService composeService) {
        this.deployVariables = deployVariables
        this.composeUnit = composeUnit
        this.composeService = composeService
    }
}
