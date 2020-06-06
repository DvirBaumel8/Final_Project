import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MainConfiguration {
    private int counter = 0;

    @Bean
    public Actor actor1(){
        Actor actor1 = new Actor(counter, "Brad Pit", 40, Actor.Gender.Male);
        counter++;
        return actor1;
    }

    @Bean
    public Actor actor2() {
        Actor actor2 = new Actor();
        actor2.setGender(Actor.Gender.Female);
        actor2.setID(counter);
        actor2.setAge(38);
        actor2.setFullName("Maria Karry");
        counter++;
        return actor2;
    }

    @Bean
    public Actor actor3() {
        Actor actor3 = new Actor(counter, "Tom Henks");
        counter++;
        actor3.setGender(Actor.Gender.Male);
        actor3.setAge(44);

        return actor3;
    }

    @Bean
    public Actor actor4() {
        Actor actor4 = new Actor(counter++, "Brad Pit", 40, Actor.Gender.Male);

        return actor4;
    }

    @Bean
    public Actor actor5() {
        Actor actor5 = new Actor();
        actor5.setID(counter++);
        actor5.setFullName("Maria karry");
        actor5.setAge(38);
        actor5.setGender(Actor.Gender.Female);

        return actor5;
    }

    @Bean
    public Actor actor6() {
        Actor actor6 = new Actor(counter++, "Tom Henks");
        actor6.setAge(44);
        actor6.setGender(Actor.Gender.Male);

        return actor6;
    }

    @Bean
    public ActorsRepo actorsFactory() {
        return new ActorsRepo(staticActors(), actors());
    }

    @Bean
    public List<Actor> actors() {
        List<Actor> actors = new ArrayList<>();
        actors.add(actor4());
        actors.add(actor5());
        actors.add(actor6());

        return actors;
    }

    @Bean
    public List<Actor> staticActors() {
        List<Actor> actors = new ArrayList<>();
        actors.add(actor1());
        actors.add(actor2());
        actors.add(actor3());

        return actors;
    }
}
