package pipeline.artifact.docker

import com.cloudbees.groovy.cps.NonCPS

class BuildWorkflowArgs {
    private static final String IMAGE = EBuildWorkflowArgKey.Image.name()
    private static final String TEMPLATE = EBuildWorkflowArgKey.DockerfileTemplate.name()
    private static final String CONFIG_FILE = EBuildWorkflowArgKey.ConfigFile.name()
    private static final String DOCKERFILE = EBuildWorkflowArgKey.Dockerfile.name()
    private static final String CUSTOM_DOCKERFILE = 'CustomTemplate'
    private String image
    private String dockerfileTemplate
    private String configFile
    private String dockerfile

    BuildWorkflowArgs(LinkedHashMap buildWorkflowArgs) {
        initImage(buildWorkflowArgs)
        initConfigFile(buildWorkflowArgs)
        initTemplateInfo(buildWorkflowArgs)
    }

    String getImage() {
        return image
    }

    String getDockerfileTemplate() {
        return dockerfileTemplate
    }

    String getConfigFile() {
        return configFile
    }

    String getDockerfile() {
        return dockerfile
    }

    @NonCPS
    private void initImage(LinkedHashMap buildWorkflowArgs) {
        String image = buildWorkflowArgs[IMAGE]
        if (image == null || image.isEmpty()) {
            throw new Exception('Image is null or empty.')
        }
        this.image = image
    }

    @NonCPS
    private void initConfigFile(LinkedHashMap buildWorkflowArgs) {
        String configFile = buildWorkflowArgs[CONFIG_FILE]
        if (configFile != null && configFile.split(/\./).size() > 2) {
            throw new Exception('ConfigFile is null or invalid.')
        }
        this.configFile = configFile
    }

    @NonCPS
    private void initTemplateInfo(LinkedHashMap buildWorkflowArgs) {
        String dockerfileTemplate = buildWorkflowArgs[TEMPLATE]
        //noinspection GroovyFallthrough
        switch (dockerfileTemplate) {
            case null:
            case CUSTOM_DOCKERFILE:
                String dockerfile = buildWorkflowArgs[DOCKERFILE]
                if (dockerfile == null || dockerfile.isEmpty()) {
                    throw new Exception('Dockerfile is null or empty.')
                }
                this.dockerfileTemplate = CUSTOM_DOCKERFILE
                this.dockerfile = dockerfile
                break
            default:
                if (dockerfileTemplate.split(/\./).size() > 2) {
                    throw new Exception('DockerfileTemplate is null or invalid.')
                }
                this.dockerfileTemplate = dockerfileTemplate
        }
    }
}
