package pipeline.artifact.ci.build

import pipeline.artifact.ci.BuildBase
import pipeline.artifact.docker.panels.SetImageTag
import pipeline.common.util.Config
import pipeline.flow.ci.Docker

@SuppressWarnings('unused')
class docker extends BuildBase {
    docker(Config config) {
        super(config)
        ciFlow = new Docker(config)
    }

    void platformArgInit() {
        settingBuildLabel()
        settingProjectLabel()
        settingUIParamProcess()
    }

    private void settingBuildLabel() {
        config.BUILD_LABEL = 'docker_build'
    }

    private void settingProjectLabel() {
        if (config.PROJECT_CODE != null) {
            config.PROJECT_LABEL = "${config.PROJECT_CODE}_linux"
        }
    }

    private void settingUIParamProcess() {
        ArrayList<String> dockerImages = []
        config.BUILD_WORK_FLOW_ARGS_LIST.each { Map buildWorkflowArgs ->
            if (!buildWorkflowArgs.containsKey('Image')) {
                return
            }
            String image = buildWorkflowArgs.Image as String
            dockerImages << image
        }
        new SetImageTag(config, dockerImages).initUIParam()
    }
}
