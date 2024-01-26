package pipeline.artifact.docker.compose

import pipeline.artifact.cd.CollectDeployTagsList
import pipeline.artifact.cd.DeploymentConfigManager
import pipeline.artifact.cd.ShellScriptSplicer
import pipeline.common.ssh.EOFCommand
import pipeline.common.ssh.TransferFile
import pipeline.common.util.Config

class ComposeDeployAssembler extends ShellScriptSplicer {
    public static final String DESTINATION_DOCKER_COMPOSE_FILE = 'docker-compose.yaml'
    private static final String DOCKER_COMPOSE_CLI = 'docker-compose'
    private static final String VAR_DOCKER = '/var/docker'
    private static final String DOCKER_VOLUMES_DIR = 'volumes'
    private static final String DEPLOYMENT_LOG_DIR = 'deployment-log'
    private static final String JENKINS_JOB_TIME = 'JENKINS_JOB_TIME'
    private static final boolean enableCustomPathMode = true
    private ComposeDeploymentConfig composeDeploymentConfig
    private String composeProjectName = null
    private DeploymentConfigManager configManager
    private GenerateUnitsList delayGenerateUnitsList = null
    private Config config

    ComposeDeployAssembler(Config config, ComposeDeploymentConfig composeDeploymentConfig) {
        this.config = config
        this.composeDeploymentConfig = composeDeploymentConfig
        this.configManager = new DeploymentConfigManager(
                'DockerCompose'
        )
    }

    EOFCommand getInitVarDirBash() {
        String deployOwner = composeDeploymentConfig.deployOwner
        return generateEOFBash([
                mkdirWithOwner(VAR_DOCKER, deployOwner)
        ], true)
    }

    EOFCommand getMkdirProjectDirBash() {
        String deployOwner = composeDeploymentConfig.deployOwner
        return generateEOFBash([
                CD(VAR_DOCKER),
                mkdirWithOwner(composeRootDir, deployOwner),
                CD(composeRootDirPath),
                mkdirWithOwner(composeProjectDir, deployOwner),
                mkdirWithOwner(DOCKER_VOLUMES_DIR, deployOwner),
                mkdirWithOwner(DEPLOYMENT_LOG_DIR, deployOwner)
        ])
    }

    Map<ArrayList<String>, ArrayList<EOFCommand>> getEnvironmentSetupShells() {
        return getTagShells(composeDeploymentConfig.environmentSetupShells)
    }

    Map<ArrayList<String>, ArrayList<EOFCommand>> getPrepareStopShells() {
        return getTagShells(composeDeploymentConfig.prepareStopShells)
    }

    Map<ArrayList<String>, ArrayList<EOFCommand>> getPostDownShells() {
        return getTagShells(composeDeploymentConfig.postDownShells)
    }

    Map<ArrayList<String>, ArrayList<TransferFile>> getTransferConfigurationFiles() {
        return getTagsTransferFiles(composeDeploymentConfig.transferConfigurationFiles)
    }

    Map<ArrayList<String>, ArrayList<EOFCommand>> getPrepareServiceLaunchShells() {
        return getTagShells(composeDeploymentConfig.prepareServiceLaunchShells)
    }

    Map<ArrayList<String>, ArrayList<EOFCommand>> getServiceTestingShells() {
        return getTagShells(composeDeploymentConfig.serviceTestingShells)
    }

    EOFCommand getComposeDownBash() {
        String command = "$DOCKER_COMPOSE_CLI down"
        boolean isReturnExitStatus = false
        return getComposeCommandScript(command, isReturnExitStatus)
    }

    EOFCommand getComposeUpBash() {
        String command = "$DOCKER_COMPOSE_CLI pull && $DOCKER_COMPOSE_CLI up -d"
        return getComposeCommandScript(command)
    }

    String getDockerComposeFile() {
        return new StringBuilder()
                .append(composeRootDirPath)
                .append('/')
                .append(composeProjectDir)
                .append('/')
                .append(DESTINATION_DOCKER_COMPOSE_FILE)
                .toString()
    }

