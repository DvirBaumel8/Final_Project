import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class ActorsRepo {
    private static ApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);

    private List<Actor> actors;
    private List<Actor> staticActors;

    public ActorsRepo(List<Actor> staticActors, List<Actor> actors) {
        this.actors = actors;
        this.staticActors = staticActors;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public List<Actor> getStaticActors() {
        return staticActors;
    }
}
