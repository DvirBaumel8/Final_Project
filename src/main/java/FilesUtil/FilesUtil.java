package FilesUtil;

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

    public File[] openProject(String directoryPath) {
        try {
            File directory = new File(directoryPath);
            File[] contents = directory.listFiles();
            if(validateProject(contents)) {
                return contents;
            }
            else {
                //throw new exception
            }
            return contents;
        }
        catch(Exception e) {

        }
        return null;
    }

    private boolean validateProject(File[] contents) {
        return true;
    }

    public void createNewPomFileWithSpringDependencies(File[] projectFile, String pathOfCreatedPom) throws IOException {
        File pomFile = findFileByName(projectFile);
        String pomContent = getFileContent(pomFile);
        pathOfCreatedPom = pathOfCreatedPom + "/pom.xml";
        File newPomFile = new File(pathOfCreatedPom);
        copyFileContent(pomContent, newPomFile);
        addSpringDependenciesToPomFile(newPomFile);
    }

    private void addSpringDependenciesToPomFile(File newPomFile) throws IOException {
        String pomContent = getFileContent(newPomFile);
        List<String> lines = Files.readAllLines(newPomFile.toPath(), StandardCharsets.UTF_8);
        PrintStream out = new PrintStream(new FileOutputStream(newPomFile));

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

    private void copyFileContent(String pomContent, File pomFile) throws IOException {
        PrintStream out = new PrintStream(new FileOutputStream(pomFile));
        out.write(pomContent.getBytes());
    }

    public static String getFileContent(File pomFile) {
        StringBuilder content = new StringBuilder();
        Path pomPath = pomFile.toPath();
        List<String> lines = null;

        try {
            lines = Files.readAllLines(pomPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String line : lines) {
            content.append(line);
            content.append(System.lineSeparator());
        }

        return content.toString();
    }

    private File findFileByName(File[] projectFile) {
        for(int i = 0; i < projectFile.length; i++) {
            if(projectFile[i].getName().equals("pom.xml")) {
                return projectFile[i];
            }
        }
        return null;
    }

    private String getDependenciesStrWithOpen() {
        return "  <dependencies>\n" +
                "\n" +
                "        <dependency>\n" +
                "            <groupId>org.springframework</groupId>\n" +
                "            <artifactId>spring-core</artifactId>\n" +
                "            <version>5.2.1.RELEASE</version>\n" +
                "        </dependency>\n" +
                "\n" +
                "    </dependencies>";
    }

    private String getDependenciesStrWithOutOpen() {
        return "        <dependency>\n" +
                "            <groupId>org.springframework</groupId>\n" +
                "            <artifactId>spring-core</artifactId>\n" +
                "            <version>5.2.1.RELEASE</version>\n" +
                "        </dependency>\n";
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
        Files.createDirectory(path);
        return pathOfNewSpringProject;
    }

    public void createConfigurationFile(String path){
        //TODO
    }

    public void addNewBeanToConfigurationFile(String fileName, String beanName, String objectType) {

    }
}
