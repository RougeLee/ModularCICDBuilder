package pipeline.artifact.docker.templates.next_js


import pipeline.artifact.docker.BuildWorkflowArgs
import pipeline.artifact.docker.DockerfileTemplate
import pipeline.common.util.Config

@SuppressWarnings('unused')
class DevelopmentTemplate extends DockerfileTemplate {

    DevelopmentTemplate(
            Config config,
            BuildWorkflowArgs buildWorkflowArgs
    ) {
        super(config, buildWorkflowArgs)
    }

    void generateDockerfile() {
        dockerfileGenerator.comment('Development Environment for Next.js')
                .from(FROM_IMAGE, 'development')
                .applyEnvs(dockerfileEnvVariablesClosure)
                .workdir('/app')
                .copy(dockerfileVariables.PROJECT_DIR, '.')
                .run('yarn install')
                .expose('3000')
                .cmd("[\"yarn\", \"dev\"]")
    }
}
