package hello;

public class InputInfo {

    private String versionCode;
    private String versionName;
    private String commitNo;

    public InputInfo() {
    }

    public InputInfo(String versionCode, String versionName, String commitNo) {
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.commitNo = commitNo;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getCommitNo() {
        return commitNo;
    }

    public void setCommitNo(String commitNo) {
        this.commitNo = commitNo;
    }
}
