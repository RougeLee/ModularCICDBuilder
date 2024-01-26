package pipeline.artifact.util

import pipeline.common.constants.BuildCauseType
import pipeline.common.ui.panels.BuildNode
import pipeline.common.util.Config
import pipeline.Pipeline

class BuildNodeScript extends Pipeline {
    private static final String buildNodeString = 'BUILD_NODE'
    private static final String jenkinsfileString = 'Jenkinsfile.BUILD_NODE'
    private Config config
    private BuildNode buildNode
    private String selectedNode

    BuildNodeScript(Config config) {
        this.config = config
        buildNode = new BuildNode(config)
    }

    void run() {
        buildNode.initUIParam()
        selectedNode = buildNode.selectedNode
        if (!setupNodeSelection) {
            config.BUILD_NODE = "${config.BUILD_LABEL}||${config.PROJECT_LABEL}"
            EchoStep("Set BUILD_LABEL to ${config.BUILD_NODE} due to not isNodeSelected.")
        }
    }

    private boolean getSetupNodeSelection() {
        if (config.BUILD_CAUSE == BuildCauseType.MANUAL && selectedNode) {
            return handleManualBuildNode()
        } else if (config.BUILD_CAUSE == BuildCauseType.TRIGGER && config.BUILD_NODE) {
            String message = new StringBuilder()
                    .append("Trigger use [${jenkinsfileString}] is set.")
                    .append("\n${buildNodeString}: ")
                    .append(config.BUILD_NODE)
                    .toString()
            EchoStep(message)
            return true
        }
        return handleUnselectedNode()
    }

    private boolean handleManualBuildNode() {
        if (selectedNode == 'Default') {
            String message = new StringBuilder()
                    .append('Manual Select ')
                    .append(buildNode.buildNodeParamName)
                    .append('= Default, Unset isNodeSelected.')
                    .toString()
            EchoStep(message)
            return false
        }
        config.BUILD_NODE = buildNode.selectedNode
        EchoStep("Manual Select ${buildNodeString}: ${config.BUILD_NODE}")
        return true
    }

    private boolean handleUnselectedNode() {
        if (config.BUILD_CAUSE == BuildCauseType.MANUAL) {
            String message = new StringBuilder()
                    .append('Manual build first time ')
                    .append("${buildNodeString} / ${jenkinsfileString} ")
                    .append('not set. Unset isNodeSelected.')
                    .toString()
            EchoStep(message)
        } else {
            String message = new StringBuilder()
                    .append('Trigger use ')
                    .append("${jenkinsfileString} ")
                    .append('not set, Unset isNodeSelected.')
                    .toString()
            EchoStep(message)
        }
        return false
    }
}
