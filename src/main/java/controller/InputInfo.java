package controller;

public class InputInfo {

    private String localDirectory;
    private String gitUrl;
    private String commitNo;

    public InputInfo() {
    }

    public InputInfo(String localDirectory, String gitUrl, String commitNo) {
        this.localDirectory = localDirectory;
        this.gitUrl = gitUrl;
        this.commitNo = commitNo;
    }

    public String getLocalDirectory() {
        return localDirectory;
    }

    public void setLocalDirectory(String localDirectory) {
        this.localDirectory = localDirectory;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public String getCommitNo() {
        return commitNo;
    }

    public void setCommitNo(String commitNo) {
        this.commitNo = commitNo;
    }
}
