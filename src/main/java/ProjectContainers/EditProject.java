package ProjectContainers;

import java.io.File;

public class EditProject {
    private String path;
    private File[] editProjectFile;
    private static int index = 0;

    public EditProject() {

    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public File[] getEditProjectFile() {
        return editProjectFile;
    }

    public void addFile(File file) {
        editProjectFile[index] = file;
        index++;
    }

    public void initFilesArr(int length) {
        editProjectFile = new File[length];
    }
}
