package pipeline.stage.util

import com.cloudbees.groovy.cps.NonCPS
import pipeline.common.constants.ConsulKVDeployState
import pipeline.common.util.ProjectInfraState

class ProjectInfraTemplate {
    private static final String CUSTOM_VAR = ConsulKVDeployState.CUSTOM_VAR
    private static final String DEPLOY_TAGS = ConsulKVDeployState.DEPLOY_TAGS
    private static final String OS_TYPE = ConsulKVDeployState.OS_TYPE
    private static final String PRIVATE_IP = ConsulKVDeployState.PRIVATE_IP
    private static final String PUBLIC_IP = ConsulKVDeployState.PUBLIC_IP
    private static final String NAME = ConsulKVDeployState.NAME
    private static final ArrayList<String> INFRA_INFO_CLOSURE_SORT = [
            NAME,
            PRIVATE_IP,
            PUBLIC_IP,
            CUSTOM_VAR,
            OS_TYPE
    ]
    private LinkedHashMap<String, Closure> infraInfoClosureMap = [:]
    private LinkedHashMap<String, Serializable> infraInfoMap
    private LinkedHashMap<String, ArrayList<LinkedHashMap<String, Serializable>>> mergeInfraInfoMap = [:]
    private LinkedHashMap<String, Serializable> osTypes = [:]
    private LinkedHashMap<String, Serializable> deployTags = [:]
    private LinkedHashMap<String, Map<String, Serializable>> infraList = [:]

    ProjectInfraTemplate(LinkedHashMap infraInfoMap) {
        this.infraInfoMap = infraInfoMap
        initInfraInfoClosureMap()
        parseInfraInfo()
    }

    ProjectInfraState generateInfraState(String credentialID) {
        return new ProjectInfraState(
                credentialID,
                osTypes,
                deployTags,
                infraList
        )
    }

    @NonCPS
    private void initInfraInfoClosureMap() {
        infraInfoClosureMap[NAME] = nameClosure
        infraInfoClosureMap[PRIVATE_IP] = privateIPClosure
        infraInfoClosureMap[PUBLIC_IP] = publicIPClosure
        infraInfoClosureMap[CUSTOM_VAR] = customVarClosure
        infraInfoClosureMap[OS_TYPE] = osTypeClosure
    }

    @NonCPS
    private void parseInfraInfo() {
        LinkedHashMap routerMap = [:]
        for (def item : infraInfoMap) {
            String consulKVRouterPath = item.key
            int lastIndex = consulKVRouterPath.lastIndexOf('/')
            String router = consulKVRouterPath.substring(lastIndex + 1)
            if (!infraInfoClosureMap.containsKey(router)) {
                continue
            }
            if (routerMap.containsKey(router)) {
                throw new Exception("routerMap router=${router} already exists.")
            }
            routerMap[router] = item.value as LinkedHashMap
        }

        INFRA_INFO_CLOSURE_SORT.each { String consulKVDeployState ->
            if (!routerMap.containsKey(consulKVDeployState)) {
                throw new Exception("routerMap consulKVDeployState=${consulKVDeployState} not found.")
            }
            infraInfoClosureMap[consulKVDeployState](routerMap[consulKVDeployState])
        }

        for (def item : mergeInfraInfoMap) {
            def gceList = item.value as ArrayList<LinkedHashMap>
            gceList.each { LinkedHashMap gce ->
                String instanceName = gce[NAME]
                if (infraList.containsKey(instanceName)) {
                    throw new Exception("infraList instanceName=${instanceName} already exists.")
                }
                infraList[instanceName] = gce
            }
        }
    }

    private Closure nameClosure = { LinkedHashMap consulValueMap ->
        for (def item : consulValueMap) {
            String gceName = item.key
            if (mergeInfraInfoMap.containsKey(gceName)) {
                throw new Exception("mergeInfraInfoMap gceName=${gceName} already exists.")
            }
            ArrayList<LinkedHashMap<String, Serializable>> gceList = []
            mergeInfraInfoMap[gceName] = gceList
            def instanceNameList = item.value as ArrayList<String>
            for (int i = 0; i < instanceNameList.size(); i++) {
                String instanceName = instanceNameList[i]
                LinkedHashMap gce = [:]
                gce[NAME] = instanceName
                gceList[i] = gce
            }
        }
    }

