import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class Main {
    private static ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);

    public static void main(String[] args) {
        ActorsRepo actorsRepo = context.getBean("actorsFactory", ActorsRepo.class);
        List<Actor> actors = actorsRepo.getActors();
        List<Actor> staticActors = actorsRepo.getStaticActors();

        for(Actor actor : actors) {
            System.out.println(actor);
        }

        for(Actor actor : staticActors) {
            System.out.println(actor);
        }

    }
}
