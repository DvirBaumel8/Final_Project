package ProjectScanning;

public class BeanNamesGenerator {
    private static int index = -1;

    public static String getNewBeanName() {
        index++;
        return "Bean" + index;
    }
}
