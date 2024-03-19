package pipeline.artifact.ansible

import pipeline.Pipeline

class AnsibleConsulImageManager extends Pipeline {
    private String dockerImage
    private String ansibleWorkdir

    AnsibleConsulImageManager(String dockerImage, String ansibleWorkdir) {
        this.dockerImage = dockerImage
        this.ansibleWorkdir = ansibleWorkdir
    }

    boolean getIsImageExist() {
        String cmd = "docker image ls | grep -q $dockerImage"
        String resultStatus = sh(script: cmd, returnStatus: true)
        return resultStatus.toInteger() == 0
    }

    void buildDockerfile() {
        EchoStep("$dockerImage not found. Building Dockerfile...")
        new DockerfileMaker(getAnsibleDockerfile(), dockerImage)
                .main()
                .removeDockerfile()
    }

    private GenerateAnsibleDockerfile getAnsibleDockerfile() {
        return new GenerateAnsibleDockerfile().setAnsibleWorkdir(ansibleWorkdir)
    }
}
