package ProjectContainers;

import java.io.File;

public class SourceProject {
    private String projectName;
    private String projectPath;
    private File[] projectFiles;

    public SourceProject() {

    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public void setProjectFiles(File[] projectFiles) {
        this.projectFiles = projectFiles;
    }

    public File[] getProjectFiles() {
        return projectFiles;
    }
}
