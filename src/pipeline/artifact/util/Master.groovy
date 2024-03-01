package pipeline.artifact.util

import pipeline.artifact.ci.BuildBase
import pipeline.artifact.ci.Util
import pipeline.artifact.ci.BuildPlatform
import pipeline.common.constants.FlowType
import pipeline.flow.cd.CustomActionFlow
import pipeline.common.util.MasterStage
import pipeline.common.util.Config
import pipeline.common.util.StrategyTuple
import pipeline.flow.util.StageFlow

class Master extends MasterStage {
    private Closure ciFlowClosure = null

    Master(Config config) {
        super(config)
    }

    void runCIFlow() {
        if (config.SKIP_ALL_FLOW) {
            return
        }
        if (!config.DO_BUILD_HANDLER.doBuild) {
            return
        }
        if (ciFlowClosure == null) {
            String errorMessage = "No BUILD_PLATFORM: ${config.BUILD_PLATFORM}"
            throw new Exception(getStepStage(errorMessage))
        }
        node(config.BUILD_NODE, ciFlowClosure)
    }

    void runCDFlow() {
        if (config.SKIP_ALL_FLOW) {
            return
        }
        ArrayList<StrategyTuple> strategyList = Manager.getStrategyList(config)
        if (strategyList.size() == 0) {
            return
        }
        strategyList.each { StrategyTuple strategy ->
            new CustomActionFlow(config, strategy.stageDataList).main()
        }
    }

    void catchException(Throwable e) {
        echoException(e)
        currentBuild.result = 'FAILURE'
    }

    void customInit() {
        optionalArgInit()
        platformLibInit()
        setupJobUI()
    }

    private void optionalArgInit() {
        new OptionalArgInitializer(config).initialize()
    }

    private void setupJobUI() {
        unityBasicUI()
        setupCIUI()
        setupCDUI()
        settingProperties()
    }

    private void setupCIUI() {
        if (config.FLOW != FlowType.CI && !config.DO_BUILD_HANDLER.openBuildUI) {
            return
        }
        buildPlatformInit()
        buildNodeListInit()
        buildTypeInit()
    }

    /**
     * Build Platform初始化
     */
    private void buildPlatformInit() {
        Class buildPlatformClass = BuildPlatform.getBuildPlatformClass(
                config.BUILD_PLATFORM
        )
        BuildBase buildPlatform = buildPlatformClass.newInstance(config)
        buildPlatform.main()
        StageFlow stageMain = buildPlatform.getCIFlow()
        ciFlowClosure = stageMain ? { stageMain.main() } : null
    }

    /**
     * Build Node List初始化
     */
    private void buildNodeListInit() {
        if (config.BUILD_LABEL == null && config.PROJECT_LABEL == null) {
            config.NODE_LIST = []
            EchoStep('No BUILD_LABEL and PROJECT_LABEL')
            return
        }
        StringBuilder regexBuilder = new StringBuilder()
        regexBuilder.append(".*(")
        if (config.BUILD_LABEL != null) {
            regexBuilder.append(config.BUILD_LABEL)
        }
        if (config.PROJECT_LABEL != null) {
            if (config.BUILD_LABEL != null) {
                regexBuilder.append("|")
            }
            regexBuilder.append(config.PROJECT_LABEL)
        }
        regexBuilder.append(").*")
        String nodeLabelRegex = regexBuilder.toString()
        EchoStep("Node Label Regex: ${nodeLabelRegex}")
        List<String> BuildNodes = Util.searchNodeByLabel(nodeLabelRegex)
        BuildNodes.add(0, 'Default')
        config.NODE_LIST = BuildNodes
        EchoStep("Available Build Nodes: ${config.NODE_LIST}")
    }

    private buildTypeInit() {
        new BuildTypeInitializer(config).initialize()
    }

    private void setupCDUI() {
        Manager.setupUI(config)
    }

    private void unityBasicUI() {
        config.CUSTOM_PROPERTIES << disableConcurrentBuilds()
        config.CUSTOM_PROPERTIES << buildDiscarder(logRotator(logRotatorArgMap))
    }

    private static LinkedHashMap getLogRotatorArgMap() {
        LinkedHashMap logRotatorArg = [:]
        logRotatorArg['artifactDaysToKeepStr'] = ''
        logRotatorArg['artifactNumToKeepStr'] = '11'
        logRotatorArg['daysToKeepStr'] = ''
        logRotatorArg['numToKeepStr'] = '11'
        return logRotatorArg
    }
}
