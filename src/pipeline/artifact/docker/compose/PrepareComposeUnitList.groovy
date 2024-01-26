package pipeline.artifact.docker.compose

class PrepareComposeUnitList {
    private ArrayList<LinkedHashMap> dockerComposeServices

    PrepareComposeUnitList(ArrayList<LinkedHashMap> dockerComposeServices) {
        this.dockerComposeServices = dockerComposeServices
    }

    ArrayList<ComposeUnit> getComposeUnitList() {
        ArrayList<ComposeUnit> composeUnits = []
        dockerComposeServices.each { LinkedHashMap composeUnitMap ->
            composeUnits << new ComposeUnit(composeUnitMap)
        }
        return composeUnits
    }
}
