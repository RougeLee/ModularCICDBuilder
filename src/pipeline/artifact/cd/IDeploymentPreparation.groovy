package pipeline.artifact.cd

/**
 * 此階段處理部署前的準備工作。
 */
interface IDeploymentPreparation {

    /**
     * 功能：準備停止服務前的操作。
     * 作用：在停止服務之前執行必要的預處理步驟，如數據庫刷新、狀態保存等，以確保服務可以安全地停止。
     */
    void prepareStopActions()

    /**
     * 功能：停止現有服務。
     * 作用：確保在開始新的部署前，任何正在運行的服務都被安全地停止，以避免衝突。
     */
    void stopExistingService()

    /**
     * 功能：執行停止服務後的操作。
     * 作用：在服務停止後執行必要的清理或設置步驟，為新的部署做準備。
     */
    void postDownActions()
}
