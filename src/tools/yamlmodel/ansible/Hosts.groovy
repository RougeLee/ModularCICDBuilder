package tools.yamlmodel.ansible

import tools.yamlmodel.YamlModel

@SuppressWarnings('unused')
class Hosts extends YamlModel {
    final static String hosts = 'hosts'

    Hosts() {
        super(hosts)
        mapping = new LinkedHashMap()
    }
}
