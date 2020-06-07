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
        Actor actor1 = context.getBean("BradPit", Actor.class);
        Actor actor2 = context.getBean("MariaKarry", Actor.class);
        Actor actor3 = context.getBean("TomHanks", Actor.class);
        List<Actor> actorsList = context.getBean("actorsList", List.class);
        actorsList.add(actor1);
        actorsList.add(actor2);
        actorsList.add(actor3);
        return actorsList;
    }
}
