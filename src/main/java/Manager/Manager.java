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


    public static Manager getInstance() {
        if(manager == null) {
            manager = new Manager();
        }
        return manager;
    }

    private Manager() {
        filesUtil = new FilesUtil();
    }

    public void start(String projectPath) throws IOException {
        init(projectPath);
        scanProject();
        unitTestValidator.runUnitTests(editProject.getEditProjectFiles());
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
        filesUtil.addAnnotationContextToMain(editProject.getEditProjectFiles());
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
        if(Files.exists(Paths.get(projectDirectoryPath))) {

        }
        return true;

    }

    public String getProjectPathErrorMessage() {
        return null;
    }

    public void addFileToEditProject(File editFile) {
        editProject.addFile(editFile);
    }
}
