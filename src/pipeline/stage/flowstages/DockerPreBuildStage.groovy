package pipeline.stage.flowstages

import pipeline.common.util.Config
import pipeline.stage.util.Stage
import pipeline.artifact.docker.DockerfileTemplateProvider
import pipeline.artifact.docker.DockerfileTemplate

@SuppressWarnings('unused')
class DockerPreBuildStage extends Stage {
    private static String stageName = 'Prepare Docker Build'

    void main(Config config) {
        stage(stageName) {
            def provider = new DockerfileTemplateProvider(config)
            ArrayList<DockerfileTemplate> templates = provider.getTemplates()
            templates.each { DockerfileTemplate dockerfileTemplate ->
                dockerfileTemplate.main()
            }
        }
    }
}
