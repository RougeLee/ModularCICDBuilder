package pipeline.artifact.docker.cli

import pipeline.Pipeline
import pipeline.artifact.docker.cli.build.BuildConfig
import pipeline.artifact.docker.cli.push.PushConfig

class DockerCommander extends Pipeline {
    private BuildConfig buildConfig
    private List<PushConfig> pushConfigs

    DockerCommander(BuildConfig buildConfig, List<PushConfig> pushConfigs) {
        this.buildConfig = buildConfig
        this.pushConfigs = pushConfigs
    }

    void build() {
        new CommandActuator(buildConfig).run()
    }

    void push() {
        pushConfigs.each { PushConfig pushConfig ->
            new CommandActuator(pushConfig).run()
        }
    }
}
