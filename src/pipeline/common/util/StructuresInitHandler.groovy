package pipeline.common.util

import java.lang.reflect.Constructor

/**
 * Use the Dynamic Class Loader to load the class.
 */
class StructuresInitHandler {
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
            String className = fileName.substring(
                    0,
                    fileName.lastIndexOf('.')
            )
            String classPath = "${baseStructureClassPath}.${className}"
            Class structureClass = classLoader.loadClass(classPath)
            Constructor structureConstructor = structureClass
                    .getDeclaredConstructor(LinkedHashMap.class)
            BaseStructure structureInstance = structureConstructor
                    .newInstance(config) as BaseStructure
            structureInstance.init()
        }
    }
}
