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

        List<Actor> actors = new ArrayList<>();
        actors.add(actor1);
        actors.add(actor2);
        actors.add(actor3);

        for (Actor actor : actors) {
            System.out.println(actor);
        }
    }
}
