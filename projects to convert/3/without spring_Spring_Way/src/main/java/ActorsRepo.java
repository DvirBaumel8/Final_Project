import java.util.ArrayList;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ActorsRepo {
   private static ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);

    public static List<Actor> getStaticActors() {
      List<Actor> actorsList = context.getBean("actorsList", List.class);

      Actor actor1 = context.getBean("actor1", Actor.class);

      Actor actor2 = context.getBean("actor2", Actor.class);

        Actor actor3 = new Actor(332, "Tom Henks");
        actor3.setAge(44);
        actor3.setGender(Actor.Gender.Male);

        actorsList.add(actor1);
        actorsList.add(actor2);
        actorsList.add(actor3);

        return actorsList;
    }

    public List<Actor> getActors() {
        List<Actor> actorsList = new ArrayList<>();
        Actor actor4 = new Actor(330, "Brad pit", 40, Actor.Gender.Male);
        Actor actor5 = new Actor();
        actor5.setID(331);
        actor5.setFullName("Maria karry");
        actor5.setAge(38);
        actor5.setGender(Actor.Gender.Female);

      Actor actor6 = context.getBean("actor6", Actor.class);

        actorsList.add(actor4);
        actorsList.add(actor5);
        actorsList.add(actor6);

        return actorsList;
    }
}
