package ProjectScanning;

import java.io.File;
import java.util.List;

public class BeanDetails {
    private String ClassName;
    private String instanceName;
    private String implName;
    private String constructorArgs;
    private String createsClass;
    private String line;
    private String configurationFilePath;
    private List<String> allLinesInCreatedClass;
    private boolean isInsideExceptionThrowing;
    private boolean isLazy;
    private boolean isDataStructure;
    private boolean isPrototypeInst;

    public BeanDetails(String className, String instanceName, String implName, String constructorArgs, String line, List<String> allLine) {
        ClassName = className;
        this.instanceName = instanceName;
        this.implName = implName;
        this.constructorArgs = constructorArgs;
        this.line = line;
        this.allLinesInCreatedClass = allLine;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getImplName() {
        return implName;
    }

    public void setImplName(String implName) {
        this.implName = implName;
    }

    public String getConstructorArgs() {
        return constructorArgs;
    }

    public void setConstructorArgs(String constructorArgs) {
        this.constructorArgs = constructorArgs;
    }

    public String getCreatedUnderClass() {
        return createsClass;
    }

    public void setCreatedUnderClass(String createdUnderClass) {
        this.createsClass = createdUnderClass;
    }

    public String getConfigurationFilePath() {
        return configurationFilePath;
    }

    public void setConfigurationFilePath(String configurationFile) {
        this.configurationFilePath = configurationFile;
    }

    public boolean isDataStructure() {
        return isDataStructure;
    }

    public void setDataStructure(boolean dataStructure) {
        isDataStructure = dataStructure;
    }

    public boolean isPrototypeInst() {
        return isPrototypeInst;
    }

    public void setPrototypeInst(boolean prototypeInst) {
        isPrototypeInst = prototypeInst;
    }

    public String getLine() {
        return line;
    }

    public List<String> getAllLines() {
        return allLinesInCreatedClass;
    }

    public void setIsLazy(boolean isLazy) {
        this.isLazy = isLazy;
    }

    public boolean getIsLazy() {
        return isLazy;
    }

    public void setInsideExceptionThrowing(boolean isInsideExceptionThrowing) {
        this.isInsideExceptionThrowing = isInsideExceptionThrowing;
    }

    public boolean getInsideExceptionThrowing() {
        return isInsideExceptionThrowing;
    }
}
