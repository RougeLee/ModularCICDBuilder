package pipeline.stage.customactions

import pipeline.artifact.cd.CollectDeployTagsList
import pipeline.artifact.cd.DeploymentConfigManager
import pipeline.artifact.docker.compose.DockerComposeDeployer
import pipeline.artifact.docker.compose.ComposeUnit
import pipeline.artifact.docker.compose.ServiceConfigurationSetup
import pipeline.artifact.cd.PuzzleCombiner
import pipeline.artifact.docker.panels.SpecifyImageTag
import pipeline.common.util.Config
import pipeline.module.lib.DockerComposeLib
import pipeline.stage.flowstages.CheckOutStage
import pipeline.stage.util.CustomAction
import pipeline.stage.util.StageData

@SuppressWarnings('unused')
class DockerComposeAssembly extends CustomAction {

    void main(Config config, StageData stageData) {
        checkConfigData(config, stageData)
        node(config.DEPLOY_NODE) {
            execCheckout(config)
            stage(stageData.Desc) {
                initComposeDeployerMap(config, stageData.StageArgs)
            }
        }
    }

    private static void checkConfigData(Config config, StageData stageData) {
        try {
            if (config.PROJECT_CODE == null) {
                throw new Exception('PROJECT_CODE is null.')
            }
            if (config.PROJECT_INFRA_STATE == null) {
                throw new Exception('PROJECT_INFRA_STATE is null.')
            }
            if (config.DOCKER_IMAGE_NAME_MAKER == null) {
                throw new Exception('DOCKER_IMAGE_NAME_MAKER is null.')
            }
        } catch (Exception e) {
            stage(stageData.Desc) {
                EchoStep('config data error: ' + e.message)
            }
            throw e
        }
    }

    private static void execCheckout(Config config) {
        new CheckOutStage().setIsCIFlow(false).main(config)
    }

    private static void initComposeDeployerMap(Config config, LinkedHashMap stageArgs) {
        ArrayList<ComposeUnit> composeUnits = DockerComposeLib.getComposeUnits(stageArgs)
        ArrayList<ArrayList<String>> tagsList = new CollectDeployTagsList(config).tagsList
        ArrayList<ArrayList<ComposeUnit>> unitsList = getComposeUnitCombinationsList(
                composeUnits,
                tagsList
        )
        validateCombinationsList(unitsList, tagsList)
        deployComposeUnits(
                config,
                unitsList,
                tagsList,
                DockerComposeLib.getDeployers(stageArgs),
                getComposeTopLevel(stageArgs)
        )
    }

    private static LinkedHashMap<String, Serializable> getComposeTopLevel(
            LinkedHashMap stageArgs
    ) {
        return stageArgs[DockerComposeLib.DOCKER_COMPOSE_TOP_LEVEL] as
                LinkedHashMap<String, Serializable>
    }

    private static ArrayList<ArrayList<ComposeUnit>> getComposeUnitCombinationsList(
            ArrayList<ComposeUnit> composeUnits,
            ArrayList<ArrayList<String>> deployTagsList
    ) {
        LinkedHashMap<String, ArrayList<ComposeUnit>> composeUnitPuzzles = [:]
        composeUnits.each { ComposeUnit composeUnit ->
            composeUnit.tags.each { String tag ->
                ArrayList<ComposeUnit> units = composeUnitPuzzles.getOrDefault(tag, [])
                composeUnitPuzzles[tag] = units
                units << composeUnit
            }
        }
        PuzzleCombiner puzzleCombiner = new PuzzleCombiner(deployTagsList)
        return puzzleCombiner.computeCombinations(composeUnitPuzzles) as
                ArrayList<ArrayList<ComposeUnit>>
    }

    private static void validateCombinationsList(
            ArrayList<ArrayList<ComposeUnit>> composeUnitCombinationsList,
            ArrayList<ArrayList<String>> deployTagsList
    ) {
        if (composeUnitCombinationsList.size() != deployTagsList.size()) {
            throw new Exception('composeUnitCombinationsList.size() != deployTagsList.size()')
        }
        for (int i = 0; i < composeUnitCombinationsList.size(); i++) {
            ArrayList<ComposeUnit> composeUnits = composeUnitCombinationsList[i]
            ArrayList<String> tags = deployTagsList[i]
            StringBuilder TagsDiffComposeUnits = new StringBuilder()
                    .append("TagsDiffComposeUnits:\n")
                    .append("tags:\n$tags\n")
                    .append("composeUnits:\n")
            composeUnits.each { ComposeUnit composeUnit ->
                TagsDiffComposeUnits.append(composeUnit.info)
            }
            EchoStep(TagsDiffComposeUnits.toString())
        }
    }

    private static void deployComposeUnits(
            Config config,
            ArrayList<ArrayList<ComposeUnit>> CombinationsList,
            ArrayList<ArrayList<String>> deployTagsList,
            Map<ArrayList<String>, DockerComposeDeployer> deployers,
            LinkedHashMap<String, Serializable> composeTopLevel
    ) {
        for (int i = 0; i < deployTagsList.size(); i++) {
            def setup = new ServiceConfigurationSetup(
                    CombinationsList[i],
                    config.DOCKER_IMAGE_NAME_MAKER,
                    new SpecifyImageTag(config),
                    new DeploymentConfigManager('ComposeService')
            )
            deployers[deployTagsList[i]] = new DockerComposeDeployer(
                    composeTopLevel,
                    setup
            ).deployComposeServiceSetup()
        }
    }
}
