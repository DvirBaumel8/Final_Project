import java.util.ArrayList;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ActorsRepo {
   private static ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);

    public static List<Actor> getStaticActors() {
        List<Actor> staticActors = new ArrayList<>();
      Actor actor1 = context.getBean("actor1", Actor.class);

//BlackList executed
        Actor actor2 = new Actor();
        actor2.setID(331);
        actor2.setFullName("Maria Karry");
        actor2.setAge(38);
        actor2.setGender(Actor.Gender.Female);

      Actor actor3 = context.getBean("actor3", Actor.class);

        staticActors.add(actor1);
        staticActors.add(actor2);
        staticActors.add(actor3);

        return staticActors;
    }

    public List<Actor> getActors() {
        List<Actor> actors = new ArrayList<>();
      Actor actor4 = context.getBean("actor4", Actor.class);

      Actor actor5 = context.getBean("actor5", Actor.class);

      Actor actor6 = context.getBean("actor6", Actor.class);

        actors.add(actor4);
        actors.add(actor5);
        actors.add(actor6);

        return actors;
    }
}
