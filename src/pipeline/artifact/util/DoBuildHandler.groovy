package pipeline.artifact.util

import pipeline.common.ui.panels.DoBuildPanel
import pipeline.common.util.Config
import pipeline.Pipeline

class DoBuildHandler extends Pipeline {
    private boolean doBuild
    private boolean jenkinsfileDoBuild
    private boolean openBuildUI

    DoBuildHandler(boolean jenkinsfileDoBuild, boolean openBuildUI) {
        this.jenkinsfileDoBuild = jenkinsfileDoBuild
        this.openBuildUI = openBuildUI
    }

    boolean getDoBuild() {
        return doBuild
    }

    boolean getOpenBuildUI() {
        return openBuildUI
    }

    void main(Config config) {
        def doBuildPanel = new DoBuildPanel(config, jenkinsfileDoBuild)
        doBuildPanel.initUIParam()
        if (!openBuildUI) {
            doBuild = jenkinsfileDoBuild
            EchoStep("DoBuild is Configured by Jenkinsfile set: ${doBuild}")
            return
        }
        switch (doBuildPanel.doBuild) {
            case true:
                EchoStep("DoBuild is Configured by Job: ${doBuild}")
                doBuild = true
                break
            case false:
                EchoStep("DoBuild is Configured by Job: ${doBuild}")
                doBuild = false
                break
            default:
                EchoStep("DoBuild is Configured by Jenkinsfile set: ${doBuild}")
                doBuild = jenkinsfileDoBuild
                break
        }

    }
}
