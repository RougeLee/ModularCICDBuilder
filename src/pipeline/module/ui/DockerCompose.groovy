package pipeline.module.ui

import pipeline.artifact.docker.compose.ArtifactImageManager
import pipeline.artifact.docker.compose.ComposeUnit
import pipeline.artifact.docker.compose.ServiceConfigurationSetup as Setup
import pipeline.artifact.docker.panels.SpecifyImageTag
import pipeline.common.util.Config
import pipeline.module.lib.DockerComposeLib as Lib
import pipeline.module.util.SetupUI

@SuppressWarnings('unused')
class DockerCompose extends SetupUI {

    DockerCompose(Config config, LinkedHashMap<Serializable, Serializable> moduleArgs) {
        super(config, moduleArgs)
    }

    void setupUI() {
        new SpecifyImageTag(config, artifactServices).initUIParam()
    }

    private LinkedHashSet<String> getArtifactServices() {
        LinkedHashSet<String> artifactServices = []
        composeUnits.each { ComposeUnit unit ->
            if (unit.artifactImage == ArtifactImageManager.NOT_ARTIFACT_IMAGE) {
                return
            }
            artifactServices << Setup.getServiceName(unit)
        }
        return artifactServices
    }

    private ArrayList<ComposeUnit> getComposeUnits() {
        try {
            return Lib.getComposeUnits(moduleArgs)
        } catch (Exception e) {
            stage('DockerCompose Setup UI Failed.') {
                EchoStep(e.message)
            }
            throw e
        }
    }
}
