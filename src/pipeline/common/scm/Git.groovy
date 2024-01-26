package pipeline.common.scm

import pipeline.common.ui.panels.GitREVersion
import pipeline.Pipeline
import pipeline.common.util.Config

class Git extends Pipeline {

    static void checkout(Config config, boolean isCIFlow) {
        def checkoutInfoSB = new StringBuilder('### Checkout Git ###\n')
        def gitREVersion = new GitREVersion(config)
        String branch = "*/${BRANCH_NAME}"
        if (isCIFlow && !gitREVersion.isGitREVersionNone) {
            branch = gitREVersion.gitREVersion
            checkoutInfoSB.append("Use Git REVersion: ${branch}\n")
        }
        checkoutInfoSB.append("Branch: ${branch}")
        EchoStep(checkoutInfoSB.toString())
        LinkedHashMap checkoutArgs = [
                $class                           : 'GitSCM',
                branches                         : [[name: "${branch}"]],
                browser                          : [$class: 'AssemblaWeb', repoUrl: ''],
                doGenerateSubmoduleConfigurations: false,
                extensions                       : extensions,
                submoduleCfg                     : [],
                userRemoteConfigs                : getUserRemoteConfigs(config)
        ]
        EchoStep("CheckoutArgs:\n${checkoutArgs}")
        checkout(checkoutArgs)
    }

    private static ArrayList getExtensions() {
        LinkedHashMap checkoutOptionMap = [
                $class : 'CheckoutOption',
                timeout: 300
        ]
        LinkedHashMap cloneOptionMap = [
                $class   : 'CloneOption',
                depth    : 0,
                noTags   : false,
                reference: WORKSPACE,
                shallow  : true,
                timeout  : 300
        ]
        return [checkoutOptionMap, cloneOptionMap]
    }

    private static ArrayList getUserRemoteConfigs(Config config) {
        LinkedHashMap remoteConfigMap = [
                url: config.GIT_URL
        ]
        if (config.GIT_CREDENTIALS_ID != null) {
            remoteConfigMap.credentialsId = config.GIT_CREDENTIALS_ID
        }
        return [remoteConfigMap]
    }
}
