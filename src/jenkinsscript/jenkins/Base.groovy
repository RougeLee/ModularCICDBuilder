package jenkinsscript.jenkins

import com.cloudbees.groovy.cps.NonCPS
import jenkinsscript.JenkinsScript
import jenkinsscript.resource.ScriptMode
import jenkinsscript.steps.BaseStep

abstract class Base {
    public static Script script
    /**
     * org.jenkinsci.plugins.workflow.cps.EnvActionImpl
     */
    protected static def env
    protected static ScriptMode mode = ScriptMode.none
    protected static LinkedHashMap<String, BaseStep> stepsPool = [:]
    protected static Integer BeforeDefaultStackTraceMaxCount = 3
    protected static Integer defaultStackTraceMaxCount = 4
    private static String defaultUnknown = 'unknown'
    private static String GroovyExtension = '.groovy'

    /**
     * 設定Jenkins Script Mode
     * @param script
     */
    static setJenkinsMode(def script) {
        mode = ScriptMode.jenkins
        JenkinsScript.script = script
        env = script.env
        script.echo('Enable JenkinsStep')
    }

    /**
     * 取得Jenkins環境變數
     * @param key Jenkins環境變數名稱。
     */
    @NonCPS
    static String getEnvProperty(String key) {
        return env.getProperty(key)
    }

    /**
     * 取得Jenkins Property
     * @param key Jenkins Property名稱。
     */
    @NonCPS
    static Object getJenkinsProperty(String key) {
        return script.getProperty(key)
    }

    @NonCPS
    static def callMethod(String methodName, args) {
        if (mode == ScriptMode.jenkins) {
            return script.invokeMethod(methodName, args)
        }
        try {
            return script.invokeMethod(methodName, args)
        } catch (ignored) {
            return runSteps(methodName, args)
        }
    }

    @NonCPS
    static def runSteps(String methodName, args) {
        if (stepsPool.containsKey(methodName)) {
            return stepsPool.get(methodName).run(args)
        }
        def classLoader = JenkinsScript.class.getClassLoader()
        def stepClass = classLoader.loadClass("lib.steps.${methodName}")
        def step = (BaseStep) stepClass.newInstance(script)
        stepsPool.put(methodName, step)
        return step.run(args)
    }

    /**
     * 獲得有PIPELINE標籤與呼叫此消息來源的類與方法的消息
     * @param message 要寫入控制台輸出的消息。
     * @return
     */
    static String getStepStage(String message) {
        return getStepStage(BeforeDefaultStackTraceMaxCount, message)
    }

    /**
     * 獲得有PIPELINE標籤與呼叫此消息來源的類與方法的消息 (可指定堆棧深度)
     * @param stackTraceMaxCount 堆棧最大深度。
     * @param message 要寫入控制台輸出的消息。
     * @return
     */
    static String getStepStage(int stackTraceMaxCount, String message) {
        def className = defaultUnknown
        def methodName = defaultUnknown
        def stackTrace = new Throwable().stackTrace
        def count = 0
        def element = stackTrace.find {
            it.fileName?.endsWith(GroovyExtension) && ++count == stackTraceMaxCount
        }
        if (element) {
            className = element.getClassName()
            className = className.substring(className.lastIndexOf('.') + 1)
            methodName = element.getMethodName()
        }
        return "PIPELINE [${className}.${methodName}] ${message}"
    }
}
