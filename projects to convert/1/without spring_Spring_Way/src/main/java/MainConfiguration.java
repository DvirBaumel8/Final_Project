import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;

@Configuration
public class MainConfiguration {
@Bean
public List<Actor> actorsList() {
List<Actor> actorsList = new ArrayList<>();
return actorsList;
}

@Bean
public Actor actor1() {
Actor actor1 = new Actor(330, "Brad pit", 40, Actor.Gender.Male);
return actor1;
}

@Bean
public Actor actor2() {
Actor actor2 = new Actor();
        actor2.setID(331);
        actor2.setFullName("Maria karry");
        actor2.setAge(38);
        actor2.setGender(Actor.Gender.Female);
return actor2;
}

@Bean
public Actor actor3() {
Actor actor3 = new Actor(332, "Tom Henks");
        actor3.setAge(44);
        actor3.setGender(Actor.Gender.Male);
return actor3;
}

    
}
