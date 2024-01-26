package pipeline.artifact.cd

import pipeline.common.ssh.EOFCommand

class ShellScriptSplicer {

    static ArrayList<String> getBashHeader() {
        return ['#!/bin/bash', 'set -x']
    }

    static ArrayList<String> CD(String dirPath) {
        return ["cd ${dirPath}"]
    }

    static ArrayList<String> mkdirWithOwner(String mkdirDirPath, String owner) {
        return [
                "if [ ! -d \"${mkdirDirPath}\" ]; then",
                "  mkdir -p ${mkdirDirPath}",
                "  chown ${owner}:${owner} ${mkdirDirPath}",
                'fi'
        ]
    }

    static EOFCommand generateEOFBash(ArrayList<ArrayList<String>> codesList, boolean isSudo = false) {
        codesList.add(0, getBashHeader())
        return generateEOFCommand(codesList, isSudo)
    }

    static EOFCommand generateEOFCommand(ArrayList<ArrayList<String>> codesList, boolean isSudo = false) {
        return new EOFCommand(concatenateScriptSegments(codesList), isSudo)
    }

    static List<String> concatenateScriptSegments(ArrayList<ArrayList<String>> codesList) {
        List<String> codes = []
        codesList.each { List<String> list ->
            codes.addAll(list)
        }
        return codes
    }
}
