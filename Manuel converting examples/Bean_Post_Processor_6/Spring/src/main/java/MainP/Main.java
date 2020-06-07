package MainP;

import Company.BeanPostProcessorImpl;
import Company.Company;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(MainConfiguration.class);
        Company[] companies = new Company[2];

        try {
            companies[0] = ctx.getBean("cisco", Company.class);
            companies[1] = ctx.getBean("waze", Company.class);

            for(Company company : companies) {
                System.out.println(company);
            }

        }
        catch (BeansException ex) {
            System.out.println("Something went wrong");
        }
    }
}
