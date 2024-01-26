package pipeline.artifact.cd

import pipeline.common.constants.ConsulKVDeployState
import pipeline.common.util.Config

class CollectDeployTagsList {
    private Config config

    CollectDeployTagsList(Config config) {
        this.config = config
    }

    LinkedHashSet<ArrayList<String>> getTagsList() {
        ArrayList<ArrayList<String>> deployTagsList = []
        infraList.each {
            String infraName, LinkedHashMap infraMap ->
                ArrayList<String> deployTags = getDeployTags(infraMap)
                if (deployTags.size() == 0) {
                    return
                }
                deployTagsList << deployTags
        }
        return new LinkedHashSet(deployTagsList)
    }

    private LinkedHashMap<String, LinkedHashMap> getInfraList() {
        return config.PROJECT_INFRA_STATE.infraList
    }

    private static ArrayList<String> getDeployTags(LinkedHashMap infraMap) {
        return infraMap[ConsulKVDeployState.DEPLOY_TAGS] as ArrayList<String>
    }
}
