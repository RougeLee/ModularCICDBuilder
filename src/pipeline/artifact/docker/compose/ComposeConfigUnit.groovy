package pipeline.artifact.docker.compose

class ComposeConfigUnit {
    final String file
    final ArrayList<String> tags
    final boolean isSudo

    ComposeConfigUnit(String file, ArrayList<String> tags, boolean isSudo = false) {
        this.file = file
        this.tags = tags
        this.isSudo = isSudo
    }
}
