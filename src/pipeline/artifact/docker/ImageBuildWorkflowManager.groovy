package pipeline.artifact.docker

import pipeline.artifact.docker.cli.DockerCommander

class ImageBuildWorkflowManager {
    private static List<DockerCommander> commanders = []

    static void addCommander(DockerCommander commander) {
        commanders.add(commander)
    }

    static void buildAll() {
        commanders.each { DockerCommander commander ->
            commander.build()
        }
    }

    static void pushAll() {
        commanders.each { DockerCommander commander ->
            commander.push()
        }
    }
}
