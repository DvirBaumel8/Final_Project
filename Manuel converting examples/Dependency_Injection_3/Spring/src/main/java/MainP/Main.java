package MainP;

import Cinema.Movie;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Main {
    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(MainConfiguration.class);
        try {
            Movie movie1 = ctx.getBean("needForSpeed1", Movie.class);
            System.out.println(movie1.toString());

        }
        catch (BeansException ex) {
            System.out.println("Something went wrong..");
        }

        try {
            Movie movie2 = ctx.getBean("friendsTheMovie", Movie.class);
            System.out.println(movie2.toString());
        }
        catch (BeansException ex) {
            System.out.println("Something went wrong..");
        }
    }



}
