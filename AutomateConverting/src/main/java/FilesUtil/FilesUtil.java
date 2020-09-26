package FilesUtil;

import Manager.Manager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FilesUtil {
    private static final String POM_FILE = "pom.xml";
    private static final String MAIN_METHOD = "public static void main(String[] args) {";
    private static final String APP_CONTEXT = "ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);";
    private static final String SPRING_IMPORT = "import org.springframework.context.ApplicationContext;\nimport org.springframework.context.annotation.AnnotationConfigApplicationContext;";
    private static final String MAIN = "Main.java";

    private static String OS = System.getProperty("os.name").toLowerCase();
    private static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    private static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public File[] getProjectFiles(String directoryPath) {
        try {
            File directory = new File(directoryPath);
            File[] content = directory.listFiles();
            if(validateProject(content)) {
                return content;
            }
            else {
                //throw new exception
            }
            return content;
        }
        catch(Exception e) {

        }
        return null;
    }

    private boolean validateProject(File[] content) {
        return true;
    }

    public void addSpringDependenciesToPomFile(File[] projectFile) throws IOException {
        File pomFile = findFileByName(projectFile, POM_FILE);
        addSpringDependenciesToPomFile(pomFile);
    }

    private void addSpringDependenciesToPomFile(File pomFile) throws IOException {
        List<String> lines = Files.readAllLines(pomFile.toPath(), StandardCharsets.UTF_8);
        PrintStream out = new PrintStream(new FileOutputStream(pomFile));

        int index = 0;


        if(checkIfPomContentContainsDependenciesOpen(lines)) {
            for(String line : lines) {
                index++;
                if(line.contains("<dependencies>")) {
                    break;
                }

            }
            lines.add(index, getDependenciesStrWithOutOpen());

        }
        else {
            for(String line : lines) {
                index++;
                if(line.contains("<version>")) {
                    break;
                }

            }
            lines.add(index, getDependenciesStrWithOpen());
        }

        StringBuilder newContent = new StringBuilder();
        for(String line : lines) {
            newContent.append(line);
            newContent.append(System.lineSeparator());
        }
        out.write(newContent.toString().getBytes());
    }

    private boolean checkIfPomContentContainsDependenciesOpen(List<String> lines) {

        for(String line : lines) {
            if(line.contains("<dependencies>")) {
                return true;
            }
        }
        return false;
    }

    public static File findFileByName(File[] projectFiles, String fileName) {
        File main;
        for(int i = 0; i < projectFiles.length; i++) {
            if(projectFiles[i].listFiles() != null && !projectFiles[i].getName().equals("target")) {
                  main = findFileByName(projectFiles[i].listFiles(), fileName);
                  if(main != null) {
                      return main;
                  }
            }
            if(projectFiles[i].getName().equals(fileName)) {
                return projectFiles[i];
            }

        }
        return null;
    }

    private String getDependenciesStrWithOpen() {
        return "    <dependencies>\n" +
                "    <!--spring core-->\n" +
                "    <dependency>\n" +
                "        <groupId>org.springframework</groupId>\n" +
                "        <artifactId>spring-context</artifactId>\n" +
                "        <version>5.0.6.RELEASE</version>\n" +
                "    </dependency>\n" +
                "    </dependencies>";
    }

    private String getDependenciesStrWithOutOpen() {
        return "    <dependency>\n" +
                "        <groupId>org.springframework</groupId>\n" +
                "        <artifactId>spring-context</artifactId>\n" +
                "        <version>5.0.6.RELEASE</version>\n" +
                "    </dependency>";
    }


    public String createNewSpringProjectDirectory(String projectPathString) throws IOException {
        Path projectPath = Paths.get(projectPathString);
        StringBuilder pathOfNewSpringProject = new StringBuilder();

        if(isMac())
            pathOfNewSpringProject.append(File.separator + projectPath.getParent() + File.separator);
        if(isWindows())
            pathOfNewSpringProject.append(projectPath.getParent() + File.separator);
        return createNewFolderInSpecificPath(pathOfNewSpringProject.toString(), projectPath.getFileName().toString());
    }

    private String createNewFolderInSpecificPath(String pathOfNewSpringProject, String directoryName) throws IOException {
        pathOfNewSpringProject = pathOfNewSpringProject + directoryName + "_Spring_Way";
        Path path = Paths.get(pathOfNewSpringProject);
        if(Files.exists(path)) {
            FileUtils.cleanDirectory(new File(pathOfNewSpringProject));
        }
        else {
            Files.createDirectory(path);
        }
        return pathOfNewSpringProject;
    }

    public void addAnnotationContextToMain(File[] editProjectFiles) throws IOException {
        File mainFile = findFileByName(editProjectFiles, MAIN);
        List<String> allLines = Files.readAllLines(mainFile.toPath(), StandardCharsets.UTF_8);
        PrintStream out = new PrintStream(new FileOutputStream(mainFile));
        int index = 0;

        for(String line : allLines) {
            if(line.contains("package")) {
                index++;
            }
        }

        allLines.add(index, SPRING_IMPORT);
        index = 0;

        for(String line : allLines) {
            if(line.contains(MAIN_METHOD)) {
                index++;
                break;
            }
            index++;
        }
        allLines.add(index, APP_CONTEXT);

        StringBuilder newContent = new StringBuilder();
        for(String line : allLines) {
            newContent.append(line);
            newContent.append(System.lineSeparator());
        }
        out.write(newContent.toString().getBytes());
    }

    public void populateConfigMainFile(File confFile) throws IOException {
        PrintStream out = new PrintStream(new FileOutputStream(confFile));
        String mainConfFileStr = getMainConfString();
        out.write(mainConfFileStr.getBytes());
    }

    private String getMainConfString() {
        return "import org.springframework.context.annotation.Configuration;\n" +
                "\n" +
                "@Configuration\n" +
                "public class MainConfiguration {\n" +
                "    \n" +
                "}";
    }

    public void addMainConfFile() throws IOException {
        String createPath = Manager.getInstance().getJavaDirectory().getPath() + "/MainConfiguration.java";
        File confFile = new File(createPath);
        populateConfigMainFile(confFile);
    }

    public void copyPasteSourceToEdit(File[] sourceProjectFiles, String path) {
        for(int i =0; i < sourceProjectFiles.length; i++) {
            copyPasteFile(sourceProjectFiles[i], path + "/" + sourceProjectFiles[i].getName());
        }
    }

    private void copyPasteFile(File sourceProjectFile, String s) {
        Path path = Paths.get(s);
        File editFile;

        try {
            if(sourceProjectFile.isDirectory()) {
                Files.createDirectory(path);
                editFile = new File(s);
                Manager.getInstance().addFileToEditProject(editFile);
                FileUtils.copyDirectory(sourceProjectFile, editFile);
            }
            else {
                editFile = new File(s);
                Manager.getInstance().addFileToEditProject(editFile);
                FileUtils.copyFile(sourceProjectFile, editFile);
            }
        }
        catch (Exception e) {
            System.out.println();
        }
    }

    public File getJavaDirectory(File[] projectFiles) {
        File main = null;
        File src = null;

        try {
            src = getSrcDir(projectFiles);
            main = getMainDir(src);
            return getJavaDir(main);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static File getSrcDir(File[] projectFiles) throws Exception {
        for(int i =0; i < projectFiles.length; i++) {
            if(projectFiles[i].getName().equals("src")) {
                return projectFiles[i];
            }
        }
        throw new Exception("src directory did not found, sorry");
    }

    public File getMainDir(File srcFile) throws Exception {
        File[] files = srcFile.listFiles();

        for(int i =0; i < files.length; i++) {
            if(files[i].getName().equals("main")) {
                return files[i];
            }
        }
        throw new Exception("main directory did not found, sorry");
    }

    public File getJavaDir(File mainFile) throws Exception {
        File[] files = mainFile.listFiles();

        for(int i =0; i < files.length; i++) {
            if(files[i].getName().equals("java")) {
                return files[i];
            }
        }
        throw new Exception("java directory did not found, sorry");
    }

    public static File getTestDir(File srcFile) throws Exception {
        File[] files = srcFile.listFiles();

        for(int i =0; i < files.length; i++) {
            if(files[i].getName().equals("test")) {
                return files[i];
            }
        }
        throw new Exception("test directory did not found, sorry");
    }
}
