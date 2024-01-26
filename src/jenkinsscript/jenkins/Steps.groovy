//file:noinspection unused
package jenkinsscript.jenkins

import jenkinsscript.resource.ScriptMode
import hudson.model.ParameterDefinition

abstract class Steps extends Base {

    /**
     * 打印帶有PIPELINE標籤與呼叫此消息來源的類與方法的消息
     * @param message 要寫入控制台輸出的消息。
     */
    static void EchoStep(String message) {
        EchoStep(defaultStackTraceMaxCount, message)
    }

    /**
     * 打印帶有PIPELINE標籤與呼叫此消息來源的類與方法的消息 (可指定堆棧深度)
     * @param stackTraceMaxCount 堆棧最大深度。
     * @param message 要寫入控制台輸出的消息。
     */
    static void EchoStep(int stackTraceMaxCount, String message) {
        echo(getStepStage(stackTraceMaxCount, message))
    }

    /**
     * 從版本控制中籤出
     * @param args
     */
    static void checkout(LinkedHashMap args) {
        if (mode == ScriptMode.jenkins) {
            script.checkout(args)
            return
        }
        callMethod('checkout', [args])
    }

    /**
     * 調用 ansible 劇本
     * @param args [playbook: String, ...(optional)]
     * @url https://www.jenkins.io/doc/pipeline/steps/ansible/#ansibleplaybook-invoke-an-ansible-playbook
     */
    static void ansiblePlaybook(LinkedHashMap args) {
        if (mode == ScriptMode.jenkins) {
            script.ansiblePlaybook(args)
            return
        }
        callMethod('ansiblePlaybook', [args])
    }

    /**
     * 彩色 ANSI 控制台輸出 向主控台輸出添加對標準 ANSI 轉義序列（包括顏色）的支援。
     * @param color 顏色名稱。
     */
    static void ansiColor(String color, Closure closure) {
        if (mode == ScriptMode.jenkins) {
            script.ansiColor(color, closure)
            return
        }
        callMethod('ansiColor', [color, closure])
    }

    /**
     * 將憑據綁定到變數
     * @param args
     * @param closure
     * @return
     */
    static def withCredentials(ArrayList args, Closure closure) {
        if (mode == ScriptMode.jenkins) {
            return script.withCredentials(args, closure)
        }
        return callMethod('withCredentials', [args, closure])
    }

    /**
     * 將一個變數設置為使用者名，將一個變數設置為憑據中提供的密碼。
     * @param args [usernameVariable: String, passwordVariable: String, credentialsId: String]
     */
    static def usernamePassword(LinkedHashMap args) {
        if (mode == ScriptMode.jenkins) {
            return script.usernamePassword(args)
        }
        return callMethod('usernamePassword', [args])
    }

    /**
     * 發佈上次更改 通過使用 Diff2HTML 的修訂版本之間的豐富 VCS 差異顯示內部版本上次更改。
     */
    static void lastChanges() {
        if (mode == ScriptMode.jenkins) {
            script.lastChanges()
            return
        }
        callMethod('lastChanges', [])
    }

    /**
     * 階段 創建帶標籤的塊。
     * @param name 階段名稱。
     * @param closure 要在階段中執行的內容。
     */
    static void stage(String name, Closure closure) {
        if (mode == ScriptMode.jenkins) {
            script.stage(name, closure)
            return
        }
        callMethod('stage', [name, closure])
    }

    /**
     * 打印消息
     * @param message 要寫入控制台輸出的消息。
     */
    static void echo(String message) {
        if (mode == ScriptMode.jenkins) {
            script.echo(message)
            return
        }
        callMethod('echo', message)
    }

    /**
     * 發出錯誤信號。 如果想有條件地中止程序的某些部分，這很有用。 也可以只拋出 new Exception()，但這一步將避免列印堆疊追蹤。
     * @param message 捕獲錯誤時將記錄到控制台的訊息。
     */
    static void error(String message) {
        if (mode == ScriptMode.jenkins) {
            script.error(message)
            return
        }
        callMethod('error', message)
    }

    /**
     * 驗證工作區中是否存在檔 檢查當前節點上是否存在給定檔。返回。此步驟必須在上下文中運行：使用聲明性語法，它必須在具有已定義代理的階段中運行（例如，與「代理無」不同）：true | falsenode
     * @param path 要檢查的檔路徑。
     * @return
     */
    static boolean fileExists(String path) {
        if (mode == ScriptMode.jenkins) {
            return script.fileExists(path)
        }
        return callMethod('fileExists', [path])
    }

