import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class Main {
    private static ApplicationContext context = new AnnotationConfigApplicationContext(mainConfiguration.class);

    public static void main(String[] args) {
        List<Actor> actors = createActorsList();

        for(Actor actor : actors) {
            System.out.println(actor);
        }
    }

    public static List<Actor> createActorsList() {
        return context.getBean("actors", List.class);
    }
}
