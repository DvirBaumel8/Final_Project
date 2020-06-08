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
    private Map<String, BeanDetails> instanceNameToBeanDetails;
    private boolean isBeanAdded;
    private Set<String> classesToAddAppCox;

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
    }

    public void scan(String javaDir) throws IOException {
        findClasses(javaDir);
        findNewProjectInstancesCreation();
        createBeanMethods();
        handleAppCtxWriteToNeededClasses();
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

        for(Map.Entry<String, BeanDetails> entry : instanceNameToBeanDetails.entrySet()) {
            cBeanMethodStr = createBeanMethodStr(entry.getKey(), entry.getValue());
            addMethodToFile(mainConf, cBeanMethodStr);
            if(isListObj(entry)) {
                addListImportsToConfFile(entry.getValue().getConfigurationFilePath());
            }
        }

    }

    private void addListImportsToConfFile(String configurationFilePath) throws IOException {
        File file = new File(configurationFilePath);
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        int index = lines.indexOf("import org.springframework.context.annotation.Configuration;");
        index++;

        if(!lines.contains("import java.util.List;")) {
           lines.add(index, "import java.util.List;");
        }
        if(!lines.contains("import java.util.ArrayList;")) {
            lines.add(index, "import java.util.ArrayList;");
        }

        writeNewContentToFile(file, lines);
    }


    private boolean isListObj(Map.Entry<String, BeanDetails> entry) {
        return entry.getValue().getClassName().contains("List");
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

    private String createBeanMethodStr(String instanceName, BeanDetails beanDetails) throws IOException {
        String origClass = beanDetails.getClassName();
        StringBuilder str = new StringBuilder();
        str.append("@Bean\n");
        if(beanDetails.isPrototypeInst()) {
            str.append("@Scope(\"prototype\")\n");
        }
        str.append(String.format("public %s %s() {\n", origClass, instanceName));
        String constructorArgs = beanDetails.getConstructorArgs();
        str.append(String.format("%s %s = new %s(%s);\n", origClass, instanceName, beanDetails.getImplName(), constructorArgs));
        String settersStr = getSettersStrs(beanDetails);
        str.append(settersStr);
        str.append(String.format("return %s;\n}\n", instanceName));

        return str.toString();
    }

    private String getSettersStrs(BeanDetails beanDetails) throws IOException {
        File file = null;
        String createdUnderClass = beanDetails.getCreatedUnderClass();
        for(Map.Entry<String, String> entry : classNameToPath.entrySet()) {
            if(createdUnderClass.equals(entry.getKey())) {
                file = new File(entry.getValue());
                break;
            }
        }

        return getSettersStrs(file, beanDetails.getInstanceName());
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
            handleInternalClassScan(entry.getKey(), entry.getValue());
        }
    }

    private void handleInternalClassScan(String createsClass, String classPath) throws IOException {
        Map<String, String> replaceLineWithNew = new HashMap<>();
        File cFile = new File(classPath);
        List<String> allLines = Files.readAllLines(cFile.toPath(), StandardCharsets.UTF_8);
        int index = 0;
        List<Integer> linesToRemove = new ArrayList<>();

        for(String line : allLines) {
            if(line.contains("new")) {
                if(checkIFNewInstanceIsBlackList(allLines, index)) {
                    linesToRemove.add(index - 1);
                    index++;
                    continue;
                }
                BeanDetails beanDetails = findAllInstanceElements(line, allLines);

                if(isInternalClass(beanDetails)) {
                    beanDetails.setConfigurationFilePath(classNameToPath.get("MainConfiguration"));
                    beanDetails.setCreatedUnderClass(createsClass);
                    classesToAddAppCox.add(createsClass);
                    instanceNameToBeanDetails.put(beanDetails.getInstanceName(), beanDetails);
                    replaceLineWithNew.put(line, getBeanAccessStr(beanDetails));
                    beanDetails.setPrototypeInst(checkIfInstancePrototype(allLines, index));
                    if(beanDetails.isPrototypeInst()) {
                        linesToRemove.add(index - 1);
                    }
                }
            }
            index++;
        }
        String empty = "";
        int counter = 0;
        for(Integer indexBlack : linesToRemove) {
            allLines.remove(indexBlack - counter);
            counter++;
        }
        for(Map.Entry<String, String> item : replaceLineWithNew.entrySet()) {
            replaceLines(item.getKey(), item.getValue(), allLines);
        }
        writeNewContentToFile(cFile, allLines);
    }

    private boolean checkIfInstancePrototype(List<String> allLines, int index) {
        String lineBeforeCreation = allLines.get(index - 1);

        return lineBeforeCreation.contains("@prototype");
    }

    private BeanDetails findAllInstanceElements(String line, List<String> allLines) {
        String temp;
        String className;
        String instanceName;
        String implName;
        String constructorArgs;

        temp = line.trim();
        temp = temp.replace(", ", ",");
        String[] elements = temp.split(" ");

        //handle data structure
        className = elements[0];
        instanceName = elements[1];
        if(elements.length <=4) {
            implName = elements[3].split("\\(")[0];
        }
        else {
            implName = elements[4].split("\\(")[0];
        }
        constructorArgs = line.substring(line.indexOf("(") + 1, line.indexOf(")"));

        return new BeanDetails(className, instanceName, implName, constructorArgs, line);
    }

    private boolean checkIFNewInstanceIsBlackList(List<String> allLines, int index) {
        String lineBeforeCreation = allLines.get(index - 1);

        return lineBeforeCreation.contains("@BlackList");
    }


    private void replaceLines(String lineToRemove, String lineToWrite, List<String> allLines) {
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

    private String getBeanAccessStr(BeanDetails beanDetails) {
        if(beanDetails.isDataStructure()) {
            return String.format("      %s %s = context.getBean(\"%s\", %s.class);", beanDetails.getClassName(), beanDetails.getInstanceName(), beanDetails.getInstanceName(), beanDetails.getClassName().split("<")[0]);
        }
        else {
            return String.format("      %s %s = context.getBean(\"%s\", %s.class);", beanDetails.getClassName(), beanDetails.getInstanceName(), beanDetails.getInstanceName(), beanDetails.getClassName());
        }


    }

    private boolean isInternalClass(BeanDetails beanDetails) {
        if(isDataStructureInstance(beanDetails)) {
            if(checkIfClassNameExist(beanDetails.getClassName())) {
                if(checkIfListTypeIsInternal(beanDetails.getClassName())) {
                    return true;
                }
            }
            else {

            }

        }
        return checkIfSimpleTypeISInternal(beanDetails.getClassName());
    }

    private boolean checkIfClassNameExist(String className) {
        String str = className.substring(className.indexOf("<") + 1, className.indexOf(">"));
        return str.length() > 0;
    }

    private boolean checkIfListTypeIsInternal(String className) {
       String str = className.substring(className.indexOf("<") + 1, className.indexOf(">"));
       return checkIfSimpleTypeISInternal(str);
    }

    private boolean checkIfSimpleTypeISInternal(String className) {
        for(Map.Entry<String, String> entry : classNameToPath.entrySet()) {
            if(entry.getKey().equals(className)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDataStructureInstance(BeanDetails beanDetails) {
        if(beanDetails.getClassName().contains("List")) {
            beanDetails.setDataStructure(true);
            return true;
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
