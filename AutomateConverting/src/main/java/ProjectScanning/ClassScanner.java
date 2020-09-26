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
import java.util.Set;

public class ClassScanner {
    private ScanChecker scanChecker;
    private ScannerUtils scannerUtils;

    public ClassScanner() {
        scanChecker = new ScanChecker();
        scannerUtils = new ScannerUtils();
    }

    public void handleInternalClassScan(String createsClass, String classPath, Map<String, InnerClassDetails> classNameToClassDetails, Map<String, BeanDetails> instanceNameToBeanDetails, Set<String> classesToAddAppCox) throws IOException {
        boolean ignore = false;
        Map<String, String> replaceLineWithNew = new HashMap<>();
        File cFile = new File(classPath);
        List<String> allLines = Files.readAllLines(cFile.toPath(), StandardCharsets.UTF_8);
        int index = 0;
        List<Integer> linesToRemove = new ArrayList<>();

        for(String line : allLines) {
            if(line.contains("@BlackList")) {
                if(scanChecker.isLineUnderIsMethod(line, allLines)) {
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
                        if(scanChecker.checkIFNewInstanceIsBlackList(allLines, index)) {
                            linesToRemove.add(index);
                            index++;
                            continue;
                        }

                        BeanDetails beanDetails = findAllInstanceElements(line, allLines);

                        if(scanChecker.isInternalClass(beanDetails.getClassName(), classNameToClassDetails)) {
                            scanChecker.checkIfBeanIsDataStructureType(line, beanDetails);
                            scanChecker.checkIfBeanIsInsideTryCatchOrMethodWithExceptionThrowing(allLines, beanDetails, index);
                            if(isLazyBean(beanDetails, classNameToClassDetails)) {
                                beanDetails.setIsLazy(true);
                            }
                            beanDetails.setConfigurationFilePath(classNameToClassDetails.get("MainConfiguration").getPath());
                            beanDetails.setCreatedUnderClass(createsClass);
                            classesToAddAppCox.add(createsClass);
                            instanceNameToBeanDetails.put(beanDetails.getInstanceName(), beanDetails);
                            replaceLineWithNew.put(line, scannerUtils.getBeanAccessStrFromBeanDetails(beanDetails));
                            beanDetails.setPrototypeInst(scanChecker.checkIfInstancePrototype(allLines, index));
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
            scannerUtils.replaceLines(item.getKey(), item.getValue(), allLines);
        }
        scannerUtils.writeNewContentToFile(cFile, allLines);
    }

    private boolean isLazyBean(BeanDetails beanDetails, Map<String, InnerClassDetails> classNameToClassDetails) throws IOException {
        //Add lazy annotation to class in case class constructor is using class static method.

        List<String> lines = getAllLinesByClassName(beanDetails.getClassName(), classNameToClassDetails);

        lines = minimizeLinesToConstructorOnly(beanDetails, lines);

        for(String line : lines) {
            if(scanChecker.isLineUsingStaticMethodOfInnerClass(line, classNameToClassDetails)) {
                return true;
            }
        }
        return false;
    }

    private List<String> getAllLinesByClassName(String className, Map<String, InnerClassDetails> classNameToClassDetails) throws IOException {
        File file = new File(classNameToClassDetails.get(className).getPath());
        return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
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
            if(temp.contains("private") || temp.contains("protected")) {
                if(elements[1].contains("<")) {
                    className = elements[1].substring(elements[1].indexOf("<") + 1, elements[1].indexOf(">"));
                }
                else {
                    className = elements[1];
                }
                instanceName = elements[2];

                return new BeanDetails(className, instanceName, elements[5].split("\\(")[0], line.substring(line.indexOf("(") + 1, line.indexOf(")")), line, allLines);
            }
            else {
                className = elements[0];
                instanceName = elements[1];
                return new BeanDetails(className, instanceName, className, temp.substring(temp.indexOf("(") + 1, temp.indexOf(")")), line, allLines);
            }

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
            instanceName = scannerUtils.changeFirstLetterToLower(className);
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

    public boolean scanClass(File currFile) throws IOException {
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
