package pipeline.artifact.docker.cli.compose

@SuppressWarnings('unused')
class ComposeCommandConfig {
    private EDockerComposeCommand command
    private List<String> options = []

    ComposeCommandConfig(EDockerComposeCommand command) {
        this.command = command
    }

    ComposeCommandConfig addOption(String option) {
        options.add(option)
        return this
    }

    String getCommand() {
        return command.toString().toLowerCase()
    }

    List<String> getOptions() {
        return options
    }
}
