package FilesUtil;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
        System.out.println("");
    }

    private boolean checkIfPomContentContainsDependenciesOpen(List<String> lines) throws IOException {

        for(String line : lines) {
            if(line.contains("<dependencies>")) {
                return true;
            }
        }
        return false;
    }

    //Maybe we have to delete
    public File findFileByName(File[] projectFiles, String fileName) {
        File main;
        for(int i = 0; i < projectFiles.length; i++) {
            if(projectFiles[i].listFiles() != null ) {
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


    public String createNewSpringProjectDirectory(String projectPath) throws IOException {
        String[] elements = projectPath.split("/");
        StringBuilder pathOfNewSpringProject = new StringBuilder();
        String separator = "/";
        pathOfNewSpringProject.append(separator);

        for(int i = 1; i < elements.length - 1; i++) {
            pathOfNewSpringProject.append(elements[i] + separator);
        }

        return createNewFolderInSpecificPath(pathOfNewSpringProject.toString(), elements[elements.length - 1]);
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

    public void createConfigurationFile(String path){
        //TODO
    }

    public void addNewBeanToConfigurationFile(String fileName, String beanName, String objectType) {

    }

    public void addAnnotationContextToFile(File mainFile) throws IOException {
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

    public String getJavaDir() {
        return "/Users/db384r/Dev/Final_Project/First examples/Without spring_Spring_Way/src/main/java";
    }
}
