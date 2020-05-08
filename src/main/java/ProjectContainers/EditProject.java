package ProjectContainers;

import FilesUtil.FilesUtil;

import java.io.File;

public class EditProject {
    private String path;
    private File[] editProjectFiles;
    private static int index = 0;

    public EditProject() {

    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public File[] getEditProjectFiles() {
        return editProjectFiles;
    }

    public void addFile(File file) {
        editProjectFiles[index] = file;
        index++;
    }

    public void initFilesArr(int length) {
        editProjectFiles = new File[length];
    }


}
