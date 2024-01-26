package pipeline.artifact.docker.templates.next_js


import pipeline.artifact.docker.BuildWorkflowArgs
import pipeline.artifact.docker.DockerfileTemplate
import pipeline.common.util.Config

@SuppressWarnings('unused')
class StaticSiteProductionTemplate extends DockerfileTemplate {

    StaticSiteProductionTemplate(
            Config config,
            BuildWorkflowArgs buildWorkflowArgs
    ) {
        super(config, buildWorkflowArgs)
    }

    void generateDockerfile() {
        dockerfileGenerator.comment('Static Site Production Environment for Next.js')
                .from(FROM_IMAGE, 'builder')
                .applyEnvs(dockerfileEnvVariablesClosure)
                .workdir('/app')
                .copy(dockerfileVariables.PROJECT_DIR, '.')
                .run('yarn install')
                .run('yarn build')
                .run('yarn next export')
                .from('nginx:alpine', 'production')
                .copy('--from=builder /app/out', '/usr/share/nginx/html')
                .expose('80')
                .cmd("[\"nginx\", \"-g\", \"daemon off;\"]")
    }
}
