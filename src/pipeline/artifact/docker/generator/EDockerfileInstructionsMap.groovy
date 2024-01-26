package pipeline.artifact.docker.generator

import com.cloudbees.groovy.cps.NonCPS

/**
 * Enum representing the various Dockerfile instructions.
 * This enum is used in conjunction with DockerfileGenerator to facilitate the generation of Dockerfile instructions.
 */
enum EDockerfileInstructionsMap {
    FROM('FROM'),
    WORKDIR('WORKDIR'),
    RUN('RUN'),
    CMD('CMD'),
    LABEL('LABEL'),
    MAINTAINER('MAINTAINER'),
    EXPOSE('EXPOSE'),
    ARG('ARG'),
    ENV('ENV'),
    ADD('ADD'),
    COPY('COPY'),
    ENTRYPOINT('ENTRYPOINT'),
    VOLUME('VOLUME'),
    USER('USER'),
    HEALTHCHECK('HEALTHCHECK'),
    ONBUILD('ONBUILD'),
    STOPSIGNAL('STOPSIGNAL'),
    SHELL('SHELL')

    private String instruction

    EDockerfileInstructionsMap(String instruction) {
        this.instruction = instruction
    }

    @NonCPS
    String getInstruction() {
        return instruction
    }
}
