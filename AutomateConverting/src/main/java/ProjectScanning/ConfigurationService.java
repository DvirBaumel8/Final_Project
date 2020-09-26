package ProjectScanning;

import FilesUtil.FilesUtil;
import Manager.Manager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationService {
    private List<ConfigurationFieldDetails> configurationFields;
    private ScannerUtils scannerUtils;

    public ConfigurationService() {
        configurationFields = new ArrayList<>();
        scannerUtils = new ScannerUtils();
    }

    public void handleConfigurationFieldsImportToClass(String className, String classPath) throws IOException {
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

            addAutoWiredAndValueImportsStatements(allLines, cFile);

            scannerUtils.writeNewContentToFile(cFile, allLines);
        }

    }

    private void addAutoWiredAndValueImportsStatements(List<String> allLines, File classFile) throws IOException {
        int indexToInject = findIndexToInjectImports(allLines);
        allLines.add(indexToInject, "import org.springframework.beans.factory.annotation.Autowired;");
        indexToInject++;
        allLines.add(indexToInject, "import org.springframework.beans.factory.annotation.Value;");

        scannerUtils.writeNewContentToFile(classFile, allLines);
    }

    private int findIndexToInjectImports(List<String> allLines) {
        int index = 0;
        for(String line : allLines) {
            if(line.contains("import org.springframework.context.ApplicationContext")) {
                return index;
            }
            index++;
        }
        return 4;
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
        scannerUtils.writeNewContentToFile(file, allLines);
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

    public void addConfigurationPropertySource(File mainConf) throws IOException {
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
            scannerUtils.writeNewContentToFile(mainConf, allLines);
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

        scannerUtils.writeNewContentToFile(mainConf, lines);
    }

}
