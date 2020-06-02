package ProjectScanning;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Scanner {
    private Map<String, String> classNameToPath;
    private Map<String, NewBeanDetails> instanceNameToBeanDetails;
    private boolean isBeanAdded;
    private Set<String> classesToAddAppCox;
    private List<String> blackListInstances;
    private boolean isProjectBlackListing;

    private static String OS = System.getProperty("os.name").toLowerCase();
    private static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    private static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }


    public Scanner() {
        classNameToPath = new HashMap<>();
        instanceNameToBeanDetails = new HashMap<>();
        classesToAddAppCox = new HashSet<>();
        isBeanAdded = false;
        blackListInstances = new ArrayList<>();
        isProjectBlackListing = false;
    }

    public void scan(String javaDir) throws IOException {
        BlackListUtil blackListUtil = new BlackListUtil(getResourcesPathFromJavaPath(javaDir));
        if(blackListUtil.isBlackListFileExist()) {
            if(blackListUtil.needEnforceBlackList()) {
                blackListUtil.parseBlackListFile();
                isProjectBlackListing = true;
            }
        }
        blackListInstances = BlackListUtil.getBlackListInstances();

        findClasses(javaDir);
        findNewProjectInstancesCreation();
        createBeanMethods();
        handleAppCtxWriteToNeededClasses();
    }

    private String getResourcesPathFromJavaPath(String javaDir) {

        Path javaDirPath = Paths.get(javaDir);
        StringBuilder pathOfResources = new StringBuilder();

        if(isMac())
            pathOfResources.append(File.separator + javaDirPath.getParent() + File.separator + "resources");
        if(isWindows())
            pathOfResources.append(javaDirPath.getParent() + File.separator + "resources");

        return pathOfResources.toString();
    }

    private void handleAppCtxWriteToNeededClasses() throws IOException {
        for(Map.Entry<String, String> entry : classNameToPath.entrySet()) {
            for(String clazz : classesToAddAppCox) {
                if(clazz.equals(entry.getKey())) {
                    addAppCtxToClass(clazz, entry.getValue());
                }
            }
        }
    }

    private void addAppCtxToClass(String clazz, String classPath) throws IOException {
        File file = new File(classPath);
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        int index = 0;

        for(String line : lines) {
            if(line.contains(String.format("class %s", clazz))) {
                index++;
                break;
            }
            index++;
        }
        lines.add(index, "   private static ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);");
        index -= 2;
        lines.add(index, "import org.springframework.context.ApplicationContext;\n" +
                "import org.springframework.context.annotation.AnnotationConfigApplicationContext;");
        writeNewContentToFile(file, lines);
    }

    private void createBeanMethods() throws IOException {
        File mainConf = getMainConfigurationFile();
        String cBeanMethodStr;

        for(Map.Entry<String, NewBeanDetails> entry : instanceNameToBeanDetails.entrySet()) {
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

    private String createBeanMethodStr(String instanceName, NewBeanDetails beanDetails) throws IOException {
        String origClass = beanDetails.getOrigClass();
        StringBuilder str = new StringBuilder();
        str.append("@Bean\n");
        str.append(String.format("public %s %s() {\n", origClass, instanceName));
        String constructorArgs = beanDetails.getCreationObjectConstructorArgs();
        str.append(String.format("%s %s = new %s(%s);\n", origClass, instanceName, origClass, constructorArgs));
        String settersStr = getSettersStrs(beanDetails);
        str.append(settersStr);
        str.append(String.format("return %s;\n}\n", instanceName));

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

    private void findNewProjectInstancesCreation() throws IOException {

        for (Map.Entry<String, String> entry : classNameToPath.entrySet()) {
            if (entry.getKey().equals("MainConfiguration")) {
                continue;
            }
            handleInternalClassScan(entry.getValue(), entry.getKey());
        }
    }

    private void handleInternalClassScan(String classPath, String key) throws IOException {
        String[] elements;
        String className;
        String instanceName;
        Map<String, String> replaceLineWithNew = new HashMap<>();
        File cFile = new File(classPath);
        List<String> allLines = Files.readAllLines(cFile.toPath(), StandardCharsets.UTF_8);
        int index = 0;

        for(String line : allLines) {
            if(line.contains("new")) {
                elements = line.split(" ");
                className = findClassName(elements);
                instanceName = findInstanceName(elements);
                if(isProjectBlackListing) {
                    if(verifyInstanceIsNotBlackListed(instanceName)) {
                        if(checkIfClassIsInternal(className)) {
                            NewBeanDetails beanDetails = new NewBeanDetails(key, line, instanceName, className);;
                            classesToAddAppCox.add(key);
                            if(checkIfClassIsInternal(className)) {
                                instanceName = findInstanceName(elements);
                                instanceNameToBeanDetails.put(instanceName, beanDetails);
                                replaceLineWithNew.put(line, getBeanAccessStr(className, instanceName));
                            }
                        }
                    }
                }
                else {
                    if(checkIfClassIsInternal(className)) {
                        NewBeanDetails beanDetails = new NewBeanDetails(key, line, instanceName, className);;
                        classesToAddAppCox.add(key);
                        if(checkIfClassIsInternal(className)) {
                            instanceName = findInstanceName(elements);
                            instanceNameToBeanDetails.put(instanceName, beanDetails);
                            replaceLineWithNew.put(line, getBeanAccessStr(className, instanceName));
                        }
                    }
                }
            }
            index++;
        }
        for(Map.Entry<String, String> item : replaceLineWithNew.entrySet()) {
            replaceLines(cFile, item.getKey(), item.getValue(), allLines);
        }
        writeNewContentToFile(cFile, allLines);
    }

    private boolean verifyInstanceIsNotBlackListed(String instanceName) {
        if(!isProjectBlackListing) {
            return false;
        }
        else {
            if(blackListInstances.contains(instanceName)) {
                return false;
            }
            else {
                return true;
            }
        }
    }

    private void replaceLines(File cFile, String lineToRemove, String lineToWrite, List<String> allLines) {
        int index = 0;

        for(String line : allLines) {
            if(line.equals(lineToRemove)) {
                allLines.remove(lineToRemove);
                break;
            }
            index++;
        }
        allLines.add(index, lineToWrite);
    }

    private String getBeanAccessStr(String className, String instanceName) {
        return String.format("      %s %s = context.getBean(\"%s\", %s.class);", className, instanceName, instanceName, className);

    }

    private String findInstanceName(String[] elements) {
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

    private boolean checkIfClassIsInternal(String instanceName) {
        for(Map.Entry<String, String> entry : classNameToPath.entrySet()) {
            if(entry.getKey().equals(instanceName)) {
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
