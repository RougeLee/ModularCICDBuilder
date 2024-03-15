package pipeline.artifact.util

import pipeline.module.util.SetupUI
import pipeline.Pipeline
import pipeline.common.interfaces.IGetStrategyList
import pipeline.common.interfaces.ISetupUI
import pipeline.common.util.Config
import pipeline.common.util.StrategyTuple
import pipeline.module.util.Base as ModuleBase

/**
 * Use the Dynamic Class Loader to load the class.
 */
class Manager extends Pipeline {
    private static final String PackageModulePath = 'pipeline.module'
    private static final String StrategyPath = "${PackageModulePath}.strategy"
    private static final String UIPath = "${PackageModulePath}.ui"

    static void setupUI(Config config) {
        def module = config.MODULE
        def moduleArgs = config.MODULE_ARG_MAP
        String className = "${UIPath}.${module}"
        try {
            Class moduleUIClass = ModuleBase.getClassLoader().loadClass(className)
            def moduleUIObject = moduleUIClass.newInstance(config, moduleArgs) as ISetupUI
            moduleUIObject.setupUI()
            return
        } catch (Exception e) {
            EchoStep("className: ${className} is not a valid class name.\n${e}")
        }
        new SetupUI(config, moduleArgs).setupUI()
    }

    static ArrayList<StrategyTuple> getStrategyList(Config config) {
        ArrayList<StrategyTuple> strategyList = []
        if (config.MODULE == null) {
            return strategyList
        }
        stage('ModuleStrategy Init') {
            strategyList = getModuleStrategyList(config)
        }
        return strategyList
    }

    private static ArrayList<StrategyTuple> getModuleStrategyList(Config config) {
        Class moduleStrategyClass = getModuleStrategyClass(config)
        def moduleStrategyObject = moduleStrategyClass.newInstance(
                config,
                config.MODULE_ARG_MAP
        ) as IGetStrategyList
        try {
            return moduleStrategyObject.getStrategyList()
        } catch (Throwable e) {
            EchoStep("Module $config.MODULE get the StrategyList fail:\n$e")
            throw e
        }
    }

    private static Class getModuleStrategyClass(Config config) {
        String ClassName = "$StrategyPath.$config.MODULE"
        try {
            return ModuleBase.getClassLoader().loadClass(ClassName)
        } catch (Exception e) {
            EchoStep("Module class not found: $config.MODULE\n$e")
            throw e
        }
    }
}
