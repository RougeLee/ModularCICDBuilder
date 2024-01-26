package pipeline.common.util

import pipeline.artifact.docker.DockerImageNameMaker
import pipeline.artifact.util.DoBuildHandler
import pipeline.common.consul.ConsulKVBaseInfo

class Config extends ConfigMethods {
    // Parameters passed in from outside.
    public static String SCM
    public static String PROJECT_CODE
    public static String GIT_URL
    public static String GIT_CREDENTIALS_ID
    public static int PIPELINE_TIMEOUT = 0
    public static ArrayList<LinkedHashMap> BUILD_WORK_FLOW_ARGS_LIST
    public static String MODULE
    public static LinkedHashMap<Serializable, Serializable> MODULE_ARG_MAP

    // Form the DevOps Artifact Project.
    public static String BUILD_TYPE
    public static String BUILD_PLATFORM
    public static String CODE_TYPE
    public static String ARTIFACT_TYPE

    // Program control flow using parameters.
    public static String JOB_STATUS
    public static String FLOW
    public static boolean SKIP_ALL_FLOW
    public static String BUILD_LABEL
    public static String PROJECT_LABEL
    public static String BUILD_NODE
    public static String DEPLOY_NODE
    public static ArrayList NODE_LIST
    public static String APP_MODE
    public static String BUILD_CAUSE
    public static ProjectInfraState PROJECT_INFRA_STATE
    public static LinkedHashSet<String> JENKINS_CREDENTIAL_LIST = []

    // Jenkins UI相關參數
    public static ArrayList CUSTOM_PARAMETERS = []
    public static ArrayList CUSTOM_PROPERTIES = []

    // structure已定義
    public static DoBuildHandler DO_BUILD_HANDLER
    public static String CRON_EXPRESSION
    public static ConsulKVBaseInfo CONSUL_KV
    public static String OPADMIN_USER_NAME
    public static String OPADMIN_PASSWORD
    public static ArrayList OPADMIN_CREDENTIAL
    public static DockerImageNameMaker DOCKER_IMAGE_NAME_MAKER

    Config(LinkedHashMap config) {
        super(config)
    }
}
