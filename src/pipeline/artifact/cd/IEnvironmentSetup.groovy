package pipeline.artifact.cd

/**
 * 此階段負責部署環境的初步設置和配置。
 */
interface IEnvironmentSetup {
    /**
     * 功能：檢查基礎目錄是否存在。
     * 作用：確保部署的基礎環境已經設置，例如 /var/docker 目錄是否已創建。
     */
    void checkBaseDirectory()

    /**
     * 功能：檢查專案目錄是否存在。
     * 作用：確保針對特定專案的目錄結構已經準備好，用於存放專案相關的文件和配置。
     */
    void checkProjectDirectory()

    /**
     * 功能：進行環境配置。
     * 作用：根據部署環境的不同，設置必要的環境參數，如網絡配置、環境變量等，進行相應的配置、安裝相關軟件等。
     */
    void environmentConfiguration()
}
