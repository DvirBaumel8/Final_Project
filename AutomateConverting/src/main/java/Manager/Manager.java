package Manager;

import FilesUtil.*;
import ProjectContainers.EditProject;
import ProjectContainers.SourceProject;
import ProjectScanning.Scanner;
import com.google.googlejavaformat.java.FormatterException;

import java.io.File;
import java.io.IOException;

public class Manager {
    private static Manager manager;
    private FilesUtil filesUtil;
    private JavaFormatter javaFormatter;
    private XmlFormatter xmlFormatter;
    private SourceProject sourceProject;
    private EditProject editProject;
    private String projectDirectoryPath;
    private ProjectValidator projectValidator;

    public static Manager getInstance() {
        if (manager == null) {
            manager = new Manager();
        }
        return manager;
    }

    private Manager() {
        filesUtil = new FilesUtil();
        javaFormatter = new JavaFormatter();
        xmlFormatter = new XmlFormatter();
        projectValidator = new ProjectValidator();
    }

    public void start() throws Exception {
        validateProjectPath();
        executeInitConverting(projectDirectoryPath);
        scanProject();
        indentProject();
    }

    private void validateProjectPath() throws Exception {
        projectDirectoryPath = "/Users/db384r/Dev/Final_Project/Project/Final_Project/projects to convert/1/without spring";
        if (!projectValidator.validateProjectDirectoryPath(projectDirectoryPath)) {
            throw new Exception(getProjectPathErrorMessage());
        }
    }

    private void scanProject() throws IOException {
        Scanner scanner = new Scanner();
        scanner.scan(filesUtil.getJavaDirectory(editProject.getEditProjectFiles()).getPath());
    }

    private void indentProject() {
       File root = new File(projectDirectoryPath + "_Spring_Way");
       indentRec(root);
    }

    private void indentRec(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.getName().contains(".java")) {
                    try {
                        javaFormatter.FormatFile(child.getAbsolutePath());
                    } catch (IOException | FormatterException e) {
                        e.printStackTrace();
                    }
                }
                if (child.getName().contains(".xml")) {
                    try {
                        xmlFormatter.FormatFile(child.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                indentRec(child);
            }
        }
    }

    private void executeInitConverting(String projectPath) throws IOException {
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

    private void copyPasteSourceToEdit() {
        editProject.initFilesArr(sourceProject.getProjectFiles().length);
        filesUtil.copyPasteSourceToEdit(sourceProject.getProjectFiles(), editProject.getPath());
    }

    public File getJavaDirectory() {
        return filesUtil.getJavaDirectory(editProject.getEditProjectFiles());
    }

    private void setValueToProjectName() {
        String[] elements = sourceProject.getProjectPath().split("/");
        sourceProject.setProjectName(elements[elements.length - 1]);
    }

    public String getProjectPathErrorMessage() {
        return "error test";
    }

    public void addFileToEditProject(File editFile) {
        editProject.addFile(editFile);
    }

    public File[] getProjectsFile() {
        return editProject.getEditProjectFiles();
    }
}