    /**
     * 如果封閉節點在類別 Unix 系統（例如 Linux 或 Mac OS X）上執行，則傳回 true，如果在 Windows 上則傳回 false。
     * @return
     */
    static boolean isUnix() {
        if (mode == ScriptMode.jenkins) {
            return script.isUnix()
        }
        return callMethod('isUnix', [])
    }

    /**
     * 從工作區讀取檔 從相對路徑（根在當前目錄，通常是工作區）讀取檔，並將其內容作為純字串返回。
     * @param path 要讀取的工作區中文件的相對（分隔）路徑。
     * @return
     */
    static String readFile(String file) {
        if (mode == ScriptMode.jenkins) {
            return script.readFile(file)
        }
        return callMethod('readFile', [file])
    }

    /**
     * 從工作區讀取檔 從相對路徑（根在當前目錄，通常是工作區）讀取檔，並將其內容作為純字串返回。
     * @param args [file: String, encoding: String(optional)]
     * @return 檔的內容。
     * file 要讀取的工作區中文件的相對（分隔）路徑。
     * encoding 要使用的編碼。如果未指定，則使用運行步驟的節點的系統預設編碼。
     */
    static String readFile(LinkedHashMap args) {
        if (mode == ScriptMode.jenkins) {
            return script.readFile(args)
        }
        return callMethod('readFile', [args])
    }

    /**
     * 強制實施時間限制 以確定的超時限制執行塊內的代碼。如果達到時間限制，則會拋出異常（org.jenkinsci.plugins.workflow.steps.FlowInterruptedException），這會導致構建中止（除非以某種方式捕獲和處理它）。
     * @param args [time: int, activity : String(optional), unit: TimeUnit(optional)]
     * @param closure 要在時間戳中執行的內容。
     * time 此步驟在取消嵌套塊之前等待的時間長度。
     * activity 此塊的日誌中沒有活動後超時，而不是絕對持續時間。默認為 false。
     * unit 時間參數的單位。如果未指定，則預設為「分鐘」。
     */
    static void timeout(Map args, Closure closure) {
        if (mode == ScriptMode.jenkins) {
            script.timeout(args, closure)
            return
        }
        callMethod('timeout', [args, closure])
    }

    /**
     * 將工具安裝綁定到變數（返回工具主目錄）。此處僅提供已配置的工具。如果原始工具安裝程式具有自動預配功能，則將根據需要安裝該工具。
     * @param name 工具的名稱。必須在 Jenkins 中的「管理 Jenkins →全域工具配置」 下預先設定工具名稱。
     * @return
     */
    static def tool(String name) {
        if (mode == ScriptMode.jenkins) {
            return script.tool(name)
        }
        return callMethod('tool', [name])
    }

    /**
     * Get contextual object from internal APIs
     * @param clazz
     * @return
     */
    static def getContext(Class clazz) {
        if (mode == ScriptMode.jenkins) {
            return script.getContext(clazz)
        }
        return callMethod('getContext', [clazz])
    }


    /**
     * Windows Batch Script
     * @param args [script: String, encoding: String(optional), label: String(optional), returnStatus: boolean(optional), returnStdout: boolean(optional)]
     * @script 執行批處理腳本。允許多行。使用該標誌時，您可能希望以 為前綴，以免命令本身包含在輸出中。returnStdout@
     * @encoding 過程輸出的編碼。在的情況下，適用於此步驟的返回值;否則，或始終為標準錯誤，控制如何將文本複製到生成日誌。如果未指定，則使用運行步驟的節點的系統預設編碼。如果期望進程輸出可能包含非 ASCII 字元，則最好顯式指定編碼。例如，如果您特定地知道給定進程將產生UTF-8，但將在具有不同系統編碼的節點上運行（通常是Windows，因為每個Linux發行版長期以來都預設為UTF-8），則可以通過指定以下內容來確保正確的輸出：return Stdoutencoding: 'UTF-8'
     * @label 要在管道步驟視圖中顯示的標籤以及步驟的藍海詳細資訊，而不是步驟類型。因此，該視圖更有意義且特定於領域，而不是技術性。
     * @returnStatus 通常，以非零狀態代碼退出的腳本將導致步驟失敗並出現異常。如果選取此選項，則步驟的返回值將是狀態代碼。例如，您可以將其與零進行比較。
     * @returnStdout 如果選取中，任務的標準輸出將作為步驟值作為 返回，而不是列印到生成日誌中。（標準錯誤（如果有）仍將列印到日誌中。您經常希望調用結果來去除尾隨換行符。String.trim()
     */
    static def bat(LinkedHashMap args) {
        if (mode == ScriptMode.jenkins) {
            return script.bat(args)
        }
        return callMethod('bat', [args])
    }

    /**
     * Windows Batch Script
     * @param command 要在Windows上執行的命令。
     */
    static void bat(String command) {
        if (mode == ScriptMode.jenkins) {
            script.bat(command)
            return
        }
        callMethod('bat', [command])
    }

