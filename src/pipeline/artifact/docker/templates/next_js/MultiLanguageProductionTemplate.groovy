package pipeline.artifact.docker.templates.next_js


import pipeline.artifact.docker.BuildWorkflowArgs
import pipeline.artifact.docker.DockerfileTemplate
import pipeline.common.util.Config

@SuppressWarnings('unused')
class MultiLanguageProductionTemplate extends DockerfileTemplate {

    MultiLanguageProductionTemplate(
            Config config,
            BuildWorkflowArgs buildWorkflowArgs
    ) {
        super(config, buildWorkflowArgs)
    }

    void generateDockerfile() {
        dockerfileGenerator.comment('MultiLanguage Production Environment for Next.js')
                .from(FROM_IMAGE, 'multiLangBuilder')
                .applyEnvs(dockerfileEnvVariablesClosure)
                .workdir('/app')
                .copy(dockerfileVariables.PROJECT_DIR, '.')
                .run('yarn install')
                .run('yarn add next-i18next')
                .run('yarn build')
                .from(FROM_IMAGE, 'multiLangRunner')
                .workdir('/app')
                .copy('--from=multiLangBuilder /app/.next', './.next')
                .copy('--from=multiLangBuilder /app/node_modules', './node_modules')
                .copy('--from=multiLangBuilder /app/public', './public')
                .copy('--from=multiLangBuilder /app/package.json', './package.json')
                .expose('3000')
                .cmd("[\"yarn\", \"start\"]")
    }
}
