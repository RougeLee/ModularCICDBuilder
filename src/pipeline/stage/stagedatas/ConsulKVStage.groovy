package pipeline.stage.stagedatas

import pipeline.common.consul.ConsulKVBaseInfo
import pipeline.common.consul.ConsulKVInfo
import pipeline.module.lib.Department
import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class ConsulKVStage extends StageData {
    public String Desc = 'Get ConsulKV'
    public static final String CREDENTIAL_ID = Department.CREDENTIAL_ID
    public static final String CONSUL_KV_INFO = 'ConsulKVInfo'
    private ConsulKVBaseInfo consulKVBaseInfo
    private static final String PROJECT_NAME = Department.PROJECT_NAME
    private static final String INFRA_NAME = Department.INFRA_NAME
    private static final String CONSUL_KV_TOKEN = Department.CONSUL_KV_TOKEN

    ConsulKVStage(LinkedHashMap moduleArgs, ConsulKVBaseInfo consulKVBaseInfo) {
        super(moduleArgs)
        this.consulKVBaseInfo = consulKVBaseInfo
        assertKeys << CREDENTIAL_ID
        assertKeys << PROJECT_NAME
        assertKeys << INFRA_NAME
        assertKeys << CONSUL_KV_TOKEN
    }

    void init() {
        StageArgs[CREDENTIAL_ID] = moduleArgs[CREDENTIAL_ID]
        StageArgs[CONSUL_KV_INFO] = consulKVInfo
    }

    private ConsulKVInfo getConsulKVInfo() {
        return new ConsulKVInfo(consulKVBaseInfo, projectName, infraName, consulKVToken)
    }

    private String getProjectName() {
        return moduleArgs[PROJECT_NAME] as String
    }

    private String getInfraName() {
        return moduleArgs[INFRA_NAME] as String
    }

    private String getConsulKVToken() {
        return moduleArgs[CONSUL_KV_TOKEN] as String
    }
}
