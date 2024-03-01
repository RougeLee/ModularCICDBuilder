package pipeline.common.util

import java.lang.reflect.Field

abstract class ConfigMethods extends BaseStructure {
    protected static final int FieldModifier = 26
    protected static final ArrayList<String> skipFillingFieldList = [
            'config',
            'subClassName',
            'isInitPool',
            'skipFillingFieldList',
    ]

    ConfigMethods(LinkedHashMap config) {
        super(config)
    }

    static void configFieldNotExist(String key) {
        skipFillingFieldList.add(key)
    }

    /**
     * 將 Config類filed中的值，進行填充到Map config中
     */
    void fillingConfigFieldsToMapValue() {
        ArrayList<Field> configFields = this.class.getDeclaredFields()
        configFields.each { Field field ->
            if (field.modifiers > FieldModifier) {
                return
            }
            String fieldName = field.name
            config[fieldName] = this[fieldName]
        }
    }

    /**
     * 將 Map config 中的值，進行填充到Config類filed中
     */
    @SuppressWarnings('unused')
    void fillingConfigFields() {
        if (!getInitStatus()) {
            echo("Config not init, skip filling filed")
            return
        }
        doMapToField()
    }

    String getConstConfig() {
        StringBuilder ConstConfigSb = new StringBuilder()
        ConstConfigSb.append('ConfigMap Value Print...\n')
        String ConstConfigPrefix = '[Config Map] '
        for (configItem in config) {
            ConstConfigSb.append(ConstConfigPrefix)
            ConstConfigSb.append(configItem.key)
            ConstConfigSb.append(' = ')
            ConstConfigSb.append(configItem.value)
            ConstConfigSb.append('\n')
        }
        return ConstConfigSb.toString()
    }

    protected void structureInitProcess() {
        StructuresInitHandler.initAllStructures(config)
        doMapToField()
    }

    protected void doMapToField() {
        for (def kvp : config) {
            String key = kvp.key
            if (skipFillingFieldList.contains(key)) {
                continue
            }
            def value = kvp.value
            try {
                this[key] = value
            } catch (ignored) {
                configFieldNotExist(key)
            }
        }
    }
}
