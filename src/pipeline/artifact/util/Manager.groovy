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
        if (config.MODULE == null) {
            return []
        }
        return getModuleStrategyList(config)
    }

    private static ArrayList<StrategyTuple> getModuleStrategyList(Config config) {
        Class moduleStrategyClass = getModuleStrategyClass(config)
        def moduleStrategyObject = moduleStrategyClass.newInstance(
                config,
                config.MODULE_ARG_MAP
        ) as IGetStrategyList
        return moduleStrategyObject.getStrategyList()
    }

    private static Class getModuleStrategyClass(Config config) {
        def module = config.MODULE
        String ClassName = "${StrategyPath}.${module}"
        try {
            return ModuleBase.getClassLoader().loadClass(ClassName)
        } catch (Exception e) {
            EchoStep("Module class not found: ${module}\n${e}")
            throw e
        }
    }
}