    private Map<ArrayList<String>, ArrayList<EOFCommand>> getTagShells(
            ArrayList<ComposeConfigUnit> configUnits
    ) {
        Map<ArrayList<String>, ArrayList<EOFCommand>> result = [:]
        Map<ArrayList<String>, ArrayList<ComposeConfigUnit>> unitCombinationsList
        unitCombinationsList = generateUnitsList(configUnits)
        unitCombinationsList.each {
            ArrayList<String> tags,
            ArrayList<ComposeConfigUnit> combinationUnits ->
                ArrayList<EOFCommand> eofCommands = result.getOrDefault(tags, [])
                result[tags] = eofCommands
                combinationUnits.each { ComposeConfigUnit configUnit ->
                    String shellContent = configManager.getFileContent(
                            configUnit.file,
                            enableCustomPathMode
                    )
                    eofCommands << new EOFCommand(
                            shellContent,
                            configUnit.isSudo
                    ).setWorkingDir(composeRootDirPath)
                }
        }
        return result
    }

    private Map<ArrayList<String>, ArrayList<TransferFile>> getTagsTransferFiles(
            ArrayList<ComposeConfigUnit> configUnits
    ) {
        Map<ArrayList<String>, ArrayList<TransferFile>> result = [:]
        Map<ArrayList<String>, ArrayList<ComposeConfigUnit>> unitCombinationsList
        unitCombinationsList = generateUnitsList(configUnits)
        unitCombinationsList.each {
            ArrayList<String> tags, ArrayList<ComposeConfigUnit> combinationUnits ->
                ArrayList<TransferFile> transferFiles = result.getOrDefault(tags, [])
                result[tags] = transferFiles
                combinationUnits.each { ComposeConfigUnit configUnit ->
                    String file = configUnit.file
                    String sourcePath = configManager.getFilePath(file)
                    String fileName = file.substring(
                            file.lastIndexOf('/') + 1,
                            file.length()
                    )
                    String destinationPath = "$composeRootDirPath/$fileName"
                    transferFiles << new TransferFile(sourcePath, destinationPath)
                }
        }
        return result
    }

    private Map<ArrayList<String>, ArrayList<ComposeConfigUnit>> generateUnitsList(
            ArrayList<ComposeConfigUnit> configUnits
    ) {
        return generateUnitsList.getList(configUnits)
    }

    private GenerateUnitsList getGenerateUnitsList() {
        if (delayGenerateUnitsList != null) {
            return delayGenerateUnitsList
        }
        delayGenerateUnitsList = new GenerateUnitsList(deployTagsList)
    }

    private ArrayList<ArrayList<String>> getDeployTagsList() {
        return new ArrayList<ArrayList<String>>(
                new CollectDeployTagsList(config).tagsList
        )
    }

    private EOFCommand getComposeCommandScript(
            String command,
            boolean isReturnExitStatus = true
    ) {
        return generateEOFBash(
                generateComposeCommandScript(command, isReturnExitStatus)
        )
    }

    private ArrayList<ArrayList<String>> generateComposeCommandScript(
            String command,
            boolean isReturnExitStatus
    ) {
        String dockerLog = 'DOCKER_LOG'
        String dockerStatus = 'DOCKER_STATUS'
        Date currentDate = new Date()
        String currentDayFile = currentDate.format('yyyy-MM-dd')
        String currentDateTime = currentDate.format('yyyy-MM-dd HH:mm:ss')
        String saveDeploymentLog = new StringBuilder()
                .append("echo -e \"[$currentDateTime] ")
                .append("Exit status: \$$dockerStatus, ")
                .append("Command: $command\n")
                .append("\$$dockerLog\n\" >> ")
                .append("${JENKINS_JOB_TIME}_${currentDayFile}.log")
                .toString()
        ArrayList result = [
                CD("$composeRootDirPath/$composeProjectDir"),
                [dockerLog + "=\$($command 2>&1)"],
                [dockerStatus + '=\$?'],
                CD("$composeRootDirPath/$DEPLOYMENT_LOG_DIR"),
                [saveDeploymentLog]
        ]
        if (isReturnExitStatus) {
            result.add(["exit \$$dockerStatus"])
        }
        return result
    }

    private String getComposeRootDirPath() {
        return new StringBuilder()
                .append(VAR_DOCKER)
                .append('/')
                .append(composeRootDir)
                .toString()
    }

    private String getComposeRootDir() {
        return new StringBuilder()
                .append(composeDeploymentConfig.projectCode)
                .append('/')
                .append(composeDeploymentConfig.branchName)
                .toString()
    }

    private String getComposeProjectDir() {
        if (composeProjectName == null) {
            composeProjectName = DockerComposeProjectNameMaker
                    .generateProjectName(
                            composeDeploymentConfig.projectCode,
                            composeDeploymentConfig.branchName
                    )
        }
        return composeProjectName
    }
}
