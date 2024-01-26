package pipeline.artifact.ci

import pipeline.Pipeline
import org.jenkinsci.plugins.workflow.cps.nodes.StepStartNode
import org.jenkinsci.plugins.workflow.graph.FlowNode

class GetStepID extends Pipeline {

    /**
     * 相對舊的版本
     1. 這版本還是用 parser URL來取得ID的方式
     2. 此方法一樣是由數字大到數字小, 找到第一個Allocate node : Start停止;
     所以使用此方法時, 有特殊的地方要考慮, 就是看情形搭配使用node, 要注意
     */
    String getNodeWsUrl(def flowNode = null) {
        if (!flowNode) {
            flowNode = getContext(FlowNode)
        }

        //找flowNode.typeFunctionName == 'node' 亦可
        if (flowNode instanceof StepStartNode && flowNode.getDisplayName() == 'Allocate node : Start') {
            def nodeAllocateURL = flowNode.url
            echo("[GetStepID]node allocate - URL: ${nodeAllocateURL}")

            //格式如同上面範例
            int indexDigitEnd = nodeAllocateURL.length() - 1
            String tempSubstring = nodeAllocateURL.substring(0, indexDigitEnd)
            int indexDigitStart = tempSubstring.lastIndexOf('/')
            String resultIDString = nodeAllocateURL.substring(indexDigitStart + 1, indexDigitEnd)
            echo("resultIDstr - ${resultIDString}")

            return resultIDString
        }

        echo("##### fn2 is ${flowNode}")

        return flowNode.parents.findResult { getNodeWsUrl(it) }
    }

    /**
     * 20221100: 本來小YI的方法(抓url分析text)在舊版Jenkins 2.249.3可行
     但是改用Jenkins 2.361.2後, 關鍵字'Allocate node : Start' 在文檔中改為 "node: -";
     而使用此api的flowNode.getDisplayName(); 仍可正常解析'Allocate node : Start';
     所以預計改用此法

     而根據目前專案使用流程, 都是尋找正向的第n個'Allocate node : Start'; (nodeSerial = n )
     配合此法; 所以用反向找到起始(2)之後,回傳最小的數值 [第0個]
     方法跟配合法也是挺死板的, 將來在考慮怎麼調整

     另外, 已找到api可以直接取用id, 得以簡化流程

     追加20221109, 原本的方法是用recursive來處理return; 但有出現爆掉的情形; 目前修正成for-loop處理

     20230116: 修改方式可以取用魔術數字的node (最早小YI版本有支援, 後來重構時漏了, 現在補上)
     */
    static String getReverseNodeWsUrl(nodeSerial) {
        //由於內部改成for-loop; 不再使用null來判定拿取flow node; 這邊直接取一次即可
        def flowNode = getContext(FlowNode)

        String nowId = flowNode.getId()
        echo("startID - ${nowId}")

        ArrayList tempList = [2] //起始的數值

        int nowIDint = Integer.parseInt(nowId)
        for (int ix = nowIDint; ix > 2; ix--) {
            //留這是為了debug, 放到if裡比較省事
            //seqID = flowNode.getId()
            //echo "strID in loop - ${seqID}"
            //找flowNode.typeFunctionName == 'node' 亦可
            if (flowNode instanceof StepStartNode && flowNode.getDisplayName() == 'Allocate node : Start') {
                String seqID = flowNode.getId()
                echo("matchIDstr - ${seqID}")
                tempList.add(Integer.parseInt(seqID))
                //rid = seqID       
            }

            // 這裡是這次的重點; 目前只找出getParents()這樣的用法; 且要用(只跑一次)的for, 才能取得上一層
            // 還是覺得應該有更好的方法, 待研究
            List<FlowNode> parentsFN = flowNode.getParents()
            if (parentsFN != null && parentsFN.size() > 0) {
                for (FlowNode ifn : parentsFN) {
                    //echo "##### now fn is ${ifn}"
                    flowNode = ifn
                }
            }
        }

        def intNodeSerial = nodeSerial as int
        def nodeListSize = tempList.size()
        if (nodeListSize <= intNodeSerial) {
            throw new Exception("The resultIDstrList size ${nodeListSize} less than nodeSerial ${nodeSerial} !!")
        }

        def matchIDstrList = tempList.sort()
        echo("resultIDstrList - ${matchIDstrList}")

        def rid = matchIDstrList[intNodeSerial]
        echo("resultIDstr - ${rid}")
        return "${rid}"
    }
}
