package pipeline.artifact.docker.compose

import java.security.MessageDigest

class DockerComposeProjectNameMaker {
    private static final String ALGORITHM_TYPE = 'MD5'
    private static final String CHARSET_NAME_TYPE = 'UTF-8'
    private static final String REPLACE_REGEX = /[^a-zA-Z0-9]/
    private static final String REPLACE_REPLACEMENT = ''
    private static int shortHashLength = 16
    private static int branchNameLength = 4

    static String generateProjectName(
            String projectName,
            String branchName
    ) {
        if (projectName == null || branchName == null) {
            String errorMessage = new StringBuilder()
                    .append("projectName: ${projectName}, ")
                    .append(" and branchName: ${branchName}, ")
                    .append(" must not be null.")
                    .toString()
            throw new IllegalArgumentException(errorMessage)
        }
        String matchComposeProjectNameRule = projectName
                .replaceAll(REPLACE_REGEX, REPLACE_REPLACEMENT)
                .toLowerCase()
        String hashBranchName = gitBranchToDockerProjectName(branchName)
        return "${matchComposeProjectNameRule}-${hashBranchName}"
    }

    private static String gitBranchToDockerProjectName(String branchName) {
        String shortHash = gitBranchToHashedDir(branchName)
                .substring(0, shortHashLength)
                .toLowerCase()
        String branchAbbreviation = branchName
                .tokenize('/')
                .last()
                .replaceAll(REPLACE_REGEX, REPLACE_REPLACEMENT)
                .take(branchNameLength)
                .toLowerCase()
        return "${branchAbbreviation}-${shortHash}"
    }

    private static String gitBranchToHashedDir(String branchName) {
        MessageDigest digest = MessageDigest.getInstance(ALGORITHM_TYPE)
        byte[] hashBytes = digest.digest(branchName.getBytes(CHARSET_NAME_TYPE))
        StringBuilder sb = new StringBuilder()
        hashBytes.each { byte b ->
            sb.append(String.format('%02x', b))
        }
        return sb.toString().replaceAll(REPLACE_REGEX, REPLACE_REPLACEMENT)
    }
}
