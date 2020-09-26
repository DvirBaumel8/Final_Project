package ProjectScanning;

public class InnerClassDetails {
    private String path;
    private String packageName;

    public InnerClassDetails(String path, String packageName) {
        this.path = path;
        this.packageName = packageName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
