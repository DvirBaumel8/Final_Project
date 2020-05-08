package ProjectScanning;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Scanner {
    private Map<String, String> classNameToPath;
    private Map<String, NewBeanDetails> objectNameToBeanDetails;
    private boolean isBeanAdded;

    public Scanner() {
        classNameToPath = new HashMap<>();
        objectNameToBeanDetails = new HashMap<>();
        isBeanAdded = false;
    }

    public void scan(String javaDir) throws IOException {
        findClasses(javaDir);
        findNewProjectObjectsCreation();
        createBeanMethodsToObjects();
    }

    private void createBeanMethodsToObjects() throws IOException {
        File mainConf = getMainConfigurationFile();
        String cBeanMethodStr;

        for(Map.Entry<String, NewBeanDetails> entry : objectNameToBeanDetails.entrySet()) {
            cBeanMethodStr = createBeanMethodStr(entry.getKey(), entry.getValue());
            addMethodToFile(mainConf, cBeanMethodStr);
        }

    }

    private void addMethodToFile(File mainConf, String cBeanMethodStr) throws IOException {
        if(!isBeanAdded) {
            addImportBeansToConf(mainConf);
            isBeanAdded = true;
        }
        List<String> lines = Files.readAllLines(mainConf.toPath(), StandardCharsets.UTF_8);

        int index = 0;

        for(String line : lines) {
            if(line.contains("@Configuration")) {
                index += 2;
                break;
            }
            index++;
        }
        lines.add(index, cBeanMethodStr);
        writeNewContentToFile(mainConf, lines);

    }

    private void writeNewContentToFile(File mainConf, List<String> lines) throws IOException {
        PrintStream out = new PrintStream(new FileOutputStream(mainConf));
        StringBuilder str = new StringBuilder();
        for(String line : lines) {
            str.append(line);
            str.append(System.lineSeparator());
        }
        out.write(str.toString().getBytes());
    }

    private void addImportBeansToConf(File mainConf) throws IOException {
        List<String> lines = Files.readAllLines(mainConf.toPath(), StandardCharsets.UTF_8);
        int index = 0;
        String beanImport = "import org.springframework.context.annotation.Bean;";

        for(String line : lines) {
            if(line.contains("Configuration")) {
                index++;
                break;
            }
            else {
                index++;
            }
        }
        lines.add(index, beanImport);

        writeNewContentToFile(mainConf, lines);
    }

    private String createBeanMethodStr(String objectName, NewBeanDetails beanDetails) throws IOException {
        String origClass = beanDetails.getOrigClass();
        StringBuilder str = new StringBuilder();
        str.append("@Bean\n");
        str.append(String.format("public %s %s() {\n", origClass, objectName));
        String constructorArgs = beanDetails.getCreationObjectConstructorArgs();
        str.append(String.format("%s %s = new %s(%s);\n", origClass, objectName, origClass, constructorArgs));
        String settersStr = getSettersStrs(beanDetails);
        str.append(settersStr);
        str.append(String.format("return %s;\n}\n", objectName));

        return str.toString();
    }

    private String getSettersStrs(NewBeanDetails beanDetails) throws IOException {
        File file = null;
        String createdUnderClass = beanDetails.getCreatedUnderClass();
        for(Map.Entry<String, String> entry : classNameToPath.entrySet()) {
            if(createdUnderClass.equals(entry.getKey())) {
                file = new File(entry.getValue());
                break;
            }
        }

        return getSettersStrs(file, beanDetails.getBeanName());
    }

    private String getSettersStrs(File file, String beanName) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        StringBuilder str = new StringBuilder();
        List<String> linesToRemove = new ArrayList<>();

        for(String line : lines) {
            if(line.contains(beanName + ".set")) {
                if(!line.contains("new")) {
                    str.append(line);
                    str.append(System.lineSeparator());
                    linesToRemove.add(line);
                }
            }
        }

        for(String line : linesToRemove) {
            lines.remove(line);
        }
        writeNewContentToFile(file, lines);

        return str.toString();
    }


    private File getMainConfigurationFile() {
        String mainConfPath = null;
        for(Map.Entry<String, String> entry : classNameToPath.entrySet()) {
            if(entry.getKey().equals("MainConfiguration")) {
                mainConfPath = entry.getValue();
                break;
            }
        }
        return new File(mainConfPath);
    }

    private void findNewProjectObjectsCreation() throws IOException {
        String classPath;
        File cFile;
        String[] elements;
        String className;
        String objectName;
        int index = 0;
        Map<String, String> replaceLineWithNew = new HashMap<>();

        for(Map.Entry<String, String> entry : classNameToPath.entrySet()) {
            if(entry.getKey().equals("MainConfiguration")) {
                continue;
            }
            classPath = entry.getValue();
            cFile = new File(classPath);
            List<String> allLines = Files.readAllLines(cFile.toPath(), StandardCharsets.UTF_8);
            for(String line : allLines) {
                if(line.contains("new")) {
                   elements = line.split(" ");
                   className = findClassName(elements);
                    if(checkIfClassIsInternal(className)) {
                        objectName = findObjectName(elements);
                        NewBeanDetails beanDetails = new NewBeanDetails(entry.getKey(), line, objectName, className);
                        objectNameToBeanDetails.put(objectName, beanDetails);
                        replaceLineWithNew.put(line, getBeanAccessStr(className, objectName));
                    }
                }
                index++;
            }
            index = 0;
            for(Map.Entry<String, String> item : replaceLineWithNew.entrySet()) {
                allLines.remove(item.getKey());
                allLines.add(getIndexToInjectBeanAccess(cFile), item.getValue());
            }
                writeNewContentToFile(cFile, allLines);
            }
        }

    private int getIndexToInjectBeanAccess(File cFile) throws IOException {
        List<String> allLines = Files.readAllLines(cFile.toPath(), StandardCharsets.UTF_8);
        int index = 0;

        for(String line : allLines) {
            if(line.contains("context = new")) {
                index++;
                break;
            }
            else {
                index++;
            }
        }

        return index;
    }

    private String getBeanAccessStr(String className, String objectName) {
        return String.format("%s %s = context.getBean(%s.class);", className, objectName, className);
    }

    private String findObjectName(String[] elements) {
        int index = 0;
        for(int i = 0; i < elements.length; i++) {
            if(elements[i].equals("")) {
                index++;
            }
            else {
                index++;
                break;
            }
        }
        return elements[index];
    }

    private String findClassName(String[] elements) {
            for(int i = 0; i < elements.length; i++) {
                if(elements[i].equals("new")) {
                    i++;
                    return elements[i].split(String.format("\\("))[0];
                }
            }
            return null;
    }

    private boolean checkIfClassIsInternal(String objectName) {
        for(Map.Entry<String, String> entry : classNameToPath.entrySet()) {
            if(entry.getKey().equals(objectName)) {
                return true;
            }
        }
        return false;
    }

    private void findClasses(String javaDir) throws IOException {
        File file = new File(javaDir);
        File[] files = file.listFiles();
        File currFile;

        for(int i = 0; i < files.length; i++) {
            currFile = files[i];
            if(currFile.isDirectory()) {
                findClasses(currFile.getPath());
            }
            else {
                if(scanClass(currFile)) {
                    classNameToPath.put(getDisplayClassName(currFile.getName()), currFile.getPath());
                }
            }
        }
    }

    private String getDisplayClassName(String name) {
        String[] names = name.split("\\.");
        return names[0];
    }


    private boolean scanClass(File currFile) throws IOException {
        if(!currFile.getName().endsWith(".java")) {
            return false;
        }
        else {
            List<String> allLines = Files.readAllLines(currFile.toPath(), StandardCharsets.UTF_8);
            for(String line : allLines) {
                if(line.contains("public class")) {
                    return true;
                }

            }
            return false;
        }
    }
}
