
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);
        Actor actor1 = context.getBean("BradPit", Actor.class);
        Actor actor2 = context.getBean("MariaKarry", Actor.class);
        Actor actor3 = context.getBean("TomHanks", Actor.class);
        List<Actor> actors = context.getBean("actorsList", List.class);

        actors.add(actor1);
        actors.add(actor2);
        actors.add(actor3);

        for(Actor actor: actors) {
            System.out.println(actor);
        }
    }

}
