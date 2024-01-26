package pipeline.artifact.cd

/**
 * 此階段負責實際啟動和部署服務。
 */
interface IServiceLaunch {

    /**
     * 功能：傳輸配置文件。
     * 作用：確保所有必要的配置文件都被傳輸到正確的位置，以便 Docker Compose 可以正確地啟動服務。
     */
    void transferConfiguration()

    /**
     * 功能：準備啟動服務前的操作。
     * 作用：在啟動服務之前執行必要的預處理步驟，例如數據庫 schema 同步、配置檢查等，以確保服務可以順利啟動。
     */
    void prepareServiceLaunch()

    /**
     * 功能：啟動服務。
     * 作用：使用 Docker Compose 啟動定義在配置文件中的服務。
     */
    void startService()
}
