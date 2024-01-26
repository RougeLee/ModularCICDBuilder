package pipeline.common.util

import pipeline.Pipeline
import pipeline.common.constants.ConsulKVDeployState
import pipeline.common.constants.PlatformOSType
import pipeline.common.ssh.SSHRemote

class LinuxRemoteProvider extends Pipeline {
    private static final String[] OS_TYPES = PlatformOSType.LINUX_OS_TYPES
    private Config config
    private ArrayList<String> deployTags
    private Boolean requireExactMatch

    LinuxRemoteProvider(Config config) {
        this.config = config
    }

    /**
     * @param deployTags : The deploy tags to filter the remotes.
     * @param requireExactMatch :
     * if true, require all deployTags to be present in the infra's deployTags
     * if false, require at least one deployTag to be present in the infra's deployTags
     * if null, ignore deployTags
     */
    ArrayList<SSHRemote> getRemotes(
            ArrayList<String> deployTags,
            Boolean requireExactMatch = false
    ) {
        this.deployTags = deployTags
        this.requireExactMatch = requireExactMatch
        return publicIPList.collect({ String hostIP ->
            new SSHRemote(config, nodeId, credentialId, hostIP).init()
        })
    }

    private ArrayList<String> getPublicIPList() {
        ArrayList<String> publicIPList = []
        OS_TYPES.each { String osType ->
            applyHosts(publicIPList, osType)
        }
        return publicIPList
    }

    private void applyHosts(
            ArrayList<String> publicIPList,
            String osType
    ) {
        osTypes[osType].each { String infraName ->
            LinkedHashMap infra = getInfra(infraName)
            if (!doesTagMatchRequireCondition(infra)) {
                return
            }
            String publicIP = infra[ConsulKVDeployState.PUBLIC_IP]
            if (publicIP == null) {
                EchoStep("${infra[ConsulKVDeployState.NAME]} PublicIP is null.")
                return
            }
            publicIPList << publicIP
        }
    }

    private doesTagMatchRequireCondition(LinkedHashMap infra) {
        def infraTags = infra[ConsulKVDeployState.DEPLOY_TAGS] as ArrayList<String>
        switch (requireExactMatch) {
            case null:
                return true
            case false:
                return infraTags.containsAll(deployTags)
            case true:
                return infraTags.containsAll(deployTags) && deployTags.containsAll(infraTags)
        }
    }

    private ProjectInfraState getProjectInfraState() {
        return config.PROJECT_INFRA_STATE
    }

    private String getNodeId() {
        return config.DEPLOY_NODE
    }

    private String getCredentialId() {
        return projectInfraState.credentialId
    }

    private LinkedHashMap<String, ArrayList<String>> getOsTypes() {
        return projectInfraState.osTypes
    }

    private LinkedHashMap getInfra(String infraName) {
        return projectInfraState.infraList[infraName]
    }

}
