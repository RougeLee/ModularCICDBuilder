package pipeline.artifact.docker.cli.run

import pipeline.artifact.docker.cli.AbstractDockerConfig

/**
 * Class for configuring Docker run command.
 */
@SuppressWarnings('unused')
class RunConfig extends AbstractDockerConfig {
    private static final String DOCKER_RUN = 'docker run '
    private static final String BACKGROUND = '-d '
    private static final String PRIVILEGED = '--privileged=true '
    private static final String RM = '--rm '
    private String dockerImage
    private String dockerRunCMD

    /**
     * Run container in the background and print container ID.
     */
    private boolean isBackground = false
    /**
     * Give extended privileges to this container.
     */
    private boolean isPrivileged = false
    /**
     * Automatically remove the container when it exits.
     */
    private boolean isRM = false
    /**
     * Working directory inside the container.
     */
    private String workDir
    /**
     * Network mode for the container.
     */
    private String networkMode
    /**
     * Limit the CPU usage of the container, e.g., '0.5' uses half CPU core.
     */
    private String cpuLimit
    /**
     * Memory limit for the container, e.g., '100m' for 100 megabytes.
     */
    private String memoryLimit
    /**
     * List of environment variables for the container, format: 'KEY=VALUE'.
     */
    private ArrayList<String> envStringList = []
    /**
     * List of volume mounts for the container, format: 'host_path:container_path'.
     */
    private ArrayList<String> volumeStringList = []
    /**
     * List of port mappings for the container, format: 'host_port:container_port'.
     */
    private ArrayList<String> portMappingList = []

    RunConfig(String dockerImage) {
        this.dockerImage = dockerImage
    }

    RunConfig(String dockerImage, String dockerRunCommand) {
        this.dockerImage = dockerImage
        this.dockerRunCMD = dockerRunCommand
    }

    /**
     * Set the container to run in the background.
     *
     * @param isBackground If true, the container runs in the background.
     * @return The DockerRunConfig instance for chaining.
     */
    RunConfig setBackground(boolean isBackground) {
        this.isBackground = isBackground
        return this
    }

    /**
     * Give extended privileges to the container.
     *
     * @param isPrivileged If true, the container is given extended privileges.
     * @return The DockerRunConfig instance for chaining.
     */
    RunConfig setPrivileged(boolean isPrivileged) {
        this.isPrivileged = isPrivileged
        return this
    }

    /**
     * Automatically remove the container when it exits.
     *
     * @param isRM If true, the container is removed automatically when it exits.
     * @return The DockerRunConfig instance for chaining.
     */
    RunConfig setRM(boolean isRM) {
        this.isRM = isRM
        return this
    }

    /**
     * Set the working directory inside the container.
     *
     * @param workDir The working directory path inside the container.
     * @return The DockerRunConfig instance for chaining.
     */
    RunConfig setWorkDir(String workDir) {
        validateParameters('workDir', workDir)
        this.workDir = workDir
        return this
    }

    /**
     * Set the network mode for the container.
     *
     * @param networkMode The network mode to set for the container.
     * @return The DockerRunConfig instance for chaining.
     */
    RunConfig setNetworkMode(String networkMode) {
        validateParameters('networkMode', networkMode)
        this.networkMode = networkMode
        return this
    }

    /**
     * Set the CPU usage limit for the container.
     *
     * @param cpuLimit The CPU limit, e.g., '0.5' uses half CPU core.
     * @return The DockerRunConfig instance for chaining.
     */
    RunConfig setCpuLimit(String cpuLimit) {
        validateParameters('cpuLimit', cpuLimit)
        this.cpuLimit = cpuLimit
        return this
    }

    /**
     * Set the memory limit for the container.
     *
     * @param memoryLimit The memory limit, e.g., '100m' for 100 megabytes.
     * @return The DockerRunConfig instance for chaining.
     */
    RunConfig setMemoryLimit(String memoryLimit) {
        validateParameters('memoryLimit', memoryLimit)
        this.memoryLimit = memoryLimit
        return this
    }

    /**
     * Add an environment variable to the container.
     *
     * @param envString The environment variable to add, in the format 'KEY=VALUE'.
     * @return The DockerRunConfig instance for chaining.
     */
    RunConfig addEnvString(String envString) {
        validateParameters('envString', envString)
        envStringList.add(envString)
        return this
    }

    /**
     * Add a volume mount to the container.
     *
     * @param volumeString The volume mount to add, in the format 'host_path:container_path'.
     * @return The DockerRunConfig instance for chaining.
     */
    RunConfig addVolumeString(String volumeString) {
        validateParameters('volumeString', volumeString)
        volumeStringList.add(volumeString)
        return this
    }

    /**
     * Add a port mapping to the container.
     *
     * @param portMapping The port mapping to add, in the format 'host_port:container_port'.
     * @return The DockerRunConfig instance for chaining.
     */
    RunConfig addPortMapping(String portMapping) {
        validateParameters('addPortMapping', portMapping)
        portMappingList.add(portMapping)
        return this
    }

    String generateConfig() {
        validateParameters('dockerImage', dockerImage)
        def dockerRunSB = new StringBuffer()
        dockerRunSB.append(DOCKER_RUN)
        if (isBackground) {
            dockerRunSB.append(BACKGROUND)
        }
        if (isPrivileged) {
            dockerRunSB.append(PRIVILEGED)
        }
        if (isRM) {
            dockerRunSB.append(RM)
        }
        if (workDir) {
            dockerRunSB.append("-w ${workDir} ")
        }
        if (networkMode) {
            dockerRunSB.append("--network=${networkMode} ")
        }
        if (cpuLimit) {
            dockerRunSB.append("--cpus=${cpuLimit} ")
        }
        if (memoryLimit) {
            dockerRunSB.append("--memory=${memoryLimit} ")
        }
        if (envStringList.size() > 0) {
            for (String envString : envStringList) {
                dockerRunSB.append("-e ${envString} ")
            }
        }
        if (volumeStringList.size() > 0) {
            for (String volumeString : volumeStringList) {
                dockerRunSB.append("-v ${volumeString} ")
            }
        }
        if (portMappingList.size() > 0) {
            portMappingList.each { portMapping ->
                dockerRunSB.append("-p ${portMapping} ")
            }
        }
        dockerRunSB.append("${dockerImage} ")
        if (checkParameter(dockerRunCMD)) {
            dockerRunSB.append(dockerRunCMD)
        }
        return dockerRunSB.toString()
    }
}
