package pipeline.artifact.docker.compose

//import org.yaml.snakeyaml.Yaml
import tools.yamlmodel.YamlModel

class ComposeYamlGenerator {
    final LinkedHashMap dockerComposeMap = [:]
    private static ArrayList<EDockerComposeTopLevelKey> topLevelKeys = [
            EDockerComposeTopLevelKey.networks,
            EDockerComposeTopLevelKey.volumes,
            EDockerComposeTopLevelKey.configs,
            EDockerComposeTopLevelKey.secrets
    ]
    private String composeYamlContent = null
    private String version = null
    private LinkedHashMap<String, LinkedHashMap> services = [:]
    private LinkedHashMap networks = [:]
    private LinkedHashMap volumes = [:]
    private LinkedHashMap configs = [:]
    private LinkedHashMap secrets = [:]

    ComposeYamlGenerator setVersion(String version) {
        this.version = version
        return this
    }

    ComposeYamlGenerator addTopLevelValues(String name, Map map) {
        switch (name) {
            case EDockerComposeTopLevelKey.networks.name():
                networks[name] = map
                break
            case EDockerComposeTopLevelKey.volumes.name():
                volumes[name] = map
                break
            case EDockerComposeTopLevelKey.configs.name():
                configs[name] = map
                break
            case EDockerComposeTopLevelKey.secrets.name():
                secrets[name] = map
                break
            default:
                throw new IllegalArgumentException("Unknown top level key: $name")
        }
        return this
    }

    ComposeYamlGenerator addService(String serviceName, LinkedHashMap composeServiceMap) {
        services[serviceName] = composeServiceMap
        return this
    }

    String getDockerComposeYamlContent() {
        if (composeYamlContent == null) {
            generateDockerComposeYamlContent()
        }
        return composeYamlContent
    }

    void generateDockerComposeYamlContent() {
        if (version == null || version.isEmpty()) {
            throw new IllegalArgumentException('Docker Compose version is not set.')
        }
        if (!isQuoted(version)) {
            version = "\"$version\""
        }
        if (services.isEmpty()) {
            throw new IllegalArgumentException('No service is defined.')
        }
        dockerComposeMap[EDockerComposeTopLevelKey.version.name()] = version
        dockerComposeMap[EDockerComposeTopLevelKey.services.name()] = services
        topLevelKeys.each { EDockerComposeTopLevelKey topLevelKey ->
            String key = topLevelKey.name()
            if (this."$key".isEmpty()) {
                return
            }
            dockerComposeMap[key] = this."$key"
        }
        //composeYamlContent = new Yaml().dump(dockerComposeMap)
        StringBuilder sb = new StringBuilder()
        YamlModel yamlModel = new YamlModel(dockerComposeMap)
        yamlModel.applyStringBuilder(sb)
        composeYamlContent = sb.toString()
    }

    private static boolean isQuoted(String version) {
        version = version.trim()
        return version.startsWith("\"") && version.endsWith("\"")
    }
}
