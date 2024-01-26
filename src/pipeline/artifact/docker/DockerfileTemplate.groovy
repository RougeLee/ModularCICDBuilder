package pipeline.artifact.docker

import pipeline.Pipeline
import pipeline.artifact.cd.DeploymentConfigManager
import pipeline.artifact.docker.generator.DockerfileGenerator
import pipeline.artifact.docker.generator.DockerfileVariables
import pipeline.common.util.Config

abstract class DockerfileTemplate extends Pipeline {
    protected static final String FROM_IMAGE = 'base'
    protected static final String DEST_DOCKERFILE = '/Dockerfile'
    protected final Config config
    protected final BuildWorkflowArgs buildWorkflowArgs
    protected DockerImageNameMaker dockerImageNameMaker
    protected DockerfileVariables dockerfileVariables = new DockerfileVariables()
    protected DockerfileGenerator dockerfileGenerator = new DockerfileGenerator()

    DockerfileTemplate(
            Config config,
            BuildWorkflowArgs buildWorkflowArgs
    ) {
        this.config = config
        this.buildWorkflowArgs = buildWorkflowArgs
        this.dockerImageNameMaker = config.DOCKER_IMAGE_NAME_MAKER
    }

    abstract void generateDockerfile()

    void main() {
        initTemplate()
        parseConfigSettings()
        executeGenerateDockerfile()
        writeDockerfile()
        makeImageNameWithTag()
    }

    protected void initTemplate() {
        String thisClassFullName = this.getClass().getName()
        String thisTemplateClassName = thisClassFullName
                .replaceAll(/^.*Docker\.Templates\./, '')
        EchoStep("${thisTemplateClassName} Start.")
    }

    protected void parseConfigSettings() {
        String file = buildWorkflowArgs.configFile
        if (file == null) {
            return
        }
        new DeploymentConfigManager('BuildWorkflowArgs')
                .readDeploymentConfigFile(file)
                .delegateConfigToClosure(file, dockerfileVariables)
                .verifyRequiredVariables(dockerfileVariables)
    }

    protected void executeGenerateDockerfile() {
        dockerfileGenerator
                .from(dockerfileVariables.FROM_IMAGE, FROM_IMAGE)
                .copy(DOCKERFILE_NAME, DEST_DOCKERFILE)
        generateDockerfile()
    }

    protected void writeDockerfile() {
        String dockerfile = dockerfileGenerator.generate()
        if (dockerfile == null) {
            throw new Exception('dockerfile is null.')
        }
        EchoStep("Dockerfile:\n${dockerfile}")
        writeFile file: DOCKERFILE_NAME, text: dockerfile
    }

    protected void makeImageNameWithTag() {
        new MakeImageNameWithTag(
                config,
                buildWorkflowArgs.image,
                DOCKERFILE_NAME
        ).main()
    }

    protected String getDOCKERFILE_NAME() {
        return "${buildWorkflowArgs.image}_Dockerfile"
    }

    protected Closure getDockerfileEnvVariablesClosure() {
        return { DockerfileGenerator dockerfileGenerator ->
            Map envVariables = dockerfileVariables.ENV_VARIABLES
            if (envVariables.size() == 0) {
                return
            }
            envVariables.each { String key, String value ->
                dockerfileGenerator.env(key, value)
            }
        }
    }
}
