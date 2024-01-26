package pipeline.artifact.docker.compose

import pipeline.artifact.docker.compose.EComposeUnitArgKey as ArgKey

class ComposeUnit {
    private static final String ARTIFACT_IMAGE = ArgKey.ArtifactImage.name()
    private static final String DEPLOY_TAGS = ArgKey.DeployTags.name()
    private static final String DEPLOY_CONFIG = ArgKey.DeployConfig.name()
    private static final String SERVICE_YAML = ArgKey.ServiceYaml.name()
    private String artifactImage
    private ArrayList<String> tags
    private String deploy
    private String service

    ComposeUnit(Map dockerComposeFile) {
        String artifactImage = dockerComposeFile[ARTIFACT_IMAGE]
        if (artifactImage == null) {
            artifactImage = ArtifactImageManager.NOT_ARTIFACT_IMAGE
        }
        ArrayList<String> tags = dockerComposeFile[DEPLOY_TAGS] as ArrayList<String>
        if (tags == null) {
            tags = []
        }
        String deploy = dockerComposeFile[DEPLOY_CONFIG]
        if (deploy != null && deploy.split(/\./).size() == 1) {
            throw new RuntimeException("DeployConfig: not support '${deploy}' yet.")
        }
        String service = dockerComposeFile[SERVICE_YAML]
        if (service == null || service.isEmpty()) {
            throw new RuntimeException("ServiceYaml: is null or empty.")
        }
        this.artifactImage = artifactImage
        this.tags = tags
        this.deploy = deploy
        this.service = service
    }

    String getArtifactImage() {
        return artifactImage
    }

    ArrayList<String> getTags() {
        return tags
    }

    String getDeploy() {
        return deploy
    }

    String getService() {
        return service
    }

    String getInfo() {
        return new StringBuilder()
                .append("ArtifactImage: ${artifactImage}, ")
                .append("DeployTags: ${tags}, ")
                .append("DeployConfig: ${deploy}, ")
                .append("ServiceYaml: ${service}\n")
                .toString()
    }
}
