import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

@Configuration
public class MainConfiguration {
@Bean
public Actor actor5() {
Actor actor5 = new Actor();
        actor5.setID(331);
        actor5.setFullName("Maria karry");
        actor5.setAge(38);
        actor5.setGender(Actor.Gender.Female);
return actor5;
}

@Bean
public Actor actor6() {
Actor actor6 = new Actor(332, "Tom Henks");
        actor6.setAge(44);
        actor6.setGender(Actor.Gender.Male);
return actor6;
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
public Actor actor3() {
Actor actor3 = new Actor(332, "Tom Henks");
        actor3.setAge(44);
        actor3.setGender(Actor.Gender.Male);
return actor3;
}

@Bean
public Actor actor4() {
Actor actor4 = new Actor(330, "Brad pit", 40, Actor.Gender.Male);
return actor4;
}

    
}
