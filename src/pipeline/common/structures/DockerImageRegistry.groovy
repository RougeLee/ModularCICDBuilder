package pipeline.common.structures

import pipeline.artifact.docker.DockerImageNameMaker
import pipeline.common.util.BaseStructure

@SuppressWarnings('unused')
class DockerImageRegistry extends BaseStructure {
    private static final String GCP_REGISTRY_URL = 'GCP_REGISTRY_URL'
    private static final String DOCKER_ROUTER = 'DOCKER_ROUTER'

    DockerImageRegistry(LinkedHashMap config) {
        super(config)
    }

    protected void structureInitProcess() {
        setConfigProperty('DOCKER_IMAGE_NAME_MAKER', nameMaker)
    }

    private DockerImageNameMaker getNameMaker() {
        String projectCode = config.PROJECT_CODE as String
        if (projectCode == null) {
            throw new Exception('PROJECT_CODE is null.')
        }
        DockerImageNameMaker nameMaker
        try {
            nameMaker = new DockerImageNameMaker(
                    projectCode,
                    dockerRegistryURL,
                    dockerRouter
            )
        } catch (Throwable e) {
            EchoStep("DockerImageNameMaker initialize failed.\n$e")
            nameMaker = null
        }
        return nameMaker
    }

    private static String getDockerRouter() {
        return getEnvProperty(DOCKER_ROUTER) as String
    }

    private static String getDockerRegistryURL() {
        return getEnvProperty(GCP_REGISTRY_URL) as String
    }
}