    /**
     * 更改當前目錄更改目前的目錄。塊內的任何步驟都將使用此目錄作為當前目錄，任何相對路徑都將使用它作為基本路徑。
     * @param path 工作區中要用作新工作目錄的目錄的相對路徑。
     * @param closure 要在新目錄中執行的內容。
     * @return
     */
    static def dir(String path, Closure closure) {
        if (mode == ScriptMode.jenkins) {
            return script.dir(path, closure)
        }
        return callMethod('dir', [path, closure])
    }

    /**
     * 分配節點 在節點（通常是生成代理）上分配執行程式，並在該代理上的工作區上下文中運行更多代碼。
     * @param nodeName 計算機名稱、標籤名稱或任何其他標籤表達式，用於限制此步驟的生成位置。可以留空，在這種情況下，將採用任何可用的執行程式。
     * @param closure 要在節點上執行的內容。
     */
    static void node(Object nodeName, Closure closure) {
        if (mode == ScriptMode.jenkins) {
            script.node(nodeName, closure)
            return
        }
        callMethod('node', [nodeName, closure])
    }

    /**
     * Shell Script
     * @param command 要在Unix上執行的命令。
     */
    static def sh(String command) {
        if (mode == ScriptMode.jenkins) {
            return script.sh(command)
        }
        callMethod('sh', [command])
    }

    /**
     * Shell Script
     * @param args [script: String, encoding: String(optional), label: String(optional), returnStatus: boolean(optional), returnStdout: boolean(optional)]
     * script 運行 Bourne shell 腳本，通常在 Unix 節點上。接受多行。
     * encoding 過程輸出的編碼。
     * label 要在管道步驟視圖中顯示的標籤以及步驟的藍海詳細資訊，而不是步驟類型。
     * returnStatus 通常，以非零狀態代碼退出的腳本將導致步驟失敗並出現異常。如果選取此選項，則步驟的返回值將是狀態代碼。例如，您可以將其與零進行比較。
     * returnStdout 如果選取中，任務的標準輸出將作為步驟值作為 返回，而不是列印到生成日誌中。（標準錯誤（如果有）仍將列印到日誌中。您經常希望調用結果來去除尾隨換行符。String.trim()
     */
    static def sh(LinkedHashMap args) {
        if (mode == ScriptMode.jenkins) {
            return script.sh(args)
        }
        callMethod('sh', [args])
    }


    /**
     * 時間戳 在控制台輸出中添加時間戳。
     * @param closure 要在時間戳中執行的內容。
     */
    static void timestamps(Closure closure) {
        if (mode == ScriptMode.jenkins) {
            script.timestamps(closure)
            return
        }
        callMethod('timestamps', [closure])
    }

    /**
     * SSH Agent 可以在陣列中傳遞多個憑據，但不支援使用代碼段生成器。
     * @param args [credentials: Array / List of String, ignoreMissing: boolean(optional)]
     * @param closure
     * @return
     */
    static def sshagent(def args, Closure closure) {
        if (mode == ScriptMode.jenkins) {
            return script.sshagent(args, closure)
        }
        return callMethod('sshagent', [args, closure])
    }

    /**
     * 將給定內容寫入目前的目錄中的命名檔。
     * @param args [file: String, text: String, encoding: String(optional)]
     * file: 工作區中文件的相對路徑。
     * text: 要寫入檔的內容。
     * encoding: 要使用的編碼。如果未指定，則使用運行步驟的節點的系統預設編碼。
     */
    static def writeFile(LinkedHashMap args) {
        if (mode == ScriptMode.jenkins) {
            return script.writeFile(args)
        }
        return callMethod('writeFile', [args])
    }


    /**
     * 禁止併發執行管道。 可用於防止同時訪問共享資源等。
     * 例如：在管道的已執行生成時對生成進行排隊，或者中止正在運行的生成並啟動新生成。
     * options { disableConcurrentBuilds() }options { disableConcurrentBuilds(abortPrevious: true) }
     */
    static def disableConcurrentBuilds() {
        if (mode == ScriptMode.jenkins) {
            return script.disableConcurrentBuilds()

        }
        return callMethod('disableConcurrentBuilds', [])
    }

    /**
     * 構建丟棄器 定義構建丟棄策略。 例如：options { buildDiscarder(logRotator(numToKeepStr: '10')) }
     * @param args
     * @return
     */
    static def buildDiscarder(Object args) {
        if (mode == ScriptMode.jenkins) {
            return script.buildDiscarder(args)
        }
        return callMethod('buildDiscarder', [args])
    }

