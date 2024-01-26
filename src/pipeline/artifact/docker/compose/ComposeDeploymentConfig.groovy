package pipeline.artifact.docker.compose

import pipeline.Pipeline
import pipeline.common.util.Config
import pipeline.module.lib.DockerComposeLib as Lib

class ComposeDeploymentConfig extends Pipeline {
    private String projectCode
    private String branchName
    private String deployOwner
    private ArrayList<ComposeConfigUnit> environmentSetupShells
    private ArrayList<ComposeConfigUnit> prepareStopShells
    private ArrayList<ComposeConfigUnit> postDownShells
    private ArrayList<ComposeConfigUnit> transferConfigurationFiles
    private ArrayList<ComposeConfigUnit> prepareServiceLaunchShells
    private ArrayList<ComposeConfigUnit> serviceTestingShells
    private Config config
    private LinkedHashMap moduleArgs

    ComposeDeploymentConfig(
            Config config,
            LinkedHashMap moduleArgs
    ) {
        this.config = config
        this.moduleArgs = moduleArgs
    }

    ComposeDeploymentConfig(
            String projectCode,
            String branchName,
            String deployOwner
    ) {
        this.projectCode = projectCode
        this.branchName = branchName
        this.deployOwner = deployOwner
    }

    ComposeDeploymentConfig init() {
        initProjectCode()
        initBranchName()
        initDeployOwner()
        initEnvironmentSetupShells()
        initPrepareStopShells()
        initPostDownShells()
        initTransferConfigurationFiles()
        initPrepareServiceLaunchShells()
        initServiceTestingShells()
        return this
    }

    String getProjectCode() {
        return projectCode
    }

    String getBranchName() {
        return branchName
    }

    String getDeployOwner() {
        return deployOwner
    }

    ArrayList<ComposeConfigUnit> getEnvironmentSetupShells() {
        return environmentSetupShells
    }

    ArrayList<ComposeConfigUnit> getPrepareStopShells() {
        return prepareStopShells
    }

    ArrayList<ComposeConfigUnit> getPostDownShells() {
        return postDownShells
    }

    ArrayList<ComposeConfigUnit> getTransferConfigurationFiles() {
        return transferConfigurationFiles
    }

    ArrayList<ComposeConfigUnit> getPrepareServiceLaunchShells() {
        return prepareServiceLaunchShells
    }

    ArrayList<ComposeConfigUnit> getServiceTestingShells() {
        return serviceTestingShells
    }

    private String initProjectCode() {
        if (
                config.PROJECT_CODE == null || config.PROJECT_CODE.isEmpty()
        ) {
            throw new Exception("Can not get project code")
        }
        this.projectCode = config.PROJECT_CODE
    }

    private void initBranchName() {
        this.branchName = JOB_BASE_NAME
    }

    private String initDeployOwner() {
        String owner = null
        node('master') {
            withCredentials(config.OPADMIN_CREDENTIAL) {
                owner = getEnvProperty(config.OPADMIN_USER_NAME)
            }
            if (owner == null || owner.isEmpty()) {
                throw new Exception("Can not get deploy owner")
            }
        }
        this.deployOwner = owner
    }

    private void initEnvironmentSetupShells() {
        this.environmentSetupShells =
                initStepConfigUnits(Lib.ENVIRONMENT_SETUP_SHELLS)
    }

    private void initPrepareStopShells() {
        this.prepareStopShells =
                initStepConfigUnits(Lib.PREPARE_STOP_SHELLS)
    }

    private void initPostDownShells() {
        this.postDownShells =
                initStepConfigUnits(Lib.POST_DOWN_SHELLS)
    }

    private void initTransferConfigurationFiles() {
        this.transferConfigurationFiles =
                initStepConfigUnits(Lib.TRANSFER_CONFIGURATION_FILES)
    }

    private void initPrepareServiceLaunchShells() {
        this.prepareServiceLaunchShells =
                initStepConfigUnits(Lib.PREPARE_SERVICE_LAUNCH_SHELLS)
    }

    private void initServiceTestingShells() {
        this.serviceTestingShells =
                initStepConfigUnits(Lib.SERVICE_TESTING_SHELLS)
    }

    private ArrayList<ComposeConfigUnit> initStepConfigUnits(String stepName) {
        def stepShells = moduleArgs[stepName] as ArrayList<Map>
        if (stepShells == null) {
            return []
        }
        ArrayList<ComposeConfigUnit> result = []
        for (Map stepShell : stepShells) {
            def file = stepShell['file'] as String
            if (file == null || file.isEmpty()) {
                continue
            }
            def tags = stepShell['tags'] as ArrayList<String>
            if (tags == null) {
                continue
            }
            def isSudo = stepShell['isSudo'] as Boolean
            isSudo = isSudo == null ? false : isSudo
            result << new ComposeConfigUnit(file, tags, isSudo)
        }
        return result
    }
}
