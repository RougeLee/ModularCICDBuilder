package pipeline.artifact.cd

import org.yaml.snakeyaml.Yaml
import pipeline.Pipeline
import pipeline.artifact.docker.IVariable

class DeploymentConfigManager extends Pipeline {
    private static final String ENV = 'env'
    private static final String YAML = 'yaml'
    private static final String YML = 'yml'
    private static final String newLineRegex = /\r?\n/
    private String deploymentConfigPrefix
    private static final String deploymentConfigPath = 'deployment/config'
    private Map<String, DeploymentConfigFile> deploymentConfigFiles = [:]

    DeploymentConfigManager(String deploymentConfigPrefix) {
        this.deploymentConfigPrefix = deploymentConfigPrefix
    }

    static String getFilePath(String file) {
        String filePath = "$WORKSPACE/$file"
        if (fileExists(filePath)) {
            return filePath
        }
        throw new Exception("$filePath File not found.")
    }

    String getFileContent(String file, boolean customPathMode = false) {
        if (!deploymentConfigFiles.containsKey(file)) {
            readDeploymentConfigFile(file, customPathMode)
        }
        return deploymentConfigFiles[file].fileContent
    }

    DeploymentConfigManager readDeploymentConfigFile(
            String file,
            boolean customPathMode = false
    ) {
        if (deploymentConfigFiles.containsKey(file)) {
            return this
        }
        String fileExtension = getFileExtension(file)
        String filePath = getConfigPath(file, customPathMode)
        EchoStep(echoPrepareReadFile(filePath))
        String fileContent = toReadFileContent(filePath)
        EchoStep(echoFileContent(file, fileContent))
        deploymentConfigFiles[file] = new DeploymentConfigFile(
                file,
                fileExtension,
                fileContent
        )
        return this
    }

    DeploymentConfigManager delegateConfigToClosure(String file, IVariable variable) {
        if (!deploymentConfigFiles.containsKey(file)) {
            readDeploymentConfigFile(file)
        }
        DeploymentConfigFile deploymentConfigFile = deploymentConfigFiles[file]
        String fileExtension = deploymentConfigFile.fileExtension
        switch (fileExtension) {
            case ENV:
                processEnvConfigEntries(deploymentConfigFile, variable)
                break
            case YAML:
                processYamlConfigEntries(deploymentConfigFile, variable)
                break
            case YML:
                processYamlConfigEntries(deploymentConfigFile, variable)
                break
            default:
                throw UnsupportedFileExtensionException(fileExtension)
        }
        return this
    }

    DeploymentConfigManager verifyRequiredVariables(IVariable variable) {
        variable.validateRequiredVariables()
        return this
    }

    private String toReadFileContent(String filePath) {
        try {
            return readFile(filePath)
        } catch (Exception e) {
            String errorMessage = new StringBuilder()
                    .append('ErrorMessage:\n')
                    .append("${deploymentConfigPrefix}File: ")
                    .append("${filePath}\n")
                    .append(e.toString())
                    .toString()
            EchoStep(errorMessage)
            throw new Exception(errorMessage)
        }
    }

    private String echoPrepareReadFile(String filePath) {
        new StringBuilder()
                .append("${deploymentConfigPrefix}File: prepare readfile\n")
                .append(filePath)
                .toString()
    }

    private String echoFileContent(String file, String fileContent) {
        new StringBuilder()
                .append("File: ${file}\n")
                .append("${deploymentConfigPrefix}Content:\n")
                .append(fileContent)
                .toString()
    }

    private static String getConfigPath(
            String configFile,
            boolean customPathMode
    ) {
        if (customPathMode) {
            return configFile
        }

        return new StringBuilder()
                .append(WORKSPACE)
                .append('/')
                .append(deploymentConfigPath)
                .append('/')
                .append(JOB_BASE_NAME)
                .append('/')
                .append(configFile)
    }

    private static String getFileExtension(String file) {
        String fileExtension = file.substring(
                file.lastIndexOf('.') + 1
        )
        return fileExtension
    }

    private static void processEnvConfigEntries(
            DeploymentConfigFile deploymentConfigFile,
            IVariable variable
    ) {
        String[] lines = deploymentConfigFile.fileContent.split(newLineRegex)
        for (line in lines) {
            if (line.startsWith('#') || line.isEmpty()) {
                continue
            }
            int index = line.indexOf('=')
            if (index == -1) {
                continue
            }
            variable.setVariable(line[0..<index].trim(), line[index + 1..-1].trim())
        }
    }

    private static void processYamlConfigEntries(
            DeploymentConfigFile deploymentConfigFile,
            IVariable variable
    ) {
        def yaml = new Yaml()
        Map<String, Serializable> yamlMap = yaml.load(deploymentConfigFile.fileContent)
        yamlMap.each { String key, Serializable value ->
            variable.setVariable(key, value)
        }
    }

    private Exception UnsupportedFileExtensionException(String fileExtension) {
        return new Exception(new StringBuilder()
                .append('Unsupported ')
                .append(deploymentConfigPrefix)
                .append(' file extension: ')
                .append(fileExtension)
                .toString()
        )
    }
}
