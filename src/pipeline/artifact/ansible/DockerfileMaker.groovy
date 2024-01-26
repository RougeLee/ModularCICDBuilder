package pipeline.artifact.ansible

import pipeline.artifact.docker.cli.CommandActuator
import pipeline.artifact.docker.cli.build.BuildConfig
import pipeline.Pipeline

class DockerfileMaker extends Pipeline {
    private GenerateAnsibleDockerfile generateAnsibleDockerfile
    private String dockerfileName = 'Ansible_Consul_Dockerfile'
    private String dockerImageName

    DockerfileMaker(
            GenerateAnsibleDockerfile generateAnsibleDockerfile,
            String dockerImageName = 'ansible-consul'
    ) {
        this.generateAnsibleDockerfile = generateAnsibleDockerfile
        this.dockerImageName = dockerImageName
    }

    DockerfileMaker main() {
        writeDockerfile()
        buildImage()
        return this
    }

    void removeDockerfile() {
        sh "rm -f ${dockerfileName}"
    }

    private void writeDockerfile() {
        String dockerfileContent = generateAnsibleDockerfile.generateContent()
        EchoStep("Dockerfile content:\n${dockerfileContent}")
        writeFile file: dockerfileName, text: dockerfileContent
    }

    private void buildImage() {
        BuildConfig buildConfig = new BuildConfig(dockerImageName)
                .setDockerfilePath(dockerfileName)
        new CommandActuator(buildConfig).run()
    }
}
