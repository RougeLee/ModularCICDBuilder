package pipeline.module.util

import pipeline.common.interfaces.ISetupUI
import pipeline.common.ui.panels.DefaultPanel
import pipeline.common.util.Config

class SetupUI extends Base implements ISetupUI {
    protected Config config
    protected LinkedHashMap<Serializable, Serializable> moduleArgs

    SetupUI(Config config, LinkedHashMap<Serializable, Serializable> moduleArgs) {
        this.config = config
        this.moduleArgs = moduleArgs
    }

    void setupUI() {
        EchoStep("${this.class.simpleName} use default setupUI")
        DefaultPanel defaultPanel = new DefaultPanel(config)
        defaultPanel.initUIParam()
    }
}
