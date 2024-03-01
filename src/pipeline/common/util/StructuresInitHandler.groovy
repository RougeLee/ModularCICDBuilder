package pipeline.common.util

import pipeline.Pipeline

import java.lang.reflect.Constructor

/**
 * Use the Dynamic Class Loader to load the class.
 */
class StructuresInitHandler extends Pipeline {
    static ArrayList structuresPackagePathStageList = [
            'pipeline',
            'common',
            'structures',
    ]

    static String getStructuresFilePath() {
        return structuresPackagePathStageList.join('/')
    }

    static String getStructuresPackagePath() {
        return structuresPackagePathStageList.join('.')
    }

    static void initAllStructures(LinkedHashMap config) {
        ClassLoader classLoader = BaseStructure.class.classLoader
        URL structuresURL = classLoader.getResource(getStructuresFilePath())
        if (structuresURL == null) {
            throw new RuntimeException("structuresURL is null")
        }
        String baseStructureClassPath = getStructuresPackagePath()
        for (File file in new File(structuresURL.toURI()).listFiles()) {
            String fileName = file.name
            String className = fileName.substring(0, fileName.lastIndexOf('.'))
            String classPath = "${baseStructureClassPath}.${className}"
            try {
                initStructure(classLoader, classPath, config)
            } catch (e) {
                EchoStep("initStructure failed. classPath: $classPath\n$e")
                throw e
            }
        }
    }

    static void initStructure(
            ClassLoader classLoader,
            String classPath,
            LinkedHashMap config
    ) {
        Class clazz = classLoader.loadClass(classPath)
        Constructor constructor = clazz.getDeclaredConstructor(LinkedHashMap.class)
        def instance = (BaseStructure) constructor.newInstance(config)
        if (instance == null) {
            throw new RuntimeException("instance is null. classPath: $classPath")
        }
        instance.initialize()
    }
}
