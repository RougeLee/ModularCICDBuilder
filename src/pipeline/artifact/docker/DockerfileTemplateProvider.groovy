package pipeline.artifact.docker

import pipeline.Pipeline
import pipeline.common.util.Config

/**
 * Use the Dynamic Class Loader to load the class.
 */
class DockerfileTemplateProvider extends Pipeline {
    private Config config
    private String templatesPath
    private ArrayList<BuildWorkflowArgs> buildWorkflowArgsList

    DockerfileTemplateProvider(Config config) {
        this.config = config
    }

    ArrayList<DockerfileTemplate> getTemplates() {
        checkProjectCode()
        getBuildWorkflowArgsList()
        initTemplatesPath()
        return generateDockerfileTemplates()
    }

    private ArrayList<DockerfileTemplate> generateDockerfileTemplates() {
        ArrayList<DockerfileTemplate> templates = []
        buildWorkflowArgsList.each { BuildWorkflowArgs buildWorkflowArgs ->
            try {
                templates << generateDockerfileTemplate(buildWorkflowArgs)
            } catch (Exception e) {
                throw generateDockerfileTemplateException(buildWorkflowArgs, e)
            }
        }
        return templates
    }

    private DockerfileTemplate generateDockerfileTemplate(
            BuildWorkflowArgs buildWorkflowArgs
    ) {
        Class templateClass = this.class.classLoader.loadClass(
                getTemplateClassName(buildWorkflowArgs)
        )
        return templateClass.newInstance(
                config,
                buildWorkflowArgs
        ) as DockerfileTemplate
    }

    private String getTemplateClassName(
            BuildWorkflowArgs buildWorkflowArgs
    ) {
        return new StringBuilder(templatesPath)
                .append('.')
                .append(buildWorkflowArgs.dockerfileTemplate)
                .toString()
    }

    private void initTemplatesPath() {
        String thisClassName = this.class.name
        String dockerPackagePath = thisClassName.substring(
                0,
                thisClassName.lastIndexOf('.')
        )
        templatesPath = "${dockerPackagePath}.templates"
    }

    private ArrayList<BuildWorkflowArgs> getBuildWorkflowArgsList() {
        ArrayList<LinkedHashMap> maps = config.BUILD_WORK_FLOW_ARGS_LIST
        if (maps == null) {
            throw new Exception('BuildWorkflowArgsList is null.')
        }
        ArrayList<BuildWorkflowArgs> buildWorkflowArgsList = []
        maps.each { LinkedHashMap buildWorkflowArgs ->
            EchoStep("BuildWorkflowArgs: ${buildWorkflowArgs}")
            buildWorkflowArgsList << new BuildWorkflowArgs(buildWorkflowArgs)
        }
        this.buildWorkflowArgsList = buildWorkflowArgsList
    }

    private void checkProjectCode() {
        String projectCode = config.PROJECT_CODE
        if (projectCode == null) {
            throw new Exception('PROJECT_CODE is null.')
        }
    }

    private Exception generateDockerfileTemplateException(
            BuildWorkflowArgs buildWorkflowArgs,
            Exception e
    ) {
        String templateClassName = getTemplateClassName(buildWorkflowArgs)
        String errorMessage = new StringBuilder("ErrorMessage:\n")
                .append("templateClassName: ${templateClassName}\n")
                .append("buildWorkflowArgs: ${buildWorkflowArgs}\n")
                .append(e.toString())
                .toString()
        EchoStep(errorMessage)
        return e
    }
}
