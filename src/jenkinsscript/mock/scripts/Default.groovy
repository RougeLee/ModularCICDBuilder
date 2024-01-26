package jenkinsscript.mock.scripts

import jenkinsscript.mock.env.DefaultEnv

class Default extends Script {
    def env = new DefaultEnv()

    @Override
    Object run() {
        return null
    }

    static def propertyMissing(String name) {
        return null
    }
}
