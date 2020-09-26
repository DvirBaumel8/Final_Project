package ProjectScanning;

import FilesUtil.FilesUtil;
import Manager.Manager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScannerUtils {
    private ScanChecker scanChecker;
    private FileUtils fileUtils;

    public ScannerUtils() {
        scanChecker = new ScanChecker();
        fileUtils = new FileUtils();
    }

    public String changeFirstLetterToLower(String className) {
        String firstLetter = className.substring(0, 1);
        firstLetter = firstLetter.toLowerCase();
        String temp = className.substring(1, className.length() - 1);
        return String.format("%s%s", firstLetter, temp);
    }


    public void replaceLines(String lineToRemove, String lineToWrite, List<String> allLines) {
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

    public String getBeanAccessStrFromBeanDetails(BeanDetails beanDetails) {
        if(beanDetails.isDataStructure()) {
            if(scanChecker.isCreationWithReturnStatement(beanDetails)) {
                return String.format("      return context.getBean(\"%s\", List.class);", beanDetails.getClassName(), beanDetails.getInstanceName(), beanDetails.getInstanceName(), beanDetails.getClassName().split("<")[0]);
            }
            else {
                if(beanDetails.getLine().contains("private") || beanDetails.getLine().contains("protected")) {
                    String accessModifier = beanDetails.getLine().trim().split(" ")[0];
                    return String.format("      %s List<%s> %s = context.getBean(\"%s\", List.class);", accessModifier, beanDetails.getClassName(), beanDetails.getInstanceName(), beanDetails.getInstanceName(), beanDetails.getClassName().split("<")[0]);
                }
                else {
                    return String.format("      List<%s> %s = context.getBean(\"%s\", List.class);", beanDetails.getClassName(), beanDetails.getInstanceName(), beanDetails.getInstanceName(), beanDetails.getClassName().split("<")[0]);
                }

            }
        }
        else {
            if(scanChecker.isCreationWithReturnStatement(beanDetails)) {
                return String.format("      return context.getBean(\"%s\", %s.class);", beanDetails.getClassName(), beanDetails.getClassName());
            }
            else {
                return String.format("      %s %s = context.getBean(\"%s\", %s.class);", beanDetails.getClassName(), beanDetails.getInstanceName(), beanDetails.getInstanceName(), beanDetails.getClassName());
            }
        }
    }

    public String getDisplayClassName(String name) {
        String[] names = name.split("\\.");
        return names[0];
    }

    public void writeNewContentToFile(File mainConf, List<String> lines) throws IOException {
        PrintStream out = new PrintStream(new FileOutputStream(mainConf));
        StringBuilder str = new StringBuilder();
        for(String line : lines) {
            str.append(line);
            str.append(System.lineSeparator());
        }
        out.write(str.toString().getBytes());
    }

    public void handleAppCtxWriteToNeededClasses(Map<String, InnerClassDetails> classNameToClassDetails, Set<String> classesToAddAppCox) throws IOException {
        for(Map.Entry<String, InnerClassDetails> entry : classNameToClassDetails.entrySet()) {
            for(String clazz : classesToAddAppCox) {
                if(clazz.equals(entry.getKey())) {
                    addAppCtxToClass(clazz, entry.getValue());
                }
            }
        }
    }

    private void addAppCtxToClass(String clazz, InnerClassDetails innerClassDetails) throws IOException {
        File file = new File(innerClassDetails.getPath());
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        int index = 0;

        for(String line : lines) {
            if(line.contains(String.format("class %s", clazz))) {
                index++;
                break;
            }
            index++;
        }
        if(innerClassDetails.getPackageName() == null) {
            lines.add(index, "   private static ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);");
        }
        else {
            String packageConfName = replaceFirstLetterToUpperCase(innerClassDetails.getPackageName());
            lines.add(index, String.format("   private static ApplicationContext context = new AnnotationConfigApplicationContext(%sConfiguration.class);", packageConfName));
        }

        index -= 1;
        lines.add(index, "import org.springframework.context.ApplicationContext;\n" +
                "import org.springframework.context.annotation.AnnotationConfigApplicationContext;");
        writeNewContentToFile(file, lines);
    }

    public File getConfigurationFile(Map<String, InnerClassDetails> classNameToClassDetails, String className) {
        InnerClassDetails innerClassDetails = classNameToClassDetails.get(className);
        String packageName = innerClassDetails.getPackageName();
        if(packageName == null) {
            return new File(classNameToClassDetails.get("MainConfiguration").getPath());
        }
        else {
            return new File(classNameToClassDetails.get(String.format("%sConfiguration", replaceFirstLetterToUpperCase(packageName))).getPath());
        }
    }

    public String getDisplayPackageName(String name) {
        String[] names = name.split("\\.");
        return names[0];
    }

    public void populatePackageConfigurationFile(File packageConfiguration, String packageName) throws IOException {
        PrintStream out = new PrintStream(new FileOutputStream(packageConfiguration));
        String mainConfFileStr = getPackageStringToConfigurationFile(packageName);
        out.write(mainConfFileStr.getBytes());

    }

    private String getPackageStringToConfigurationFile(String packageName) {
        return String.format("package %s;\n" +
                "import org.springframework.context.annotation.Configuration;\n" +
                "\n" +
                "@Configuration\n" +
                "public class %sConfiguration {\n" +
                "    \n" +
                "}", packageName, replaceFirstLetterToUpperCase(packageName));
    }

    public String replaceFirstLetterToUpperCase(String name) {
        String temp = name.substring(1, name.length());
        String firstLetter = name.substring(0, 1);
        String upperCaseFirstLetter = firstLetter.toUpperCase();
        return String.format("%s%s", upperCaseFirstLetter, temp);
    }

    public void addImportConfigurationToMainConf(String packageConfName, String packageName) throws IOException {
        File mainConfigurationFile = FilesUtil.findFileByName(Manager.getInstance().getProjectsFile(), "MainConfiguration.java");
        List<String> lines = Files.readAllLines(mainConfigurationFile.toPath(), StandardCharsets.UTF_8);
        handleImportOfMainConfigurationOfPackageConfiguration(mainConfigurationFile, lines, packageConfName, packageName);

    }

    private void handleImportOfMainConfigurationOfPackageConfiguration(File mainConfigurationFile, List<String> lines, String packageConfName, String packageName) throws IOException {
        int index = 0;
        for(String line : lines) {
            if(line.contains("@Configuration")) {
                index++;
                break;
            }
            index++;
        }

        String line = lines.get(index);
        if(!line.contains("@Import")) {
            lines.add(index, String.format("@Import(%s.class)", packageConfName));
        }
        else {
            String newImportLine = getNewImportLineWithNewPackageConfiguration(line, packageConfName);
            lines.remove(index);
            lines.add(index, newImportLine);
        }

        addImportOfImport(lines);
        addImportToNewConfigurationFile(lines, packageConfName, packageName);
        writeNewContentToFile(mainConfigurationFile, lines);
    }

    private void addImportToNewConfigurationFile(List<String> lines, String packageConfName, String packageName) {
        int index = 0;
        for(String line : lines) {
            if(line.contains("import org.springframework.context.annotation.Import;")) {
                index++;
                break;
            }
            index++;
        }
        lines.add(index, String.format("import %s.%s;", packageName, packageConfName));
    }

    private void addImportOfImport(List<String> lines) {
        int index = 0;
        for(String line : lines) {
            if(line.contains("import org.springframework.context.annotation.Configuration;")) {
                index++;
                break;
            }
            index++;
        }
        lines.add(index, "import org.springframework.context.annotation.Import;\n");
    }

    private String getNewImportLineWithNewPackageConfiguration(String line, String packageConfName) {
        String sub = line.substring(line.indexOf("("), line.indexOf(")"));
        return line.replace(sub, String.format("%s, %s.class", sub, packageConfName));
    }

}
