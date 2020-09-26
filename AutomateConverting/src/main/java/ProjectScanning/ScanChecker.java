package ProjectScanning;

import java.util.List;
import java.util.Map;

public class ScanChecker {

    public boolean isCreationWithReturnStatement(BeanDetails beanDetails) {
        String line = beanDetails.getLine().trim();
        String[] elements = line.split(" ");
        if(elements[0].contains("return")) {
            return true;
        }
        return false;
    }

    public boolean isInternalClass(String className, Map<String, InnerClassDetails> classNameToClassDetails) {
        if(isDataStructureInstance(className)) {
            if(checkIfClassNameExist(className)) {
                if(checkIfListTypeIsInternal(className, classNameToClassDetails)) {
                    return true;
                }
            }
            else {

            }

        }
        return checkIfSimpleTypeISInternal(className, classNameToClassDetails);
    }

    private boolean checkIfClassNameExist(String className) {
        String str = className.substring(className.indexOf("<") + 1, className.indexOf(">"));
        return str.length() > 0;
    }

    private boolean checkIfListTypeIsInternal(String className, Map<String, InnerClassDetails> classNameToClassDetails) {
        String str = className.substring(className.indexOf("<") + 1, className.indexOf(">"));
        return checkIfSimpleTypeISInternal(str, classNameToClassDetails);
    }

    private boolean checkIfSimpleTypeISInternal(String className, Map<String, InnerClassDetails> classNameToClassDetails) {
        for(Map.Entry<String, InnerClassDetails> entry : classNameToClassDetails.entrySet()) {
            if(entry.getKey().equals(className)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDataStructureInstance(String line) {
        if(line.contains("List")) {
            return true;
        }
        return false;
    }

    public boolean checkIfVariableIsBean(String line, Map<String, BeanDetails> instanceNameToBeanDetails) {
        int left = line.indexOf("(") + 1;
        int right = line.indexOf(")");
        String sub = line.substring(left, right);
        if(sub.contains(".")) {
            String element = sub.split("\\.")[0];
            return instanceNameToBeanDetails.containsKey(element);
        }
        return instanceNameToBeanDetails.containsKey(sub);
    }

    public boolean checkIfNumberStringOrBoolean(String line) {
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

    public boolean checkIFNewInstanceIsBlackList(List<String> allLines, int index) {
        String lineBeforeCreation = allLines.get(index);

        return lineBeforeCreation.contains("@BlackList");
    }

    public void checkIfBeanIsDataStructureType(String line, BeanDetails beanDetails) {
        if(isDataStructureInstance(line)) {
            beanDetails.setDataStructure(true);
        }
        else {
            beanDetails.setDataStructure(false);
        }
    }

    public void checkIfBeanIsInsideTryCatchOrMethodWithExceptionThrowing(List<String> allLines, BeanDetails beanDetails, int indexOfBeanLine) {
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

    public boolean isLineUnderIsMethod(String annotateLine, List<String> allLines) {
        int index = 0;
        for(String line : allLines) {
            if(line.equals(annotateLine)) {
                break;
            }
            index++;
        }
        if(allLines.get(index + 1).contains("new")) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean checkIfInstancePrototype(List<String> allLines, int index) {
        String lineBeforeCreation = allLines.get(index - 1);

        return lineBeforeCreation.contains("@prototype") || lineBeforeCreation.contains("@Prototype");
    }

    public boolean isLineUsingStaticMethodOfInnerClass(String line, Map<String, InnerClassDetails> classNameToClassDetails) {
        for(Map.Entry<String, InnerClassDetails> entry : classNameToClassDetails.entrySet()) {
            if(line.contains(entry.getKey()) && line.contains(".") && line.contains("()")) {
                return true;
            }
        }
        return false;
    }

    public boolean isConstructorArgsIsBean(String constructorArgs, Map<String, BeanDetails> instanceNameToBeanDetails) {
        return instanceNameToBeanDetails.containsKey(constructorArgs);
    }
}
