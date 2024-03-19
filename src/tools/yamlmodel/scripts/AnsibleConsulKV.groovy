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

static String getSetConsulValuePlaybookYaml(
        ConsulKVInfo consulKVInfo,
        LinkedHashMap setJson
) {
    def setConsulValuePlayBook = [
            name : 'Set Consul Value',
            hosts: 'localhost',
            vars : [
                    set_json: setJson
            ],
            tasks: [
                    getSetConsulValueTask(consulKVInfo)
            ]
    ]
    return getPlaybookYaml([setConsulValuePlayBook])
}

static def getSetConsulValueTask(ConsulKVInfo consulKVInfo) {
    return [
            name     : 'Set Consul Value',
            consul_kv: [
                    host : consulKVInfo.host,
                    token: consulKVInfo.token,
                    key  : consulKVInfo.key,
                    value: "{{ set_json | to_json }}",
            ]
    ]

}

static String getGetConsulValuePlaybookYaml(
        ConsulKVInfo consulKVInfo,
        String targetTaskName
) {
    def getConsulValuePlayBook = [
            name : 'Get Consul Value',
            hosts: 'localhost',
            tasks: [
                    getConnectToConsulTask(consulKVInfo),
                    getSetAsDictionaryStructureTask(),
                    getDebugTask(targetTaskName)
            ]
    ]
    return getPlaybookYaml([getConsulValuePlayBook])
}

static String getPlaybookYaml(ArrayList<LinkedHashMap> playbooks) {
    def playbookSB = new StringBuilder()
    new YamlModel(playbooks).applyStringBuilder(playbookSB)
    return playbookSB.toString()
}

static def getConnectToConsulTask(ConsulKVInfo consulKVInfo) {
    return [
            name     : 'Connect to Consul',
            consul_kv: [
                    host   : consulKVInfo.host,
                    token  : consulKVInfo.token,
                    key    : consulKVInfo.key,
                    recurse: true
            ],
            register : 'retrieved_value'
    ]
}

static def getSetAsDictionaryStructureTask() {
    def defineDictionary = 'new_dictionary|default({})'
    def combine = 'combine({item.Key: item.Value | from_json })'
    def dictionaryValue = "\"{{ $defineDictionary | $combine }}\""
    def setFactValue = [new_dictionary: dictionaryValue]
    return [
            name    : 'Set as dictionary structure',
            set_fact: setFactValue,
            loop    : "\"{{ retrieved_value.data }}\"",
            no_log  : true
    ]
}

static def getDebugTask(String targetTaskName) {
    return [
            name : targetTaskName,
            debug: [msg: "\"{{ new_dictionary }}\""]
    ]
}
