package jenkinsscript.mock.env

@SuppressWarnings('unused')
class DefaultEnv {
    String BRANCH_NAME = 'unittest'
    String JENKINS_URL = 'http://localhost:8080'

    static def propertyMissing(String name) {
        return null
    }
}
