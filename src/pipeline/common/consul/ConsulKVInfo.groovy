package pipeline.common.consul

import com.cloudbees.groovy.cps.NonCPS

class ConsulKVInfo {
    private ConsulKVBaseInfo consulKVBaseInfo
    private String projectName
    private String infraName
    private String token

    ConsulKVInfo(
            ConsulKVBaseInfo consulKVBaseInfo,
            String projectName,
            String infraName,
            String token
    ) {
        this.consulKVBaseInfo = consulKVBaseInfo
        this.projectName = projectName
        this.infraName = infraName
        this.token = token
        validate()
    }

    @NonCPS
    void validate() {
        assert consulKVBaseInfo != null
        assert projectName != null
        assert infraName != null
        assert token != null
    }

    String getHost() {
        return consulKVBaseInfo.getHost()
    }

    String getKey() {
        return ConsulKVBaseInfo.getKey(projectName, infraName)
    }

    String getToken() {
        return token
    }
}
