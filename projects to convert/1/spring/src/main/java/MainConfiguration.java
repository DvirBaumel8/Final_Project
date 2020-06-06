import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MainConfiguration {

    @Bean
    public Actor BradPit(){
        Actor bradPit = new Actor(330, "Brad Pit", 40, Actor.Gender.Male);
        return bradPit;
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
    public Actor TomHenks() {
        Actor actor3 = new Actor(332, "Tom Henks");
        actor3.setGender(Actor.Gender.Male);
        actor3.setAge(44);

        return actor3;
    }

    @Bean
    public List<Actor> actors() {
        List<Actor> actors = new ArrayList<>();
        actors.add(BradPit());
        actors.add(MariaKarry());
        actors.add(TomHenks());

        return actors;
    }

}
