package ProjectScanning;

public class NewBeanDetails {
    private String createdUnderClass;
    private String creationLine;
    private String beanName;
    private String origClass;

    public NewBeanDetails(String createdUnderClass, String creationLine, String beanName, String origClass) {
        this.createdUnderClass = createdUnderClass;
        this.creationLine = creationLine;
        this.beanName = beanName;
        this.origClass = origClass;
    }

    public String getCreatedUnderClass() {
        return createdUnderClass;
    }

    public String getCreationObjectConstructorArgs() {
        return creationLine.substring(creationLine.indexOf("(")+1, creationLine.indexOf(")"));
    }

    public String getBeanName() {
        return beanName;
    }

    public String getOrigClass() {
        return origClass;
    }
}
