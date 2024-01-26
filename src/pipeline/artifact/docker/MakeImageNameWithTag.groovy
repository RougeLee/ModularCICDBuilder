package pipeline.artifact.docker

import pipeline.artifact.docker.cli.DockerCommander
import pipeline.artifact.docker.cli.build.BuildConfig
import pipeline.artifact.docker.cli.push.PushConfig
import pipeline.artifact.docker.panels.SetImageTag
import pipeline.Pipeline
import pipeline.common.util.Config

class MakeImageNameWithTag extends Pipeline {
    private static final String TagLatest = 'latest'
    private Config config
    private String image
    private String dockerfileName
    private DockerImageNameMaker dockerImageNameMaker

    MakeImageNameWithTag(Config config, String image, String dockerfileName) {
        this.config = config
        this.image = image
        this.dockerfileName = dockerfileName
        this.dockerImageNameMaker = config.DOCKER_IMAGE_NAME_MAKER
    }

    void main() {
        String dockerImage = dockerImageNameMaker.getDockerImage(image)
        BuildConfig buildConfig = new BuildConfig(dockerImage)
                .addTag(TagLatest)
                .setDockerfilePath(dockerfileName)
        ArrayList dockerPushConfigs = [
                new PushConfig(dockerImage).setTag(TagLatest)
        ]
        dockerImageTags.each { String tag ->
            tag = tag.trim()
            buildConfig.addTag(tag)
            dockerPushConfigs << new PushConfig(dockerImage).setTag(tag)
        }
        def dockerCommander = new DockerCommander(buildConfig, dockerPushConfigs)
        ImageBuildWorkflowManager.addCommander(dockerCommander)
    }

    private LinkedHashSet<String> getDockerImageTags() {
        def setImageTag = new SetImageTag(config)
        String uiValue = setImageTag.getImageTag(image)
        if (uiValue == null || uiValue.isEmpty()) {
            return []
        }
        ArrayList<String> tags = uiValue.split(/\./)
        if (tags.contains(setImageTag.DEFAULT_TAG)) {
            if (tags.size() > 1) {
                String errorMessage = new StringBuilder()
                        .append("DockerImageTags: ${image}, ")
                        .append("${setImageTag.DEFAULT_TAG}不可與其他tag同時存在")
                        .toString()
                throw new Exception(errorMessage)
            }
            tags = []
        }
        if (tags.contains(TagLatest)) {
            tags.remove(TagLatest)
        }
        return new LinkedHashSet<>(tags)
    }
}
