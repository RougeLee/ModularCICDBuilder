package pipeline.artifact.ci

import pipeline.artifact.ci.build.DefaultBuildPlatform
import pipeline.Pipeline

/**
 * Use the Dynamic Class Loader to load the class.
 */
class BuildPlatform extends Pipeline {

    static Class<BuildBase> getBuildPlatformClass(String buildPlatform) {
        ClassLoader buildPlatformClassLoader = BuildBase.class.getClassLoader()
        try {
            return buildPlatformClassLoader.loadClass('pipeline.artifact.ci.build.' + buildPlatform) as Class<BuildBase>
        } catch (ClassNotFoundException ignored) {
            return DefaultBuildPlatform
        }
    }
}
