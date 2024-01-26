package pipeline.artifact.cd

import com.cloudbees.groovy.cps.NonCPS

class PuzzleCombiner<T> {
    List<List<String>> tags

    PuzzleCombiner(List<List<String>> tags) {
        this.tags = tags
    }

    /**
     * 計算基於標籤組合的拼圖組合。
     * @param tagToPuzzles 標籤到拼圖列表的映射。
     * @return 組合後的拼圖列表。
     */
    List<List<T>> computeCombinations(Map<String, List<T>> tagToPuzzles) {
        List<List<T>> combinations = []
        Map<String, List<T>> processedTagToPuzzles = tagToPuzzles
                .collectEntries { String tag, List<T> puzzles ->
                    [(tag.trim()): puzzles]
                }
        tags.each { List<String> tagGroup ->
            Set<T> combinedPuzzles = tagGroup
                    .unique()
                    .collectMany { String tag ->
                        processedTagToPuzzles.getOrDefault(tag.trim(), [])
                    }
            combinations.add(sortPuzzles(combinedPuzzles) as List<T>)
        }
        return combinations
    }

    /**
     * 對給定的拼圖集合進行排序。
     * @param puzzles 拼圖集合。
     * @return 排序後的拼圖列表。
     */
    @NonCPS
    static List sortPuzzles(Set puzzles) {
        return new ArrayList(puzzles).sort { a, b ->
            a.toString() <=> b.toString()
        }
    }
}
