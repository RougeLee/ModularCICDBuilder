package pipeline.artifact.docker.compose

import pipeline.Pipeline
import pipeline.artifact.cd.PuzzleCombiner

class GenerateUnitsList extends Pipeline {
    private ArrayList<ArrayList<String>> deployTagsList
    private PuzzleCombiner puzzleCombiner

    GenerateUnitsList(ArrayList<ArrayList<String>> deployTagsList) {
        this.deployTagsList = deployTagsList
        this.puzzleCombiner = new PuzzleCombiner(deployTagsList)
    }

    Map<ArrayList<String>, ArrayList<ComposeConfigUnit>> getList(
            ArrayList<ComposeConfigUnit> configUnits
    ) {
        return getUnitsList(getConfigUnitPuzzles(configUnits))
    }

    private static Map<String, ArrayList<ComposeConfigUnit>> getConfigUnitPuzzles(
            ArrayList<ComposeConfigUnit> configUnits
    ) {
        Map<String, ArrayList<ComposeConfigUnit>> puzzles = [:]
        configUnits.each { ComposeConfigUnit configUnit ->
            configUnit.tags.each { String tag ->
                ArrayList<ComposeConfigUnit> units = puzzles.getOrDefault(tag, [])
                puzzles[tag] = units
                units << configUnit
            }
        }
        return puzzles
    }

    private Map<ArrayList<String>, ArrayList<ComposeConfigUnit>> getUnitsList(
            Map<String, ArrayList<ComposeConfigUnit>> configUnitPuzzles
    ) {
        ArrayList<ArrayList<ComposeConfigUnit>> combinationsList
        combinationsList = getCombinationsList(configUnitPuzzles)
        Map<ArrayList<String>, ArrayList<ComposeConfigUnit>> result = [:]
        for (int i = 0; i < deployTagsList.size(); i++) {
            result[deployTagsList[i]] = combinationsList[i]
        }
        return result
    }

    private ArrayList<ArrayList<ComposeConfigUnit>> getCombinationsList(
            Map<String, ArrayList<ComposeConfigUnit>> configUnitPuzzles
    ) {
        ArrayList<ArrayList<ComposeConfigUnit>> combinationsList
        combinationsList = puzzleCombiner.computeCombinations(configUnitPuzzles)
        if (combinationsList.size() != deployTagsList.size()) {
            throw new Exception('combinations.size() != deployTagsList.size()')
        }
        return combinationsList
    }
}
