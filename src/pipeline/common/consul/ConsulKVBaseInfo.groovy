package pipeline.common.consul

class ConsulKVBaseInfo {
    private static final String ProjectNamePrefix = 'Project-'
    private static final String consulRouterPath = 'Deploy-State'
    private String host

    ConsulKVBaseInfo(String host) {
        this.host = host
    }

    String getHost() {
        return host
    }

    static String getKey(String projectName, String infraName) {
        return new StringBuilder(ProjectNamePrefix)
                .append(projectName)
                .append('/')
                .append(infraName)
                .append('/')
                .append(consulRouterPath)
                .toString()
    }
}
