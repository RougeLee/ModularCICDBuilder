package pipeline.common.util

import pipeline.Pipeline

class ProjectInfraState extends Pipeline {
    protected String credentialId
    protected LinkedHashMap<String, ArrayList<String>> osTypes
    protected LinkedHashMap<String, ArrayList<String>> deployTags
    protected LinkedHashMap<String, LinkedHashMap> infraList

    ProjectInfraState(
            String credentialId,
            LinkedHashMap osTypes,
            LinkedHashMap deployTags,
            LinkedHashMap infraList
    ) {
        this.credentialId = credentialId
        this.osTypes = osTypes
        this.deployTags = deployTags
        this.infraList = infraList
    }

    String getCredentialId() {
        return credentialId
    }

    LinkedHashMap<String, ArrayList<String>> getOsTypes() {
        return osTypes
    }

    LinkedHashMap<String, ArrayList<String>> getDeployTags() {
        return deployTags
    }

    LinkedHashMap<String, LinkedHashMap> getInfraList() {
        return infraList
    }
}
