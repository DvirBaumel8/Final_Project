import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;

@Configuration
public class MainConfiguration {
@Bean
public Actor actor6() {
Actor actor6 = new Actor(332, "Tom Henks");
        actor6.setAge(44);
        actor6.setGender(Actor.Gender.Male);
return actor6;
}

@Bean
public List<Actor> actorsList() {
List<Actor> actorsList = new ArrayList<>();
return actorsList;
}

@Bean
public ActorsRepo actorsRepo() {
ActorsRepo actorsRepo = new ActorsRepo();
return actorsRepo;
}

@Bean
public Actor actor1() {
Actor actor1 = new Actor(330, "Brad pit", 40, Actor.Gender.Male);
return actor1;
}

@Bean
@Scope("prototype")
public Actor actor2() {
Actor actor2 = new Actor();
        actor2.setID(331);
        actor2.setFullName("Maria Karry");
        actor2.setAge(38);
        actor2.setGender(Actor.Gender.Female);
return actor2;
}

    
}