    private Closure customVarClosure = { LinkedHashMap consulValueMap ->
        for (def item : consulValueMap) {
            String gceName = item.key
            if (!mergeInfraInfoMap.containsKey(gceName)) {
                throw new Exception("mergeInfraInfoMap gceName=${gceName} not found.")
            }
            def instanceDeployDataList = item.value as ArrayList<LinkedHashMap>
            if (instanceDeployDataList.size() == 0) {
                continue
            }
            setDeployTagsClosure(
                    deployTags,
                    instanceDeployDataList,
                    mergeInfraInfoMap[gceName] as ArrayList
            )
        }
    }

    private Closure setDeployTagsClosure = {
        LinkedHashMap deployTags,
        ArrayList<LinkedHashMap> instanceDeployDataList,
        ArrayList gceList ->
            for (int i = 0; i < instanceDeployDataList.size(); i++) {
                LinkedHashMap instanceDeployData = instanceDeployDataList[i]
                def gce = gceList[i] as LinkedHashMap
                gce.putAll(instanceDeployData)
                if (instanceDeployData.containsKey(DEPLOY_TAGS)) {
                    def infraDeployTags = instanceDeployData[DEPLOY_TAGS] as
                            ArrayList<String>
                    String instanceName = gce[NAME]
                    for (String deployTag : infraDeployTags) {
                        def deployTagsList = deployTags[deployTag] as
                                ArrayList<String>
                        if (deployTagsList == null) {
                            deployTagsList = []
                            deployTags[deployTag] = deployTagsList
                        }
                        if (!deployTagsList.contains(instanceName)) {
                            deployTags[deployTag] << instanceName
                        }
                    }
                }
            }
    }

    private Closure osTypeClosure = { LinkedHashMap consulValueMap ->
        for (def item : consulValueMap) {
            String gceName = item.key
            if (!mergeInfraInfoMap.containsKey(gceName)) {
                throw new Exception("mergeInfraInfoMap gceName=${gceName} not found.")
            }
            def osTypeList = item.value as ArrayList<String>
            if (osTypeList.size() == 0) {
                continue
            }
            setOSTypesClosure(
                    osTypes,
                    osTypeList,
                    mergeInfraInfoMap[gceName] as ArrayList
            )
        }
    }

    private Closure setOSTypesClosure = {
        LinkedHashMap oSTypes,
        ArrayList<String> osTypeList,
        ArrayList gceList ->
            for (int i = 0; i < osTypeList.size(); i++) {
                String osName = osTypeList[i]
                def gce = gceList[i] as LinkedHashMap
                gce[OS_TYPE] = osName
                def osTypeMapInstanceName = oSTypes[osName] as
                        ArrayList<String>
                if (osTypeMapInstanceName == null) {
                    osTypeMapInstanceName = []
                    oSTypes[osName] = osTypeMapInstanceName
                }
                String instanceName = gce[NAME]
                if (!osTypeMapInstanceName.contains(instanceName)) {
                    osTypeMapInstanceName << instanceName
                }
            }
    }

    private Closure privateIPClosure = { LinkedHashMap consulValueMap ->
        addIPClosure(mergeInfraInfoMap, consulValueMap, PRIVATE_IP)
    }

    private Closure publicIPClosure = { LinkedHashMap consulValueMap ->
        addIPClosure(mergeInfraInfoMap, consulValueMap, PUBLIC_IP)
    }

    private Closure addIPClosure = {
        LinkedHashMap mergeInfraInfoMap,
        LinkedHashMap consulValueMap,
        String ipType ->
            for (item in consulValueMap) {
                String gceName = item.key
                if (!mergeInfraInfoMap.containsKey(gceName)) {
                    throw new Exception("mergeInfraInfoMap gceName=${gceName} not found.")
                }
                def ipList = item.value as ArrayList<String>
                def gceList = mergeInfraInfoMap[gceName] as ArrayList<LinkedHashMap>
                for (int i = 0; i < ipList.size(); i++) {
                    String ip = ipList[i]
                    def gce = gceList[i] as LinkedHashMap
                    gce[ipType] = ip
                }
            }
    }
}
