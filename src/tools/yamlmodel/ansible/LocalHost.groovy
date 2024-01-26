package tools.yamlmodel.ansible

import com.cloudbees.groovy.cps.NonCPS
import tools.yamlmodel.YamlModel

@SuppressWarnings('unused')
class LocalHost extends YamlModel {
    String modelName = 'localhost'
    String ansible_connection = 'local'

    LocalHost() {
        super([:])

        initMapping()
    }

    LocalHost(LinkedHashMap params) {
        super([:])
        initMapping()
        mapping << params
    }

    @NonCPS
    void initMapping() {
        mapping.put('ansible_connection', ansible_connection)
    }

    LinkedHashMap getYamlModel() {
        return mapping
    }
}
