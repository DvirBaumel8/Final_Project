import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class ActorsRepo {
    private static ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);

    public static List<Actor> getActorsStatic() {
        List<Actor> actorsList = context.getBean("actorsList", List.class);

        Actor actor1 = context.getBean("BradPit", Actor.class);
        Actor actor2 = context.getBean("MariaKarry", Actor.class);
        Actor actor3 = context.getBean("TomHanks", Actor.class);

        actorsList.add(actor1);
        actorsList.add(actor2);
        actorsList.add(actor3);

        return actorsList;
    }

    public List<Actor> getActors() {
        List<Actor> actorsList = context.getBean("actorsList", List.class);

        Actor actor1 = context.getBean("BradPit", Actor.class);
        Actor actor2 = context.getBean("MariaKarry", Actor.class);
        Actor actor3 = context.getBean("TomHanks", Actor.class);

        actorsList.add(actor1);
        actorsList.add(actor2);
        actorsList.add(actor3);

        return actorsList;
    }
}
