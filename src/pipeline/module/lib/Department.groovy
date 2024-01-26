package pipeline.module.lib

class Department {
    public static final String CREDENTIAL_ID = 'CredentialID'
    public static final String PROJECT_NAME = 'ProjectName'
    public static final String INFRA_NAME = 'InfraName'
    public static final String CONSUL_KV_TOKEN = 'ConsulKVToken'
    static final ArrayList<String> needToCopyArgNameList = [
            CREDENTIAL_ID,
            CONSUL_KV_TOKEN,
            PROJECT_NAME,
            INFRA_NAME
    ]

    static LinkedHashMap getDepartmentArgs(LinkedHashMap moduleArgs) {
        LinkedHashMap departmentArgs = [:]
        needToCopyArgNameList.each { String key ->
            departmentArgs[key] = moduleArgs[key]
            assert departmentArgs[key] != null
        }
        return departmentArgs
    }
}
