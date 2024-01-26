package pipeline.stage.customactions

import pipeline.artifact.ansible.AnsibleConsulImageManager
import pipeline.artifact.docker.cli.CommandActuator
import pipeline.artifact.docker.cli.run.RunConfig
import pipeline.common.util.Config
import pipeline.common.consul.ConsulKVInfo
import pipeline.common.util.ProjectInfraState
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
    private static final String DockerImage = 'ansible-consul'
    private static final String AnsibleWorkdir = '/ansible'
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
            checkAnsibleConsulImage()
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
        return AnsibleConsulKV.getConsulKVPlaybookYaml(
                getConsulKVInfo(stageArgs),
                TargetTaskName
        )
    }

    private static ConsulKVInfo getConsulKVInfo(LinkedHashMap stageArgs) {
        return stageArgs[ConsulKVStageData.CONSUL_KV_INFO] as ConsulKVInfo
    }

    private static void checkAnsibleConsulImage() {
        new AnsibleConsulImageManager(DockerImage, AnsibleWorkdir).main()
    }

    private static ProjectInfraTemplate generateTemplate() {
        return parseAnsibleResult(runAnsiblePlaybook())
    }

    private static String runAnsiblePlaybook() {
        RunConfig dockerRunConfig = new RunConfig(DockerImage, dockerRunCommand)
                .setRM(true)
                .addVolumeString(convertVolumeString(AnsibleHostsFileName))
                .addVolumeString(convertVolumeString(AnsiblePlaybookFileName))
        return new CommandActuator(dockerRunConfig).run()
    }

    private static String getDockerRunCommand() {
        return "ansible-playbook -i $AnsibleHostsFileName $AnsiblePlaybookFileName"
    }

    private static String convertVolumeString(String fileName) {
        return "./$fileName:$AnsibleWorkdir/$fileName"
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
