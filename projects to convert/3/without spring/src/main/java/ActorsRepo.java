import java.util.ArrayList;
import java.util.List;

public class ActorsRepo {

    public static List<Actor> getStaticActors() {
        List<Actor> staticActors = new ArrayList<>();
        Actor actor1 = new Actor(330, "Brad pit", 40, Actor.Gender.Male);

        //@BlackList
        Actor actor2 = new Actor();
        actor2.setID(331);
        actor2.setFullName("Maria Karry");
        actor2.setAge(38);
        actor2.setGender(Actor.Gender.Female);

        Actor actor3 = new Actor(332, "Tom Henks");
        actor3.setAge(44);
        actor3.setGender(Actor.Gender.Male);

        staticActors.add(actor1);
        staticActors.add(actor2);
        staticActors.add(actor3);

        return staticActors;
    }

    public List<Actor> getActors() {
        List<Actor> actors = new ArrayList<>();
        Actor actor4 = new Actor(330, "Brad pit", 40, Actor.Gender.Male);

        Actor actor5 = new Actor();
        actor5.setID(331);
        actor5.setFullName("Maria karry");
        actor5.setAge(38);
        actor5.setGender(Actor.Gender.Female);

        Actor actor6 = new Actor(332, "Tom Henks");
        actor6.setAge(44);
        actor6.setGender(Actor.Gender.Male);

        actors.add(actor4);
        actors.add(actor5);
        actors.add(actor6);

        return actors;
    }
}
