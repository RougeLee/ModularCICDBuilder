package pipeline.stage.util

/**
 * Stage的配置檔
 */
class StageData extends Base {
    /**
     * 設定要呼叫指定的Stage Name
     */
    public String StageName = this.class.getSimpleName()
    /**
     * 定義Stage的描述
     */
    public String Desc = this.class.getSimpleName()
    /**
     * Stage的參數
     */
    public final LinkedHashMap StageArgs = [:]
    /**
     * Stage的Timeout時間(分鐘)
     */
    public int TimeoutMinutes = 0
    /**
     * 需要驗證的參數
     */
    protected final ArrayList<String> assertKeys = []

    protected LinkedHashMap<Serializable, Serializable> moduleArgs

    protected StageData(
            LinkedHashMap<Serializable, Serializable> moduleArgs
    ) {
        this.moduleArgs = moduleArgs
    }

    StageData(
            String stageName,
            LinkedHashMap<Serializable, Serializable> moduleArgs
    ) {
        this.StageName = stageName
        this.Desc = stageName
        this.moduleArgs = moduleArgs
    }

    protected void init() {
        StageArgs << moduleArgs
    }

    /**
     * 執行初始化，若有定義init Closure，則執行
     */
    void execInit() {
        initStart()
        if (moduleArgs.containsKey('Desc')) {
            this.Desc = moduleArgs.Desc
        }
        assertModuleArgs()
        try {
            init()
        }
        catch (Exception e) {
            throw initStageDataFail(e)
        }
        initDone()
    }

    protected Exception initStageDataFail(Exception e) {
        String errorMessage = new StringBuilder()
                .append("StageData: ${this.class.getSimpleName()} init fail.\n")
                .append("Error Message:\n${e}")
                .toString()
        return new Exception(errorMessage)
    }

    protected void assertModuleArgs() {
        assertKeys.each { String key ->
            assert moduleArgs.containsKey(key)
            assert moduleArgs[key] != null
        }
    }

    protected void moduleArgsMapping() {
        assertKeys.each { String key ->
            StageArgs[key] = moduleArgs[key]
        }
    }

    protected void initStart() {
        EchoStep("${StageName} stage arguments init begins.")
    }

    protected void initDone() {
        EchoStep("${StageName} stage arguments init done.")
    }
}
