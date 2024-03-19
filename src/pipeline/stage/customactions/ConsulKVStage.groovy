package pipeline.stage.customactions

import pipeline.artifact.docker.cli.CommandActuator
import pipeline.artifact.docker.cli.run.RunConfig
import pipeline.common.util.Config
import pipeline.common.consul.ConsulKVInfo
import pipeline.common.util.ProjectInfraState
import pipeline.stage.flowstages.BuildAnsibleConsulImageStage
import tools.AnsibleParser
import pipeline.stage.stagedatas.ConsulKVStage as ConsulKVStageData
import pipeline.stage.util.CustomAction
import pipeline.stage.util.ProjectInfraTemplate
import pipeline.stage.util.StageData
import tools.yamlmodel.scripts.AnsibleConsulKV

@SuppressWarnings('unused')
class ConsulKVStage extends CustomAction {
    private static final String TargetTaskName = 'Print ConsulKV Value'
    private static final String AnsibleHostsFileName = 'ansible_hosts.yaml'
    private static final String AnsiblePlaybookFileName = 'ansible_playbook.yaml'
    private static final String targetKey = 'localhost'

    void main(Config config, StageData stageData) {
        LinkedHashMap stageArgs = stageData.StageArgs
        stage(stageData.Desc) {
            node(config.DEPLOY_NODE) {
                timestamps getMainClosure(config, stageArgs)
            }
        }
    }

    private static Closure getMainClosure(
            Config config,
            LinkedHashMap stageArgs
    ) {
        return {
            makeAnsibleYamlFile(stageArgs)
            checkAnsibleConsulImage(config)
            settingConfigInfraState(config, stageArgs, generateTemplate())
        }
    }

    private static void makeAnsibleYamlFile(LinkedHashMap stageArgs) {
        writeFile file: AnsibleHostsFileName, text: ansibleHostsYaml
        writeFile file: AnsiblePlaybookFileName, text: getPlaybookContent(stageArgs)
    }

    private static String getAnsibleHostsYaml() {
        return AnsibleConsulKV.inventoryLocalHostYaml
    }

    private static String getPlaybookContent(LinkedHashMap stageArgs) {
        return AnsibleConsulKV.getGetConsulValuePlaybookYaml(
                getConsulKVInfo(stageArgs),
                TargetTaskName
        )
    }

    private static ConsulKVInfo getConsulKVInfo(LinkedHashMap stageArgs) {
        return stageArgs[ConsulKVStageData.CONSUL_KV_INFO] as ConsulKVInfo
    }

    private static void checkAnsibleConsulImage(Config config) {
        new BuildAnsibleConsulImageStage().main(config)
    }

    private static ProjectInfraTemplate generateTemplate() {
        return parseAnsibleResult(runAnsiblePlaybook())
    }

    private static String runAnsiblePlaybook() {
        def dockerRunConfig = new RunConfig(
                BuildAnsibleConsulImageStage.DOCKER_IMAGE,
                dockerRunCommand
        )
        dockerRunConfig.setRM(true)
                .setNetworkMode('host')
                .addVolumeString(convertVolumeString(AnsibleHostsFileName))
                .addVolumeString(convertVolumeString(AnsiblePlaybookFileName))
        try {
            return new CommandActuator(dockerRunConfig).run()
        } catch (Exception e) {
            EchoStep("Failed to run ansible-playbook.\nError Message: ${e.message}")
            throw e
        }
    }

    private static String getDockerRunCommand() {
        return "ansible-playbook -i $AnsibleHostsFileName $AnsiblePlaybookFileName"
    }

    private static String convertVolumeString(String fileName) {
        return "./$fileName:${BuildAnsibleConsulImageStage.ANSIBLE_WORK_DIR}/$fileName"
    }

    private static ProjectInfraTemplate parseAnsibleResult(String playbookResult) {
        LinkedHashMap resultMap = new AnsibleParser(
                TargetTaskName,
                playbookResult
        ).executeParse()
        if (!resultMap.containsKey(targetKey)) {
            throw new Exception('ProjectInfraDeployState localhost not found.')
        }
        return new ProjectInfraTemplate(resultMap[targetKey] as LinkedHashMap)
    }

    private static ProjectInfraState settingConfigInfraState(
            Config config,
            LinkedHashMap stageArgs,
            ProjectInfraTemplate template
    ) {
        config.PROJECT_INFRA_STATE = template.generateInfraState(
                stageArgs[ConsulKVStageData.CREDENTIAL_ID] as String
        )
    }
}
