package pipeline.artifact.ansible

import pipeline.Pipeline

class AnsibleConsulImageManager extends Pipeline {
    private String dockerImage
    private String ansibleWorkdir

    AnsibleConsulImageManager(String dockerImage, String ansibleWorkdir) {
        this.dockerImage = dockerImage
        this.ansibleWorkdir = ansibleWorkdir
    }

    void main() {
        if (ansibleConsulImageExistStatus == 0) {
            return
        }
        buildDockerfile()
    }

    private void buildDockerfile() {
        EchoStep("${dockerImage} not found. Building Dockerfile...")
        new DockerfileMaker(getAnsibleDockerfile(), dockerImage)
                .main()
                .removeDockerfile()
    }

    private GenerateAnsibleDockerfile getAnsibleDockerfile() {
        return new GenerateAnsibleDockerfile().setAnsibleWorkdir(ansibleWorkdir)
    }

    private int getAnsibleConsulImageExistStatus() {
        String result = sh(script: checkAnsibleConsulImageCommand, returnStatus: true)
        return result.toInteger()
    }

    private getCheckAnsibleConsulImageCommand() {
        return "docker image ls | grep -q ${dockerImage}"
    }
}
