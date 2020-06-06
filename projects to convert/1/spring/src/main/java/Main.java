
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);
        List<Actor> actors = context.getBean("actors", List.class);

        for(Actor actor: actors) {
            System.out.println(actor);
        }
    }

}
