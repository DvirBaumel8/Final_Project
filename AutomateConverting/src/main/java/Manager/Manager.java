package Manager;

import FilesUtil.FilesUtil;
import ProjectContainers.EditProject;
import ProjectContainers.SourceProject;
import ProjectScanning.Scanner;
import UnitTests.UnitTestValidator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Manager {
    private static Manager manager;
    private FilesUtil filesUtil;
    private SourceProject sourceProject;
    private EditProject editProject;
    private UnitTestValidator unitTestValidator;
    private String projectDirectoryPath;


    public static Manager getInstance() {
        if(manager == null) {
            manager = new Manager();
        }
        return manager;
    }

    private Manager() {
        filesUtil = new FilesUtil();
    }

    public void start() throws Exception {
        getProjectFromUser();
        init(projectDirectoryPath);
        scanProject();
        unitTestValidator.runUnitTests(editProject.getEditProjectFiles());
    }

    private void getProjectFromUser() throws Exception {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        System.out.println("Enter project path:");
        projectDirectoryPath = "/Users/db384r/Dev/Final_Project/Project/Final_Project/projects to convert/3/without spring";
        if(!validateProjectDirectoryPath(projectDirectoryPath)) {
            throw new Exception(getProjectPathErrorMessage());
        }
        }

    private void scanProject() throws IOException {
        Scanner scanner = new Scanner();
        scanner.scan(filesUtil.getJavaDirectory(editProject.getEditProjectFiles()).getPath());
    }

    private void init(String projectPath) throws IOException {
        unitTestValidator = new UnitTestValidator();
        editProject = new EditProject();
        sourceProject = new SourceProject();
        sourceProject.setProjectPath(projectPath);
        setValueToProjectName();
        editProject.setPath(filesUtil.createNewSpringProjectDirectory(projectPath));
        sourceProject.setProjectFiles(filesUtil.getProjectFiles(projectPath));
        copyPasteSourceToEdit();
        initializeEditProjectToSpring();
    }

    private void initializeEditProjectToSpring() throws IOException {
        filesUtil.addSpringDependenciesToPomFile(editProject.getEditProjectFiles());
        filesUtil.addMainConfFile();
    }

    private void copyPasteSourceToEdit()  {
        editProject.initFilesArr(sourceProject.getProjectFiles().length);
        filesUtil.copyPasteSourceToEdit(sourceProject.getProjectFiles(), editProject.getPath());
    }

    public File getJavaDirectory() {
        return filesUtil.getJavaDirectory(editProject.getEditProjectFiles());
    }

    private void setValueToProjectName( ) {
        String[] elements = sourceProject.getProjectPath().split("/");
        sourceProject.setProjectName(elements[elements.length - 1]);
    }

    public boolean validateProjectDirectoryPath(String projectDirectoryPath) {
        boolean isValid = true;

        if(!validateDirectoryContainPomFile(projectDirectoryPath)) {
            isValid = false;
        }
        if(!Files.exists(Paths.get(projectDirectoryPath))) {
            isValid = false;
        }
        return isValid;
    }

    private boolean validateDirectoryContainPomFile(String projectDirectoryPath) {
        return true;
    }

    public String getProjectPathErrorMessage() {
        return "error test";
    }

    public void addFileToEditProject(File editFile) {
        editProject.addFile(editFile);
    }
}
