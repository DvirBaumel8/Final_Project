import java.util.ArrayList;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
   private static ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);
    public static void main(String[] args) {
      Actor actor1 = context.getBean("actor1", Actor.class);

      Actor actor2 = context.getBean("actor2", Actor.class);

      Actor actor3 = context.getBean("actor3", Actor.class);

      List<Actor> actorsList = context.getBean("actorsList", List.class);
        actorsList.add(actor1);
        actorsList.add(actor2);
        actorsList.add(actor3);

        for (Actor actor : actorsList) {
            System.out.println(actor);
        }
    }

}
