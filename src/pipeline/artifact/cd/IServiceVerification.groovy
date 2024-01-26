package pipeline.artifact.cd

/**
 * 此階段負責部署後的驗證工作。
 */
interface IServiceVerification {
    /**
     * 功能：進行服務測試。
     * 作用：驗證新部署的服務是否按預期運行，確保部署的質量。
     */
    void serviceTesting()
}
