package tools.yamlmodel.scripts

import pipeline.common.consul.ConsulKVInfo
import tools.yamlmodel.ansible.Hosts
import tools.yamlmodel.ansible.LocalHost
import tools.yamlmodel.YamlModel

static String getInventoryLocalHostYaml() {
    def localhost = new LocalHost()
    Hosts hosts = new Hosts()
    hosts.addItem(localhost)
    def ansibleHost = new YamlModel('all')
    ansibleHost.addItem(hosts)
    def ansibleHostSB = new StringBuilder()
    ansibleHost.applyStringBuilder(ansibleHostSB)
    return ansibleHostSB.toString()
}

static String getConsulKVPlaybookYaml(
        ConsulKVInfo consulKVInfo,
        String targetTaskName
) {
    LinkedHashMap connectToConsulTask = [
            name     : 'Connect to Consul',
            consul_kv: [
                    host   : consulKVInfo.host,
                    token  : consulKVInfo.token,
                    key    : consulKVInfo.key,
                    recurse: true
            ],
            register : 'retrieved_value'
    ]
    String setFactValue = "\"{{ new_dictionary|default({}) | combine({item.Key: item.Value | from_json }) }}\""
    Map set_fact = [new_dictionary: setFactValue]
    LinkedHashMap setAsDictionaryStructureTask = [
            name    : 'Set as dictionary structure',
            set_fact: set_fact,
            loop    : "\"{{ retrieved_value.data }}\"",
            no_log  : true
    ]
    LinkedHashMap debugTask = [
            name : targetTaskName,
            debug: [msg: "\"{{ new_dictionary }}\""]
    ]
    LinkedHashMap playBook = [
            name : 'Get Consul Value',
            hosts: 'localhost',
            tasks: [connectToConsulTask, setAsDictionaryStructureTask, debugTask]
    ]
    def playbookSB = new StringBuilder()
    new YamlModel([playBook]).applyStringBuilder(playbookSB)
    return playbookSB.toString()
}
