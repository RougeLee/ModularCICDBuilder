package tools.yamlmodel

@SuppressWarnings('unused')
class YamlModel {
    protected static final int ForwardSpaceAmount = 2
    protected static final String SpaceSymbol = ' '
    protected static final String NewLineSymbol = '\n'
    protected static final String ColonSymbol = ':'
    protected static final String DashSpaceSymbol = '- '
    protected int baseSpace = 0
    protected boolean isSkipFirstSpace = false
    protected String modelName
    protected LinkedHashMap mapping = null
    protected ArrayList blockList = null

    YamlModel(String modelName) {
        this.modelName = modelName
    }

    YamlModel(LinkedHashMap mapping) {
        this.mapping = mapping
    }

    YamlModel(ArrayList blockList) {
        this.blockList = blockList
    }

    void setBaseSpace(int baseSpace) {
        this.baseSpace = baseSpace
    }

    void setModelName(String modelName) {
        this.modelName = modelName
    }

    String getModelName() {
        return modelName
    }

    def getYamlModel() {
        def value = mapping ?: blockList
        if (modelName != null) {
            LinkedHashMap result = new LinkedHashMap()
            result.put(modelName, value)
            return result
        }
        return value
    }

    void addItem(YamlModel yamlModel) {
        String modelName = yamlModel.getModelName()
        def item = yamlModel.getYamlModel()
        if (blockList != null) {
            if (modelName != null) {
                blockList.add([[modelName]: item])
                return
            }
            blockList.add(item)
            return
        }
        if (mapping != null) {
            if (modelName != null) {
                mapping.put(modelName, item)
                return
            }
            if (item.getClass() != LinkedHashMap) {
                throw new Exception("YamlModel addItem error, item class is not LinkedHashMap")
            }
            mapping.putAll(item as LinkedHashMap)
        }
        switch (item.getClass()) {
            case LinkedHashMap:
                mapping = item as LinkedHashMap
                break
            case ArrayList:
                blockList = item as ArrayList
                break
            default:
                throw new Exception("YamlModel addItem error, item class is not LinkedHashMap or ArrayList")
        }
    }

    void addItem(String itemName, def item) {
        assert blockList == null
        if (mapping == null) {
            mapping = new LinkedHashMap()
        }
        mapping.put(itemName, item)
    }

    void addItem(def item) {
        assert mapping == null
        if (blockList == null) {
            blockList = new ArrayList()
        }
        blockList.add(item)
    }

    void applyStringBuilder(StringBuilder sb) {
        if (modelName != null) {
            mappingApplyKey(sb, modelName)
            sb.append(NewLineSymbol)
            baseSpace += ForwardSpaceAmount
        }
        if (mapping != null) {
            mappingToStringBuffer(sb)
            return
        }
        blockListToStringBuffer(sb)
    }

    void setSkipFirstSpace() {
        this.isSkipFirstSpace = true
    }

    protected int getSpaceCount() {
        if (isSkipFirstSpace) {
            isSkipFirstSpace = false
            return 0
        }
        return baseSpace
    }

    protected int getSubSpaceCount() {
        return getSpaceCount() + ForwardSpaceAmount
    }

    protected static YamlModel getHandleModel(def item) {
        switch (item.getClass()) {
            case YamlModel:
                return item
            case LinkedHashMap:
                return new YamlModel(item as LinkedHashMap)
            case ArrayList:
                return new YamlModel(item as ArrayList)
            default:
                return null
        }
    }

    protected void mappingApplyKey(StringBuilder sb, String mappingKey) {
        sb.append(SpaceSymbol * getSpaceCount())
        sb.append(mappingKey)
        sb.append(ColonSymbol)
    }

    protected void mappingToStringBuffer(StringBuilder sb) {
        for (String mappingKey : mapping.keySet()) {
            mappingApplyKey(sb, mappingKey)
            int subSpace = getSubSpaceCount()
            def mappingValue = mapping[mappingKey]
            YamlModel handleModel = getHandleModel(mappingValue)
            if (handleModel != null) {
                sb.append(NewLineSymbol)
                handleModel.setBaseSpace(subSpace)
                handleModel.applyStringBuilder(sb)
                continue
            }
            sb.append(SpaceSymbol)
            sb.append(mappingValue)
            sb.append(NewLineSymbol)
        }
    }

    protected void blockListToStringBuffer(StringBuilder sb) {
        for (def item : blockList) {
            sb.append(SpaceSymbol * getSpaceCount())
            sb.append(DashSpaceSymbol)
            YamlModel itemModel = getHandleModel(item)
            if (itemModel != null) {
                itemModel.setBaseSpace(getSubSpaceCount())
                itemModel.setSkipFirstSpace()
                itemModel.applyStringBuilder(sb)
                continue
            }
            sb.append(item)
            sb.append(NewLineSymbol)
        }
    }
}
