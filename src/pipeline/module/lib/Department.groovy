package pipeline.module.lib

class Department {
    public static final String CREDENTIAL_ID = 'CredentialID'
    public static final String PROJECT_NAME = 'ProjectName'
    public static final String INFRA_NAME = 'InfraName'
    public static final String CONSUL_KV_TOKEN = 'ConsulKVToken'
    private static final ArrayList<String> needToCopyArgNameList = [
            CREDENTIAL_ID,
            PROJECT_NAME,
            INFRA_NAME,
            CONSUL_KV_TOKEN,
    ]

    static LinkedHashMap getDepartmentArgs(LinkedHashMap moduleArgs) {
        LinkedHashMap departmentArgs = [:]
        for (String key : needToCopyArgNameList) {
            departmentArgs[key] = moduleArgs[key]
            if (departmentArgs[key] != null) {
                continue
            }
            String message = "departmentArgs: key = $key is not defined."
            throw new IllegalArgumentException(message)
        }
        return departmentArgs
    }
}
