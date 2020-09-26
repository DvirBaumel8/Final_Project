package ProjectScanning;

import FilesUtil.FilesUtil;
import Manager.Manager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanMethodService {
    private ScanChecker scanChecker;
    private ScannerUtils scannerUtils;
    private ConfigurationService configurationService;
    private Map<String, Boolean> confFileNameToISBeanAdded;

    public BeanMethodService() {
        scanChecker = new ScanChecker();
        scannerUtils = new ScannerUtils();
        configurationService = new ConfigurationService();
        confFileNameToISBeanAdded = new HashMap<>();
        confFileNameToISBeanAdded.put("MainConfiguration.java", false);
    }

    public void createBeanMethods(Map<String, InnerClassDetails> classNameToClassDetails, Map<String, BeanDetails> instanceNameToBeanDetails) throws IOException {
        for(Map.Entry<String, BeanDetails> entry : instanceNameToBeanDetails.entrySet()) {
            File confFile = scannerUtils.getConfigurationFile(classNameToClassDetails, entry.getValue().getCreatedUnderClass());
            addScopeImportToConfFile(confFile);
            String cBeanMethodStr = createBeanMethodStr(entry.getKey(), entry.getValue(), classNameToClassDetails, instanceNameToBeanDetails);
            addMethodToFile(confFile, cBeanMethodStr);
            if(isListObj(entry)) {
                addListImportsToConfFile(confFile);
            }
        }
        configurationService.addConfigurationPropertySource(scannerUtils.getConfigurationFile(classNameToClassDetails, "MainConfiguration"));
    }

    private boolean isListObj(Map.Entry<String, BeanDetails> entry) {
        return entry.getValue().getImplName().contains("List");
    }

    public String createBeanMethodStr(String instanceName, BeanDetails beanDetails, Map<String, InnerClassDetails> classNameToClassDetails, Map<String, BeanDetails> instanceNameToBeanDetails) throws IOException {
        String origClass = beanDetails.getClassName();
        StringBuilder str = new StringBuilder();
        str.append("@Bean\n");

        if(beanDetails.getIsLazy()) {
            str.append("@Lazy\n");
            addLazyImportToConfFile(scannerUtils.getConfigurationFile(classNameToClassDetails, beanDetails.getClassName()));
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
        else if(scanChecker.isConstructorArgsIsBean(constructorArgs, instanceNameToBeanDetails)) {
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

        String settersStr = getSettersStrs(beanDetails, classNameToClassDetails, instanceNameToBeanDetails);
        str.append(settersStr);
        str.append(String.format("return %s;\n}\n", instanceName));

        return str.toString();
    }

    private String getSettersStrs(BeanDetails beanDetails, Map<String, InnerClassDetails> classNameToClassDetails, Map<String, BeanDetails> instanceNameToBeanDetails) throws IOException {
        File file = null;
        String createdUnderClass = beanDetails.getCreatedUnderClass();
        for(Map.Entry<String, InnerClassDetails> entry : classNameToClassDetails.entrySet()) {
            if(createdUnderClass.equals(entry.getKey())) {
                file = new File(entry.getValue().getPath());
                break;
            }
        }

        return getSettersStrs(file, beanDetails.getInstanceName(), instanceNameToBeanDetails, classNameToClassDetails);
    }

    private String getSettersStrs(File file, String beanName, Map<String, BeanDetails> instanceNameToBeanDetails, Map<String, InnerClassDetails> classNameToClassDetails) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        StringBuilder str = new StringBuilder();
        List<String> linesToRemove = new ArrayList<>();

        for(String line : lines) {
            if(line.contains(beanName + ".set")) {
                if(scanChecker.checkIfVariableIsBean(line, instanceNameToBeanDetails)) {
                    if(isBeanFromTheSameConfFile(line, beanName, instanceNameToBeanDetails, classNameToClassDetails)) {
                        String temp = getLineWithBeanAccess(line);
                        str.append(temp);
                        str.append(System.lineSeparator());
                        linesToRemove.add(line);
                    }
                    else {
                        String temp = getLineWithBeanAccessFromOtherConfFile(line, findConfFileNameOfVariable(line, instanceNameToBeanDetails, classNameToClassDetails));
                        str.append(temp);
                        str.append(System.lineSeparator());
                        linesToRemove.add(line);
                        addAutoWiredStatementToConfFile(line, beanName, instanceNameToBeanDetails, classNameToClassDetails);
                    }

                }
                else if(scanChecker.checkIfNumberStringOrBoolean(line)) {
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
        scannerUtils.writeNewContentToFile(file, lines);

        return str.toString();
    }

    private void addAutoWiredStatementToConfFile(String line, String beanName, Map<String, BeanDetails> instanceNameToBeanDetails, Map<String, InnerClassDetails> classNameToClassDetails) throws IOException {
        BeanDetails beanDetails = instanceNameToBeanDetails.get(beanName);
        InnerClassDetails innerClassDetails = classNameToClassDetails.get(beanDetails.getCreatedUnderClass());
        String packageName = innerClassDetails.getPackageName();
        File confFile;
        if(packageName == null) {
            confFile = FilesUtil.findFileByName(Manager.getInstance().getProjectsFile(), "MainConfiguration.java");
        }
        else {
            confFile = FilesUtil.findFileByName(Manager.getInstance().getProjectsFile(), String.format("%sConfiguration.java", scannerUtils.replaceFirstLetterToUpperCase(packageName)));
        }
        addAutoWiredStatementToConfFile(confFile, line, instanceNameToBeanDetails, classNameToClassDetails);
    }

    private void addAutoWiredStatementToConfFile(File file, String line, Map<String, BeanDetails> instanceNameToBeanDetails, Map<String, InnerClassDetails> classNameToClassDetails) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        String fileName = findConfFileNameOfVariable(line, instanceNameToBeanDetails, classNameToClassDetails);
        String temp2 = fileName.substring(0, fileName.length() - 1);
        String temp = scannerUtils.replaceFirstLetterToUpperCase(temp2);

        int index = 0;
        for(String currLine : lines) {
            if(currLine.contains("public class ")) {
                index++;
                break;
            }
            index++;
        }
        String statement = String.format("private %s %s", temp, temp2);
        if(!isStatementExists(file, statement)) {
            lines.add(index, "@Autowired\n");
            lines.add(index + 1, String.format("private %s %s;\n", temp, temp2));

            index = 0;

            for(String currLine : lines) {
                if(currLine.contains("import org.springframework.context.annotation.Configuration;")) {
                    index++;
                    break;
                }
                index++;
            }
            lines.add(index, "import org.springframework.beans.factory.annotation.Autowired;\n");
            scannerUtils.writeNewContentToFile(file, lines);
        }

    }

    private boolean isStatementExists(File file, String statement) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        for(String line : lines) {
            if(line.contains(statement)) {
                return true;
            }
        }
        return false;
    }

    private String findConfFileNameOfVariable(String line, Map<String, BeanDetails> instanceNameToBeanDetails, Map<String, InnerClassDetails> classNameToClassDetails) {
        int left = line.indexOf("(") + 1;
        int right = line.indexOf(")");
        String sub = line.substring(left, right);
        String variable = sub;
        if(sub.contains(".")) {
            variable = sub.split("\\.")[0];
        }

        BeanDetails beanDetailsVariable = instanceNameToBeanDetails.get(variable);
        String createdUnderClassVariable = beanDetailsVariable.getCreatedUnderClass();
        InnerClassDetails innerClassDetailsVariable = classNameToClassDetails.get(createdUnderClassVariable);
        String packageName = innerClassDetailsVariable.getPackageName();
        return String.format("%sConfiguration.", packageName);
    }

    private String getLineWithBeanAccessFromOtherConfFile(String line, String confFileAccess) {
        String temp = getLineWithBeanAccess(line);
        String sub = temp.substring(temp.indexOf("(") + 1, temp.indexOf(";") - 1);
        return temp.replace(sub, String.format("%s%s", confFileAccess, sub));
    }

    private boolean isBeanFromTheSameConfFile(String line, String beanName, Map<String, BeanDetails> instanceNameToBeanDetails, Map<String, InnerClassDetails> classNameToClassDetails) {
        int left = line.indexOf("(") + 1;
        int right = line.indexOf(")");
        String sub = line.substring(left, right);
        String variable = sub;
        if(sub.contains(".")) {
            variable = sub.split("\\.")[0];
        }

        BeanDetails beanDetailsVariable = instanceNameToBeanDetails.get(variable);
        String createdUnderClassVariable = beanDetailsVariable.getCreatedUnderClass();
        InnerClassDetails innerClassDetailsVariable = classNameToClassDetails.get(createdUnderClassVariable);

        BeanDetails beanDetails = instanceNameToBeanDetails.get(beanName);
        String createdUnderClass = beanDetails.getCreatedUnderClass();
        InnerClassDetails innerClassDetails = classNameToClassDetails.get(createdUnderClass);

        if(innerClassDetailsVariable.getPackageName() == null && innerClassDetails.getPackageName() == null) {
            return true;
        }
        else if(innerClassDetailsVariable.getPackageName() != null && innerClassDetails.getPackageName() == null) {
            return false;
        }
        else {
            return true;
        }
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

    private void addLazyImportToConfFile(File mainConf) throws IOException {
        List<String> lines = Files.readAllLines(mainConf.toPath(), StandardCharsets.UTF_8);
        int index = 0;
        for(int i =0; i < lines.size(); i++) {
            if(lines.get(i).contains("org.springframework.context.annotation.Bean"));
            index = i + 1;
            break;
        }
        lines.add(index, "import org.springframework.context.annotation.Lazy;");

        scannerUtils.writeNewContentToFile(mainConf, lines);
    }

    public void addScopeImportToConfFile(File mainConf) throws IOException {
        List<String> lines = Files.readAllLines(mainConf.toPath(), StandardCharsets.UTF_8);
        int index = 0;
        for(int i =0; i < lines.size(); i++) {
            if(lines.get(i).contains("org.springframework.context.annotation.Bean"));
            index = i + 1;
            break;
        }
        lines.add(index, "import org.springframework.context.annotation.Scope;");

        scannerUtils.writeNewContentToFile(mainConf, lines);
    }

    public void addListImportsToConfFile(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        int index = lines.indexOf("import org.springframework.context.annotation.Configuration;");
        index++;

        if(!lines.contains("import java.util.List;")) {
            lines.add(index, "import java.util.List;");
        }
        if(!lines.contains("import java.util.ArrayList;")) {
            lines.add(index, "import java.util.ArrayList;");
        }

        scannerUtils.writeNewContentToFile(file, lines);
    }

    private void addMethodToFile(File mainConf, String cBeanMethodStr) throws IOException {
        if(!confFileNameToISBeanAdded.get(mainConf.getName())) {
            addImportBeansToConf(mainConf);
            confFileNameToISBeanAdded.put(mainConf.getName(), true);
        }
        List<String> lines = Files.readAllLines(mainConf.toPath(), StandardCharsets.UTF_8);

        int index = 0;

        for(String line : lines) {
            if(line.contains("public class ")) {
                index += 1;
                break;
            }
            index++;
        }
        lines.add(index, cBeanMethodStr);
        scannerUtils.writeNewContentToFile(mainConf, lines);
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

        scannerUtils.writeNewContentToFile(mainConf, lines);
    }

    public void addNewConfFile(String confFileName) {
        confFileNameToISBeanAdded.put(confFileName, false);
    }


}
