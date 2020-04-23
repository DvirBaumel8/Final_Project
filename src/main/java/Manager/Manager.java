package Manager;

import FilesUtil.FilesUtil;

import java.io.File;
import java.io.IOException;

public class Manager {

    private FilesUtil filesUtil;
    private String projectName;

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Manager() {
        filesUtil = new FilesUtil();
    }

    public void start(String projectPath) throws IOException {
        setValueToProjectName(projectPath);
        File[] projectFile = filesUtil.openProject(projectPath);
        String pathOfNewSpringProject = filesUtil.createNewSpringProjectDirectory(projectPath);
        filesUtil.createNewPomFileWithSpringDependencies(projectFile, pathOfNewSpringProject);

    }

    private void setValueToProjectName(String projectPath) {
        String[] elements = projectPath.split("/");
        setProjectName(elements[elements.length - 1]);
    }

}
