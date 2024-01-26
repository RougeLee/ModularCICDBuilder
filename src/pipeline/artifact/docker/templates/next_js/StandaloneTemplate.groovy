package pipeline.artifact.docker.templates.next_js


import pipeline.artifact.docker.BuildWorkflowArgs
import pipeline.artifact.docker.DockerfileTemplate
import pipeline.common.util.Config

@SuppressWarnings('unused')
class StandaloneTemplate extends DockerfileTemplate {

    StandaloneTemplate(
            Config config,
            BuildWorkflowArgs buildWorkflowArgs
    ) {
        super(config, buildWorkflowArgs)
    }

    void generateDockerfile() {
        dockerfileGenerator.comment('Step 1. Rebuild the source code only when needed')
        dockerfileGenerator.from(FROM_IMAGE, 'builder')
                .applyEnvs(dockerfileEnvVariablesClosure)
                .workdir('/app')
                .copy(dockerfileVariables.PROJECT_DIR, '.')
                .run(new StringBuilder()
                        .append("if [ -f yarn.lock ]; then yarn --frozen-lockfile; ")
                        .append("elif [ -f package-lock.json ]; then npm ci; ")
                        .append("elif [ -f pnpm-lock.yaml ]; then yarn global add pnpm && pnpm i; ")
                        .append("else echo ")
                        .append("\"Warning: Lockfile not found. It is recommended to commit lockfiles to version control.\"")
                        .append(" && yarn install; ")
                        .append("fi")
                        .toString())
                .run(new StringBuilder()
                        .append("if [ -f yarn.lock ]; then yarn build; ")
                        .append("elif [ -f package-lock.json ]; then npm run build; ")
                        .append("elif [ -f pnpm-lock.yaml ]; then pnpm build; ")
                        .append("else yarn build; fi")
                        .toString())
        dockerfileGenerator.comment('Step 2. Production image, copy all the files and run next')
                .from(FROM_IMAGE, 'runner')
                .applyEnvs(dockerfileEnvVariablesClosure)
                .workdir('/app')
                .run('addgroup --system --gid 1001 nodejs')
                .run('adduser --system --uid 1001 nextjs')
                .user('nextjs')
                .copy('--from=builder /app/public', './public')
                .copy('--from=builder --chown=nextjs:nodejs /app/.next/standalone', '.')
                .copy('--from=builder --chown=nextjs:nodejs /app/.next/static', './.next/static')
                .cmd("[\"node\", \"server.js\"]")
    }
}