    /**
     * 日誌旋轉器 定義日誌旋轉策略。 例如：options { logRotator(numToKeepStr: '10') }
     * @param args
     * @return
     */
    static def logRotator(LinkedHashMap args) {
        if (mode == ScriptMode.jenkins) {
            return script.logRotator(args)
        }
        return callMethod('logRotator', [args])
    }

    /**
     * 默認情況下，在指令中跳過從原始程式碼管理中籤出代碼。 例如：agentoptions { skipDefaultCheckout() }
     */
    static def skipDefaultCheckout() {
        if (mode == ScriptMode.jenkins) {
            return script.skipDefaultCheckout()
        }
        return callMethod('skipDefaultCheckout', [])
    }


    /**
     * 環境 變更環境變數。 例如：environment { FOO = 'bar' }
     * @param closure
     * @return
     */
    static def environment(Closure closure) {
        if (mode == ScriptMode.jenkins) {
            return script.environment(closure)
        }
        return callMethod('environment', [closure])
    }
    /**
     * 該指令提供使用者在觸發管道時應提供的參數清單。
     * @param args 參數清單。
     * @url https://www.jenkins.io/doc/book/pipeline/syntax/#agent-parameters
     */
    static def parameters(List<ParameterDefinition> args) {
        if (mode == ScriptMode.jenkins) {
            return script.parameters(args)
        }
        return callMethod('parameters', [args])
    }

    static def string(LinkedHashMap args) {
        if (mode == ScriptMode.jenkins) {
            return script.string(args)
        }
        return callMethod('string', [args])
    }

    static def booleanParam(LinkedHashMap args) {
        if (mode == ScriptMode.jenkins) {
            return script.booleanParam(args)
        }
        return callMethod('booleanParam', [args])
    }

    /**
     * 選擇參數，例如：parameters { choice(name: 'CHOICES', choices: ['one', 'two', 'three'], description: '') }
     * @param args 參數。
     * @return
     */
    static def choice(LinkedHashMap args) {
        if (mode == ScriptMode.jenkins) {
            return script.choice(args)
        }
        return callMethod('choice', [args])
    }

    /**
     * 記錄編譯器警告和靜態分析結果
     * @param args
     * @url https://www.jenkins.io/doc/pipeline/steps/warnings-ng/#recordissues-record-compiler-warnings-and-static-analysis-results
     */
    static def recordIssues(LinkedHashMap args) {
        if (mode == ScriptMode.jenkins) {
            return script.recordIssues(args)
        }
        return callMethod('recordIssues', [args])
    }

    /**
     * @param triggers
     * @url https://www.jenkins.io/doc/pipeline/steps/params/pipelinetriggers/
     */
    static def pipelineTriggers(List triggers) {
        if (mode == ScriptMode.jenkins) {
            return script.pipelineTriggers(triggers)
        }

        return callMethod('pipelineTriggers', [triggers])
    }

    /**
     * 配置 Jenkins 以輪詢 SCM 中的更改。
     * @param args [scmpoll_spec: String, ignorePostCommitHooks: boolean(optional)]
     * scmpoll_spec 要使用的 SCM 輪詢規格。如果未指定，則使用項目的全局 SCM 輪詢規格。
     * ignorePostCommitHooks 如果為 true，則忽略 SCM 的後提交鉤子。如果為 false，則在 SCM 的後提交鉤子中運行輪詢程序。默認為 false。
     */
    static def pollSCM(LinkedHashMap args) {
        if (mode == ScriptMode.jenkins) {
            return script.pollSCM(args)
        }
        return callMethod('pollSCM', [args])
    }

    /**
     * 提供類似cron的功能來定期執行此專案。 此功能主要用於使用 Jenkins 作為 cron 替代品，對於持續構建軟體專案並不理想。
     * @param spec
     */
    static def cron(String spec) {
        if (mode == ScriptMode.jenkins) {
            return script.cron(spec)
        }
        return callMethod('cron', [spec])
    }

    /**
     * 配置 Jenkins 以輪詢 SCM 中的更改。
     * @param scmpoll_spec 要使用的 SCM 輪詢規格。如果未指定，則使用項目的全局 SCM 輪詢規格。
     */
    static def pollSCM(String scmpoll_spec) {
        if (mode == ScriptMode.jenkins) {
            return script.pollSCM(scmpoll_spec)
        }
        return callMethod('pollSCM', [scmpoll_spec])
    }

    /**
     * @param args [parserId: String, id: String(optional), name: String(optional), pattern: String(optional), reportEncoding: String(optional), skipSymbolicLinks: boolean(optional)]
     */
    static def groovyScript(LinkedHashMap args) {
        if (mode == ScriptMode.jenkins) {
            return script.groovyScript(args)
        }
        return callMethod('groovyScript', [args])
    }
}
