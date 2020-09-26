package ProjectScanning;

import FilesUtil.FilesUtil;
import Manager.Manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
    private List<ConfigurationFieldDetails> configurationFields;

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
        configurationFields = new ArrayList<>();
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
        index -= 1;
        lines.add(index, "import org.springframework.context.ApplicationContext;\n" +
                "import org.springframework.context.annotation.AnnotationConfigApplicationContext;");
        writeNewContentToFile(file, lines);
    }

    private void createBeanMethods() throws IOException {
        File mainConf = getMainConfigurationFile();
        String cBeanMethodStr;
        addScopeImportToConfFile(mainConf);

        for(Map.Entry<String, BeanDetails> entry : instanceNameToBeanDetails.entrySet()) {
            cBeanMethodStr = createBeanMethodStr(entry.getKey(), entry.getValue());
            addMethodToFile(mainConf, cBeanMethodStr);
            if(isListObj(entry)) {
                addListImportsToConfFile(entry.getValue().getConfigurationFilePath());
            }
        }

        addConfigurationPropertySource(mainConf);

    }

    private void addConfigurationPropertySource(File mainConf) throws IOException {
        List<ConfigurationFieldDetails> configurationFields = this.configurationFields;
        List<String> allLines = Files.readAllLines(mainConf.toPath(), StandardCharsets.UTF_8);

        for(ConfigurationFieldDetails configurationFieldDetails : configurationFields) {
            int index = 0;
            int searchedIndex = 0;
            for(String line : allLines) {
                if(line.contains("public class MainConfiguration")) {
                    searchedIndex = index;
                    break;
                }
                index++;

            }
            allLines.add(searchedIndex, String.format("@PropertySource(\"classpath:%s\")\n", configurationFieldDetails.getPath()));
            writeNewContentToFile(mainConf, allLines);
        }

        addPropertySourceImportToConfFile(mainConf);

    }

    private void addPropertySourceImportToConfFile(File mainConf) throws IOException {
        List<String> lines = Files.readAllLines(mainConf.toPath(), StandardCharsets.UTF_8);
        int index = 0;
        for(int i =0; i < lines.size(); i++) {
            if(lines.get(i).contains("org.springframework.context.annotation.Bean"));
            index = i + 1;
            break;
        }
        lines.add(index, "import org.springframework.context.annotation.PropertySource;");

        writeNewContentToFile(mainConf, lines);
    }

    private void addScopeImportToConfFile(File mainConf) throws IOException {
        List<String> lines = Files.readAllLines(mainConf.toPath(), StandardCharsets.UTF_8);
        int index = 0;
        for(int i =0; i < lines.size(); i++) {
            if(lines.get(i).contains("org.springframework.context.annotation.Bean"));
            index = i + 1;
            break;
        }
        lines.add(index, "import org.springframework.context.annotation.Scope;");

        writeNewContentToFile(mainConf, lines);
    }

    private void addLazyImportToConfFile(File mainConf) throws IOException {
        List<String> lines = Files.readAllLines(mainConf.toPath(), StandardCharsets.UTF_8);
        int index = 0;
        for(int i =0; i < lines.size(); i++) {
            if(lines.get(i).contains("org.springframework.context.annotation.Bean"));
            index = i + 1;
            break;
        }
        lines.add(index, "import org.springframework.context.annotation.Lazy;");

        writeNewContentToFile(mainConf, lines);
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
        return entry.getValue().getImplName().contains("List");
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

        if(beanDetails.getIsLazy()) {
            str.append("@Lazy\n");
            addLazyImportToConfFile(getMainConfigurationFile());
        }
        if(beanDetails.isPrototypeInst()) {
            str.append("@Scope(\"prototype\")\n");
        }

        String constructorArgs = beanDetails.getConstructorArgs();
        if(beanDetails.isDataStructure()) {
            if(beanDetails.getInsideExceptionThrowing()) {
                str.append(String.format("public List<%s> %s() throws Exception {\n", origClass, instanceName));
            }
            else {
                str.append(String.format("public List<%s> %s() {\n", origClass, instanceName));
            }

            str.append(String.format("List<%s> %s = new ArrayList<>();\n", origClass, instanceName));
        }
        else if(isConstructorArgsIsBean(constructorArgs)) {
            if(beanDetails.getInsideExceptionThrowing()) {
                str.append(String.format("public %s %s() throws Exception {\n", origClass, instanceName));
            }
            else {
                str.append(String.format("public %s %s() {\n", origClass, instanceName));
            }
            str.append(String.format("%s %s = new %s(%s());\n", origClass, instanceName, beanDetails.getImplName(), constructorArgs));
        }
        else {
            if(beanDetails.getInsideExceptionThrowing()) {
                str.append(String.format("public %s %s() throws Exception {\n", origClass, instanceName));
            }
            else  {
                str.append(String.format("public %s %s() {\n", origClass, instanceName));
            }

            str.append(String.format("%s %s = new %s(%s);\n", origClass, instanceName, beanDetails.getImplName(), constructorArgs));
        }

        String settersStr = getSettersStrs(beanDetails);
        str.append(settersStr);
        str.append(String.format("return %s;\n}\n", instanceName));

        return str.toString();
    }

    private boolean isLazyBean(BeanDetails beanDetails) throws IOException {
        //Add lazy annotation to class in case class constructor is using class static method.

        List<String> lines = getAllLinesByClassName(beanDetails.getClassName());

        lines = minimizeLinesToConstructorOnly(beanDetails, lines);

        for(String line : lines) {
            if(isLineUsingStaticMethodOfInnerClass(line)) {
                return true;
            }
        }
        return false;
    }

    private List<String> getAllLinesByClassName(String className) throws IOException {
        File file = new File(classNameToPath.get(className));
        return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
    }

    private boolean isLineUsingStaticMethodOfInnerClass(String line) {
        for(Map.Entry<String, String> entry : classNameToPath.entrySet()) {
            if(line.contains(entry.getKey()) && line.contains(".") && line.contains("()")) {
                return true;
            }
        }
        return false;
    }

    private List<String> minimizeLinesToConstructorOnly(BeanDetails beanDetails, List<String> lines) {
        int indexOfStart = 0;
        int indexOfEnd = 0;
        int index = 0;
        boolean found = false;

        for(String line : lines) {
            if(line.contains("public") && line.contains(beanDetails.getClassName())) {
                indexOfStart = index;
                found = true;
                index++;
            }
            else {
                index++;
                if(found) {
                    if(line.contains("}")) {
                        indexOfEnd = index;
                        break;
                    }
                }
            }
        }

        return lines.subList(indexOfStart, indexOfEnd);
    }

    private boolean isConstructorArgsIsBean(String constructorArgs) {
        return instanceNameToBeanDetails.containsKey(constructorArgs);
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

        return getSettersStrs(file, beanDetails.getInstanceName(), beanDetails.getLine());
    }

    private String getSettersStrs(File file, String beanName, String lineOfCreation) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        StringBuilder str = new StringBuilder();
        List<String> linesToRemove = new ArrayList<>();

        for(String line : lines) {
            if(line.contains(beanName + ".set")) {
                if(checkIfVariableIsBean(line)) {
                    String temp = getLineWithBeanAccess(line);
                    str.append(temp);
                    str.append(System.lineSeparator());
                    linesToRemove.add(line);
                }
                else if(checkIfNumberStringOrBoolean(line)) {
                    str.append(line);
                    str.append(System.lineSeparator());
                    linesToRemove.add(line);
                }
                else {
                    continue;
                }
            }
        }

        for(String line : linesToRemove) {
            lines.remove(line);
        }
        writeNewContentToFile(file, lines);

        return str.toString();
    }

    private boolean checkIfNumberStringOrBoolean(String line) {
        String setterInputValue = line.substring(line.indexOf("("), line.indexOf(")"));

        try {
            Integer.parseInt(setterInputValue);
        }
        catch (Exception ignored) {

        }
        try {
            Double.parseDouble(setterInputValue);
        }
        catch (Exception ignored) {

        }
        if(setterInputValue.contains("\"")) {
            return true;
        }
        else if(setterInputValue.contains("true") || setterInputValue.contains("false")) {
            return true;
        }
        return false;
    }

    private boolean checkIfVariableIsBean(String line) {
        int left = line.indexOf("(") + 1;
        int right = line.indexOf(")");
        String sub = line.substring(left, right);
        if(sub.contains(".")) {
            String element = sub.split("\\.")[0];
            return instanceNameToBeanDetails.containsKey(element);
        }
        return instanceNameToBeanDetails.containsKey(sub);
    }

    private String getLineWithBeanAccess(String line) {
        int left = line.indexOf("(") + 1;
        int right = line.indexOf(")");
        String sub = line.substring(left, right);
        if(sub.contains(".")) {
            String element = sub.split("\\.")[0];
            String temp = String.format("%s()", element);
            return line.replace(element, temp);
        }
        return line.replace(sub, String.format("%s()", sub));
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
            handleConfigurationFieldsImport(entry.getKey(), entry.getValue());
        }
    }

    private void handleConfigurationFieldsImport(String className, String classPath) throws IOException {
        File cFile = new File(classPath);
        List<String> allLines = Files.readAllLines(cFile.toPath(), StandardCharsets.UTF_8);
        boolean found = false;
        int indexStart = 0;
        int indexOfConfMethod = 0;
        int indexEnd = 0;
        int index = 0;
        int amountOfCloses = 0;
        List<Integer> linesToRemove = new ArrayList<>();

        for(String line : allLines) {
                if(line.contains("@ConfigurationField")) {
                    indexStart = index;
                    found = true;
                    index++;
                    indexOfConfMethod = index;
                }
                else {
                    if(found) {
                        if(line.contains("{")) {
                            amountOfCloses++;
                        }
                        else if(line.contains("}")) {
                            amountOfCloses--;
                            if(amountOfCloses == 0) {
                                indexEnd = index;
                                List<String> methodLines = allLines.subList(indexStart, indexEnd);
                                configurationFields.add(createNewConfigurationFieldDetails(className, methodLines));

                                for(int i = indexStart; i <= indexEnd; i++) {
                                    linesToRemove.add(i + 2);
                                }
                                found = false;
                            }

                        }
                    }
                    index++;
                }
        }

        if(!linesToRemove.isEmpty()) {
            String methodSignatureLine = allLines.get(indexOfConfMethod);
            String methodCall = getMethodCallFromMethodLine(methodSignatureLine);
            replaceMethodCallWithNewConfField(methodCall, allLines);
            int counter = 0;
            indexOfConfMethod++;
            for(Integer lineIndex : linesToRemove) {
                allLines.remove(lineIndex - counter);
                counter++;
            }

            writeNewContentToFile(cFile, allLines);
        }

    }

    private void replaceMethodCallWithNewConfField(String methodCall, List<String> allLines) throws IOException {
        int index = 0;
        int searchedIndex = 0;
        for(String line : allLines) {
            if(line.contains(methodCall) && line.contains("=")) {
                searchedIndex = index;
                break;
            }
            index++;
        }
        allLines.add(searchedIndex, getNewMethodCallLine(allLines, searchedIndex, configurationFields.get(0).getFieldName()));
        allLines.remove(searchedIndex + 1);

        index = 0;
        for(String line : allLines) {
            if(line.contains("public class")) {
                searchedIndex = index;
            }
            index++;
        }
        ConfigurationFieldDetails configurationFieldDetails = this.configurationFields.get(0);
        this.configurationFields.remove(0);
        allLines.add(searchedIndex + 1, String.format("private String %s = null;\n", convertFieldNameToDataMemberName(configurationFieldDetails.getFieldName())));
        allLines.add(searchedIndex + 2, String.format("    @Autowired\n" +
                "    public void initProperty(@Value(\"${%s}\") String property) {\n" +
                "        this.%s = property;\n" +
                "    }", configurationFieldDetails.getFieldName(), convertFieldNameToDataMemberName(configurationFieldDetails.getFieldName())));

        String searchFileName = String.format("%s.java", configurationFieldDetails.getUnderClass());
        File file = FilesUtil.findFileByName(Manager.getInstance().getProjectsFile(), searchFileName);
        writeNewContentToFile(file, allLines);
    }

    private String getNewMethodCallLine(List<String> allLines, int searchedIndex, String fieldName) {
        String origLine = allLines.get(searchedIndex);
        String semiLine = origLine.split("=")[0];
        String convertedFieldName = convertFieldNameToDataMemberName(fieldName);
        return String.format("%s = this.%s;\n", semiLine, convertedFieldName);
    }

    private String convertFieldNameToDataMemberName(String fieldName) {
        String[] elements = fieldName.split("\\.");
        String sub = elements[1];
        String sub1 = sub.substring(0, 1);
        sub1 = sub1.toUpperCase();
        String sub2 = sub.substring(1, sub.length());
        return String.format("%s%s%s", elements[0], sub1, sub2);
    }

    private String getMethodCallFromMethodLine(String methodSignatureLine) {
        methodSignatureLine = methodSignatureLine.trim();
        String[] elements = methodSignatureLine.split(" ");
        return elements[2];
    }

    private ConfigurationFieldDetails createNewConfigurationFieldDetails(String className, List<String> methodLines) {
        String fieldName = null;
        String fileName = null;

        for(String line : methodLines) {
            if(line.contains("getProperty(")) {
                String sub = line.substring(line.indexOf("("), line.indexOf(")"));
                fieldName = sub.substring(2, sub.length() - 1);
            }
            if(line.contains("getResourceAsStream(")) {
                String[] elements = line.split("getResourceAsStream");
                String sub1 = elements[elements.length - 1];
                fileName = sub1.substring(sub1.indexOf("(") + 2, sub1.indexOf(")") - 1);
            }
        }

        ConfigurationFieldDetails configurationFieldDetails = new ConfigurationFieldDetails();
        configurationFieldDetails.setFieldName(fieldName);
        configurationFieldDetails.setFileName(fileName);
        configurationFieldDetails.setUnderClass(className);

        File confFile = FilesUtil.findFileByName(Manager.getInstance().getProjectsFile(), fileName);
        String path = confFile.getAbsolutePath();
        String semiPath = path.split("resources")[1];

        configurationFieldDetails.setPath(semiPath);
        return configurationFieldDetails;
    }

    private void handleInternalClassScan(String createsClass, String classPath) throws IOException {
        boolean ignore = false;
        Map<String, String> replaceLineWithNew = new HashMap<>();
        File cFile = new File(classPath);
        List<String> allLines = Files.readAllLines(cFile.toPath(), StandardCharsets.UTF_8);
        int index = 0;
        List<Integer> linesToRemove = new ArrayList<>();

        for(String line : allLines) {
            if(line.contains("@BlackList")) {
                if(lineUnderIsMethod(line, allLines)) {
                    linesToRemove.add(index);
                    ignore = true;
                }
            }
            else {
                if(ignore) {
                    if(line.contains("}")) {
                        ignore = false;
                        continue;
                    }
                }
                else {
                    if(line.contains("new") && !line.contains("Exception")) {
                        if(checkIFNewInstanceIsBlackList(allLines, index)) {
                            linesToRemove.add(index - 1);
                            index++;
                            continue;
                        }

                        BeanDetails beanDetails = findAllInstanceElements(line, allLines);

                        if(isInternalClass(beanDetails.getClassName())) {
                            checkIfBeanIsDataStructureType(line, beanDetails);
                            checkIfBeanIsInsideTryCatchOrMethodWithExceptionThrowing(allLines, beanDetails, index);
                            if(isLazyBean(beanDetails)) {
                                beanDetails.setIsLazy(true);
                            }
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
                }

                index++;
            }

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

    private void checkIfBeanIsInsideTryCatchOrMethodWithExceptionThrowing(List<String> allLines, BeanDetails beanDetails, int indexOfBeanLine) {
        int indexOfStartTryState = 0;
        int indexOfEndTryState = 0;
        int index = 0;
        boolean found = false;
        boolean answer = false;

        for(String line : allLines) {
            if(line.contains("try {")) {
                indexOfStartTryState = index;
                found = true;
                index++;
            }
            else {
                index++;
                if(found) {
                    if(line.contains("}")) {
                        indexOfEndTryState = index;
                        if(indexOfStartTryState < indexOfBeanLine && indexOfBeanLine < indexOfEndTryState) {
                            answer = true;
                        }
                        else {
                            found = false;
                        }
                    }
                }
            }
        }
        if(!answer) {
            answer = checkIfMethodOfLineThrowsException(allLines, indexOfBeanLine);
        }

        beanDetails.setInsideExceptionThrowing(answer);
    }

    private boolean checkIfMethodOfLineThrowsException(List<String> allLines, int indexOfBeanLine) {
        int indexOfStart = 0;
        int indexOfEnd = 0;
        int index = 0;
        boolean found = false;


        for(String line : allLines) {
            if(!line.contains("class") && (line.contains("public") || line.contains("private") || line.contains("protected")) && (line.contains("(") && line.contains(")") && line.contains("{"))) {
                indexOfStart = index;
                found = true;
                index++;
            }
            else {
                index++;
                if(found) {
                    if(line.contains("}")) {
                        indexOfEnd = index;
                        if(indexOfStart < indexOfBeanLine && indexOfBeanLine < indexOfEnd && allLines.get(indexOfStart).contains("throws Exception")) {
                            return true;
                        }
                        else {
                            found = false;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void checkIfBeanIsDataStructureType(String line, BeanDetails beanDetails) {
        if(isDataStructureInstance(line)) {
            beanDetails.setDataStructure(true);
        }
        else {
            beanDetails.setDataStructure(false);
        }
    }

    private boolean lineUnderIsMethod(String annotateLine, List<String> allLines) {
        int index = 0;
        for(String line : allLines) {
            if(line.equals(annotateLine)) {
                   break;
            }
            index++;
        }
        if(allLines.get(index).contains("new")) {
            return false;
        }
        else {
            return true;
        }
    }

    private boolean checkIfInstancePrototype(List<String> allLines, int index) {
        String lineBeforeCreation = allLines.get(index - 1);

        return lineBeforeCreation.contains("@prototype") || lineBeforeCreation.contains("@Prototype");
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

        if(elements.length == 6) {
            if(elements[1].contains("<")) {
                className = elements[1].substring(elements[1].indexOf("<") + 1, elements[1].indexOf(">"));
            }
            else {
                className = elements[1];
            }
           instanceName = elements[2];

           return new BeanDetails(className, instanceName, elements[5].split("\\(")[0], line.substring(line.indexOf("(") + 1, line.indexOf(")")), line, allLines);
        }
        else if(elements.length == 5) {
            if(elements[0].contains("<")) {
                className = elements[0].substring(elements[0].indexOf("<") + 1, elements[0].indexOf(">"));
            }
            else {
                className = elements[0];
            }
            instanceName = elements[1];
            return new BeanDetails(className, instanceName, elements[4].split("\\(")[0], line.substring(line.indexOf("(") + 1, line.indexOf(")")), line, allLines);
        }
        else if(elements.length == 3) {
            if(elements[2].contains("<")) {
                className = elements[2].substring(elements[2].indexOf("<") + 1, elements[2].indexOf(">"));
            }
            else {
                className = elements[2].substring(0, elements[2].indexOf("("));
            }
            instanceName = changeFirstLetterToLower(className);
            return new BeanDetails(className, instanceName, elements[2].split("\\(")[0], line.substring(line.indexOf("(") + 1, line.indexOf(")")), line, allLines);
        }
        //handle data structure
        className = elements[0];
        instanceName = elements[1];
        try {
            if(elements.length <=4) {
                implName = elements[3].split("\\(")[0];
            }
            else {
                implName = elements[4].split("\\(")[0];
            }
            constructorArgs = line.substring(line.indexOf("(") + 1, line.indexOf(")"));

            return new BeanDetails(className, instanceName, implName, constructorArgs, line, allLines);
        }
        catch (Exception ex) {
            return new BeanDetails(className, instanceName, null, null, line, allLines);
        }

    }

    private String changeFirstLetterToLower(String className) {
        String firstLetter = className.substring(0, 1);
        firstLetter = firstLetter.toLowerCase();
        String temp = className.substring(1, className.length() - 1);
        return String.format("%s%s", firstLetter, temp);
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
            if(isCreationWithReturnStatement(beanDetails)) {
                return String.format("      return context.getBean(\"%s\", List.class);", beanDetails.getClassName(), beanDetails.getInstanceName(), beanDetails.getInstanceName(), beanDetails.getClassName().split("<")[0]);
            }
            else {
                return String.format("      List<%s> %s = context.getBean(\"%s\", List.class);", beanDetails.getClassName(), beanDetails.getInstanceName(), beanDetails.getInstanceName(), beanDetails.getClassName().split("<")[0]);
            }
        }
        else {
            if(isCreationWithReturnStatement(beanDetails)) {
                return String.format("      return context.getBean(\"%s\", %s.class);", beanDetails.getClassName(), beanDetails.getClassName());
            }
            else {
                return String.format("      %s %s = context.getBean(\"%s\", %s.class);", beanDetails.getClassName(), beanDetails.getInstanceName(), beanDetails.getInstanceName(), beanDetails.getClassName());
            }

        }

    }

    private boolean isCreationWithReturnStatement(BeanDetails beanDetails) {
        String line = beanDetails.getLine().trim();
        String[] elements = line.split(" ");
        if(elements[0].contains("return")) {
            return true;
        }
        return false;
    }

    private boolean isInternalClass(String className) {
        if(isDataStructureInstance(className)) {
            if(checkIfClassNameExist(className)) {
                if(checkIfListTypeIsInternal(className)) {
                    return true;
                }
            }
            else {

            }

        }
        return checkIfSimpleTypeISInternal(className);
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

    private boolean isDataStructureInstance(String line) {
        if(line.contains("List")) {
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
