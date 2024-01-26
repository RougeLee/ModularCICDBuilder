package pipeline.common.util

import pipeline.Pipeline

abstract class BaseStructure extends Pipeline {
    protected static LinkedHashMap<String, Boolean> isInitPool = [:]
    protected LinkedHashMap config
    protected String subClassName

    BaseStructure(LinkedHashMap config) {
        this.config = config
        subClassName = this.class.name
    }

    abstract protected void initProcess()

    void init() {
        if (isInitPool.get(subClassName)) {
            return
        }
        initProcess()
        EchoStep("Init ${this.class.simpleName} success")
        isInitPool.put(subClassName, true)
    }

    @SuppressWarnings('unused')
    def getConfigProperty(String key, def defaultValue = null) {
        if (config.containsKey(key)) {
            return config.get(key)
        } else {
            return defaultValue
        }
    }

    void setConfigProperty(String key, def value, boolean isOverride = false) {
        updateConfigProperty(key, value)
        if (isOverride) {
            this[key] = value
        }
    }

    protected def initProperty(String key, def defaultValue = null) {
        def value
        if (!config.containsKey(key)) {
            value = defaultValue
        } else {
            value = config.get(key)
        }
        updateConfigProperty(key, value)
        return value
    }

    protected getInitStatus() {
        return isInitPool.get(subClassName)
    }

    protected void updateConfigProperty(String key, def value) {
        config[key] = value
        try {
            Config[key] = value
        } catch (ignored) {
            Config.configFieldNotExist(key)
        }
    }

    protected LinkedHashMap convertToLinkedHashMap() {
        return config
    }

    protected def asType(Class clazz) {
        init()
        if (clazz == LinkedHashMap) {
            return convertToLinkedHashMap()
        }
    }
}
