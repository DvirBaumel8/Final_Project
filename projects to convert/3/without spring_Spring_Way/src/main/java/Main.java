import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
   private static ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);
    public static void main(String[] args) {
        List<Actor> staticActors = ActorsRepo.getStaticActors();

        for(Actor actor : staticActors) {
            System.out.println(actor);
        }

      ActorsRepo actorsRepo = context.getBean("actorsRepo", ActorsRepo.class);
        List<Actor> actors = actorsRepo.getActors();

        for(Actor actor : actors) {
            System.out.println(actor);
        }
    }

}
