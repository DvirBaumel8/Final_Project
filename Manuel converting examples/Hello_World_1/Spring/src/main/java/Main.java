import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        HelloWorld bean;
        ApplicationContext ctx = new AnnotationConfigApplicationContext(MainConfiguration.class);

        try {
            bean = ctx.getBean("helloWorld", HelloWorld.class);
            String message = bean.getMessage();
            System.out.println(message);
        }
        catch (BeansException ex) {
            System.out.println("Something went wrong...");
        }
    }
}
