package pipeline.artifact.docker.templates


import pipeline.artifact.cd.DeploymentConfigManager
import pipeline.artifact.docker.BuildWorkflowArgs
import pipeline.artifact.docker.DockerfileTemplate
import pipeline.common.util.Config

@SuppressWarnings('unused')
class CustomTemplate extends DockerfileTemplate {
    String dockerfileContent

    CustomTemplate(Config config, BuildWorkflowArgs buildWorkflowArgs) {
        super(config, buildWorkflowArgs)
    }

    void generateDockerfile() {
        String dockerfile = buildWorkflowArgs.dockerfile
        def manager = new DeploymentConfigManager(
                'CustomTemplate',
        )
        dockerfileContent = manager.getFileContent(dockerfile, true)
    }

    @Override
    protected void writeDockerfile() {
        if (dockerfileContent == null) {
            throw new Exception('dockerfile is null.')
        }
        EchoStep("Dockerfile:\n${dockerfileContent}")
        writeFile file: DOCKERFILE_NAME, text: dockerfileContent
    }
}
