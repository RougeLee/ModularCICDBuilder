package pipeline.module.strategy

import pipeline.artifact.docker.compose.ComposeDeployAssembler
import pipeline.artifact.docker.compose.ComposeDeploymentConfig
import pipeline.artifact.docker.compose.DeploymentSetupManager
import pipeline.artifact.docker.compose.DockerComposeDeployer
import pipeline.artifact.docker.compose.DockerComposeExecutor
import pipeline.artifact.docker.compose.DeployersConvertExecutorsManager
import pipeline.common.util.Config
import pipeline.common.util.StrategyTuple
import pipeline.module.lib.ConsulKVStrategyTuple
import pipeline.module.lib.DockerComposeLib
import pipeline.module.util.StrategyList
import pipeline.stage.stagedatas.DeploymentPreparation
import pipeline.stage.stagedatas.DockerComposeAssembly
import pipeline.stage.stagedatas.EnvironmentSetup
import pipeline.stage.stagedatas.ServiceLaunch
import pipeline.stage.stagedatas.PrintDockerCompose
import pipeline.stage.stagedatas.SSHConnectionCreate
import pipeline.stage.stagedatas.ServiceVerification

@SuppressWarnings('unused')
class DockerCompose extends StrategyList {
    private LinkedHashMap<ArrayList<String>, DockerComposeExecutor> executors = [:]
    private LinkedHashMap<ArrayList<String>, DockerComposeDeployer> deployers = [:]

    DockerCompose(Config config, LinkedHashMap<Serializable, Serializable> moduleArgs) {
        super(config, moduleArgs)
        requiredArgNames << DockerComposeLib.DOCKER_COMPOSE_TOP_LEVEL
        requiredArgNames << DockerComposeLib.DOCKER_COMPOSE_SERVICES
    }

    ArrayList<StrategyTuple> getStrategyList() {
        verifyModuleArgs()
        applyExecutorsWithDeployersModuleArgs()
        applyConsulKVStrategyTuple()
        applyComposeAssemblyStrategyTuple()
        applyDockerComposeStageList()
        return strategyTuples
    }

    private void applyExecutorsWithDeployersModuleArgs() {
        moduleArgs[DockerComposeLib.DOCKER_COMPOSE_EXECUTOR_MAP] = executors
        moduleArgs[DockerComposeLib.DOCKER_COMPOSE_DEPLOYER_MAP] = deployers
    }

    private void applyConsulKVStrategyTuple() {
        ConsulKVStrategyTuple.applyConsulKVStrategyTuple(
                strategyTuples,
                config,
                moduleArgs
        )
    }

    private void applyComposeAssemblyStrategyTuple() {
        strategyTuples << new StrategyTuple([
                new DockerComposeAssembly(moduleArgs),
                new PrintDockerCompose(moduleArgs)
        ])
    }

    private void applyDockerComposeStageList() {
        def setupManager = generateDeploymentSetupManager()
        def convertManager = generateDeployersConvertExecutorsManager()
        ArrayList dockerComposeStageList = []
        dockerComposeStageList << new SSHConnectionCreate(moduleArgs, convertManager)
        dockerComposeStageList << new EnvironmentSetup(moduleArgs, setupManager)
        dockerComposeStageList << new DeploymentPreparation(moduleArgs, setupManager)
        dockerComposeStageList << new ServiceLaunch(moduleArgs, setupManager)
        dockerComposeStageList << new ServiceVerification(moduleArgs, setupManager)
        strategyTuples << new StrategyTuple(dockerComposeStageList)
    }

    private DeploymentSetupManager generateDeploymentSetupManager() {
        ComposeDeploymentConfig deployConfig = new ComposeDeploymentConfig(
                config,
                moduleArgs
        ).init()
        def deployAssembler = new ComposeDeployAssembler(config, deployConfig)
        return new DeploymentSetupManager(config, executors, deployAssembler)
    }

    private DeployersConvertExecutorsManager generateDeployersConvertExecutorsManager() {
        return new DeployersConvertExecutorsManager(executors, deployers)
    }
}