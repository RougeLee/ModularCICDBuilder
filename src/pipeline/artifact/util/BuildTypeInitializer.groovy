package pipeline.artifact.util

import pipeline.common.constants.ESCM
import pipeline.Pipeline
import pipeline.common.constants.BuildCauseType
import pipeline.common.constants.BuildTypeLabel
import pipeline.common.ui.panels.GitREVersion
import pipeline.common.util.Config

class BuildTypeInitializer extends Pipeline implements IInitializeProcessData {
    private static final String USER_ID_CAUSE = 'UserIdCause'
    private static final String SCM_GIT = ESCM.GIT.name()
    private Config config

    BuildTypeInitializer(Config config) {
        this.config = config
    }

    void initialize() {
        createDoBuildUIParam()
        initializeBuildCause()
        skipDefaultCheckout()
        setupBuildNode()
        configureBuildBasedOnType()
        setupGitVersionParametersForUI()
    }

    private createDoBuildUIParam() {
        config.DO_BUILD_HANDLER.main(config)
    }

    private void initializeBuildCause() {
        if (isManualBuildTriggered()) {
            EchoStep('Manual establishment.')
            config.BUILD_CAUSE = BuildCauseType.MANUAL
        } else {
            EchoStep('automatic trigger.')
            config.BUILD_CAUSE = BuildCauseType.TRIGGER
        }
    }

    private boolean isManualBuildTriggered() {
        try {
            def causes = currentBuild.rawBuild.getCauses()
            if (!causes.isEmpty() &&
                    causes.get(0)
                            .toString()
                            .contains(USER_ID_CAUSE)
            ) {
                return true
            }
        } catch (Exception e) {
            EchoStep("Error determining build trigger: ${e}")
        }
        return false
    }

    private void setupBuildNode() {
        new BuildNodeScript(config).run()
    }

    private void configureBuildBasedOnType() {
        if (config.JOB_STATUS == 'Pause') {
            return
        }
        switch (config.BUILD_TYPE) {
            case BuildTypeLabel.CONTINUOUS:
                configureForContinuousBuild()
                break
            case BuildTypeLabel.DAILY:
                configureForDailyBuild()
                break
        }
    }

    private void configureForContinuousBuild() {
        addTriggerForSCM()
    }

    private void addTriggerForSCM() {
        if (config.SCM == SCM_GIT) {
            config.CUSTOM_PROPERTIES.add(pipelineTriggers([pollSCM('')]))
        }
    }

    private void configureForDailyBuild() {
        EchoStep("config.CronExpression = ${config.CRON_EXPRESSION} set")
        config.CUSTOM_PROPERTIES.add(pipelineTriggers([cron(config.CRON_EXPRESSION as String)]))
    }

    private void setupGitVersionParametersForUI() {
        if (config.SCM != SCM_GIT) {
            return
        }
        GitREVersion useGetGitVersion = new GitREVersion(config)
        useGetGitVersion.initUIParam()
    }
}
