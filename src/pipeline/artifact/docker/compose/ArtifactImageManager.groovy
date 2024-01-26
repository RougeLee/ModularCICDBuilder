package pipeline.artifact.docker.compose

import pipeline.Pipeline
import pipeline.artifact.docker.compose.EDockerComposeServiceLevelKey as SK
import pipeline.artifact.docker.compose.ServiceConfigurationSetup as Setup
import pipeline.artifact.docker.DockerImageNameMaker
import pipeline.artifact.docker.panels.SpecifyImageTag

class ArtifactImageManager extends Pipeline {
    public static final String NOT_ARTIFACT_IMAGE = 'none'
    private static final String KEY_IMAGE = SK.image.name()
    private final DockerImageNameMaker imageNameMaker
    private final SpecifyImageTag specifyImageTag
    private final ComposeServiceUnit serviceUnit

    ArtifactImageManager(
            DockerImageNameMaker imageNameMaker,
            SpecifyImageTag specifyImageTag,
            ComposeServiceUnit serviceUnit
    ) {
        this.imageNameMaker = imageNameMaker
        this.specifyImageTag = specifyImageTag
        this.serviceUnit = serviceUnit
    }

    void checkArtifactImage() {
        if (artifactImage == NOT_ARTIFACT_IMAGE) {
            return
        }
        String artifactTag = getArtifactTag()
        String imageWithTag = (artifactTag != specifyImageTag.DEFAULT_TAG && artifactTag != null) ?
                "${fullImageName}:${artifactTag}" :
                "${fullImageName}:${customTag}"
        EchoStep("Using artifact imageWithTag: ${imageWithTag}, ${artifactImage} ${artifactTag}, ${customTag}")
        serviceUnit.composeService.setVariable(KEY_IMAGE, imageWithTag)
    }

    private String getArtifactImage() {
        return serviceUnit.composeUnit.artifactImage
    }

    private String getFullImageName() {
        return imageNameMaker.getDockerImage(artifactImage)
    }

    private String getArtifactTag() {
        String serviceName = Setup.getServiceName(serviceUnit.composeUnit)
        return specifyImageTag.getArtifactTag(serviceName)
    }

    private String getCustomTag() {
        String customTag = serviceUnit.deployVariables.CUSTOM_DOCKER_IMAGE_TAG
        if (customTag == null || customTag.isEmpty()) {
            return 'latest'
        }
        return customTag
    }
}
