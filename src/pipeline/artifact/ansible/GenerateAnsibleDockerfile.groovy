package pipeline.artifact.ansible

import pipeline.artifact.docker.generator.DockerfileGenerator

@SuppressWarnings('unused')
class GenerateAnsibleDockerfile {
    private String fromImage = 'python:3.8-slim'
    private String downloadConsulUrl = 'https://releases.hashicorp.com/consul/1.9.5/'
    private String zipFileName = 'consul_1.9.5_linux_amd64.zip'
    private String ansibleWorkdir = '/ansible'
    private String defaultCommand = '[ "ansible-playbook", "--version" ]'

    GenerateAnsibleDockerfile setFromImage(String fromImage) {
        this.fromImage = fromImage
        return this
    }

    GenerateAnsibleDockerfile setDownloadConsulUrl(String downloadConsulUrl) {
        this.downloadConsulUrl = downloadConsulUrl
        return this
    }

    GenerateAnsibleDockerfile setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName
        return this
    }

    GenerateAnsibleDockerfile setAnsibleWorkdir(String ansibleWorkdir) {
        this.ansibleWorkdir = ansibleWorkdir
        return this
    }

    GenerateAnsibleDockerfile setDefaultCommand(String defaultCommand) {
        this.defaultCommand = defaultCommand
        return this
    }

    String generateContent() {
        def generator = new DockerfileGenerator()
        generator.from(fromImage)
                .run('pip install ansible python-consul')
                .run('apt-get update && apt-get install -y wget unzip')
                .run(generateConsulDownloadCommand)
                .run('ln -s /usr/local/bin/python3 /usr/bin/python3')
                .run('apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*')
                .workdir(ansibleWorkdir)
                .cmd(defaultCommand)
        return generator.generate()
    }

    private String getGenerateConsulDownloadCommand() {
        return new StringBuilder('wget ')
                .append(downloadConsulUrl)
                .append(zipFileName)
                .append(' && unzip ')
                .append(zipFileName)
                .append(' && mv consul /usr/local/bin/ && rm -f ')
                .append(zipFileName)
                .toString()
    }
}
