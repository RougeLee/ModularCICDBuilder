package pipeline.artifact.docker

import pipeline.Pipeline
import com.cloudbees.groovy.cps.NonCPS

class DockerImageNameMaker extends Pipeline {
    protected String dockerRegistryURL
    protected String dockerRouter
    protected String projectCode

    DockerImageNameMaker(
            String projectCode,
            String dockerRegistryURL,
            String dockerRouter
    ) {
        this.projectCode = projectCode
        this.dockerRegistryURL = dockerRegistryURL
        this.dockerRouter = dockerRouter
        validate()
    }

    @NonCPS
    void validate() {
        assert dockerRegistryURL != null
        assert dockerRouter != null
        assert projectCode != null
    }

    String getDockerImage(String image) {
        return new StringBuilder()
                .append(GCPArtifactRegistryDockerURL)
                .append(getImageBaseName(image))
                .toString()
    }

    protected String getGCPArtifactRegistryDockerURL() {
        return new StringBuilder()
                .append(dockerRegistryURL)
                .append('/')
                .append(dockerRouter)
                .append('/')
                .toString()
    }

    protected String getImageBaseName(String image) {
        return new StringBuilder()
                .append(projectCode.toLowerCase())
                .append('/')
                .append(branchName.toLowerCase())
                .append('/')
                .append(image.toLowerCase())
                .toString()
    }

    protected static String getBranchName() {
        return JOB_BASE_NAME
    }
}
