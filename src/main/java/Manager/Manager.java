package Manager;

import FilesUtil.FilesUtil;

import java.io.File;

public class Manager {

    private FilesUtil filesUtil;

    public void start(String projectPath) {
        File file = filesUtil.openFile(projectPath);
        if(file.exists()) {

        }
    }

}
