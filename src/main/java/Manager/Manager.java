package Manager;

import FilesUtil.FilesUtil;
import ProjectContainers.EditProject;
import ProjectContainers.SourceProject;
import ProjectScanning.FilesScanner;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Manager {
    private static Manager manager;
    private FilesUtil filesUtil;
    private FilesScanner scanner;
    private SourceProject sourceProject;
    private EditProject editProject;
    private static final String MAIN = "Main.java";

    public static Manager getInstance() {
        if(manager == null) {
            manager = new Manager();
        }
        return manager;
    }

    private Manager() {
        filesUtil = new FilesUtil();
        editProject = new EditProject();
    }

    public void start(String projectPath) throws IOException {
        init(projectPath);
        scanner = new FilesScanner();
        scanner.scan(filesUtil.getJavaDir());
    }

    private void init(String projectPath) throws IOException {
        sourceProject = new SourceProject();
        sourceProject.setProjectPath(projectPath);
        setValueToProjectName();

        editProject.setPath(filesUtil.createNewSpringProjectDirectory(projectPath));
        sourceProject.setProjectFiles(filesUtil.getProjectFiles(projectPath));

        copyPasteSourceToEdit();
        filesUtil.addSpringDependenciesToPomFile(editProject.getEditProjectFile());
        initializeEditProjectToSpring();
    }

    private void initializeEditProjectToSpring() throws IOException {
        addAnnotationContextToMain();
        addMainConfFile();
    }

    private void addMainConfFile() throws IOException {
        String createPath = getJavaDirectory().getPath() + "/MainConfiguration.java";
        File confFile = new File(createPath);
        filesUtil.populateConfigMainFile(confFile);
    }

    private void addAnnotationContextToMain() throws IOException {
        File mainFile = filesUtil.findFileByName(editProject.getEditProjectFile(), MAIN);
        filesUtil.addAnnotationContextToFile(mainFile);
    }

    private void copyPasteSourceToEdit() throws IOException {
        File[] sourceProjectFiles = sourceProject.getProjectFiles();
        editProject.initFilesArr(sourceProjectFiles.length);

        for(int i =0; i < sourceProjectFiles.length; i++) {
                copyPasteFile(sourceProjectFiles[i], editProject.getPath() + "/" + sourceProjectFiles[i].getName());
        }
    }

    private void copyPasteFile(File sourceProjectFile, String s) throws IOException {
        Path path = Paths.get(s);
        File editFile;

        try {
            if(sourceProjectFile.isDirectory()) {
                Files.createDirectory(path);
                editFile = new File(s);
                editProject.addFile(editFile);
                FileUtils.copyDirectory(sourceProjectFile, editFile);
            }
            else {
                editFile = new File(s);
                editProject.addFile(editFile);
                FileUtils.copyFile(sourceProjectFile, editFile);
            }
        }
        catch (Exception e) {
            System.out.println();
        }

    }

    private File getJavaDirectory() {
        File src = null;
        File main = null;
        File[] projectFiles = editProject.getEditProjectFile();

        for(int i =0; i < projectFiles.length; i++) {
            if(projectFiles[i].getName().equals("src")) {
                src = projectFiles[i];
            }
        }
        if(src == null) {
            //throw exception
        }
        File[] srcFiles = src.listFiles();
        for(int i = 0; i < srcFiles.length; i++) {
            if(srcFiles[i].getName().equals("main")){
                main = srcFiles[i];
            }
        }
        if(main == null) {
            //throw Excetion
        }

        File[] mainFiles = main.listFiles();

        for(int i = 0; i < mainFiles.length; i++) {
            if(mainFiles[i].getName().equals("java")) {
                return mainFiles[i];
            }
        }

        //throw exception

        return null;

    }

    private void setValueToProjectName( ) {
        String[] elements = sourceProject.getProjectPath().split("/");
        sourceProject.setProjectName(elements[elements.length - 1]);
    }

    public boolean validateProjectDirectoryPath(String projectDirectoryPath) {
        return true;
    }

    public String getProjectPathErrorMessage() {
        return null;
    }
}
