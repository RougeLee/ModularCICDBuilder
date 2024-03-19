package pipeline.stage.flowstages

import pipeline.artifact.ansible.AnsibleConsulImageManager
import pipeline.stage.util.Stage
import pipeline.common.util.Config

@SuppressWarnings('unused')
class BuildAnsibleConsulImageStage extends Stage {
    public static String DOCKER_IMAGE = 'ansible-consul'
    public static String ANSIBLE_WORK_DIR = '/ansible'

    void main(Config config) {
        def manager = new AnsibleConsulImageManager(DOCKER_IMAGE, ANSIBLE_WORK_DIR)
        if (manager.isImageExist) {
            return
        }
        EchoStep("$DOCKER_IMAGE not found. Building Dockerfile...")
        manager.buildDockerfile()
    }
}
