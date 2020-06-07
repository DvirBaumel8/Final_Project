import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MainConfiguration {
    @Bean
    public Actor BradPit(){
        return new Actor(330, "Brad Pit", 40, Actor.Gender.Male);
    }

    @Bean
    public Actor MariaKarry() {
        Actor mariaKarry = new Actor();
        mariaKarry.setGender(Actor.Gender.Female);
        mariaKarry.setID(331);
        mariaKarry.setAge(38);
        mariaKarry.setFullName("Maria Karry");

        return mariaKarry;
    }

    @Bean
    public Actor TomHanks() {
        Actor actor3 = new Actor(332, "Tom Hanks");
        actor3.setGender(Actor.Gender.Male);
        actor3.setAge(44);

        return actor3;
    }

    @Bean
    public List<Actor> actors() {
        List<Actor> actors = new ArrayList<>();

        return actors;
    }

    @Bean
    public List<Actor> actorsList() {
        return new ArrayList<>();
    }

    @Bean
    public ActorsRepo actorsRepo() {
        return new ActorsRepo();
    }

}
