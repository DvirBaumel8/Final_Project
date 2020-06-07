import java.util.ArrayList;
import java.util.List;

public class ActorsRepo {
    private static List<Actor> actorsList = new ArrayList<>();

    public static List<Actor> getStaticActors() {
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

        actorsList.add(actor1);
        actorsList.add(actor2);
        actorsList.add(actor3);

        return actorsList;
    }

    public List<Actor> getActors() {
        Actor actor4 = new Actor(330, "Brad pit", 40, Actor.Gender.Male);

        Actor actor5 = new Actor();
        actor5.setID(331);
        actor5.setFullName("Maria karry");
        actor5.setAge(38);
        actor5.setGender(Actor.Gender.Female);

        Actor actor6 = new Actor(332, "Tom Henks");
        actor6.setAge(44);
        actor6.setGender(Actor.Gender.Male);

        actorsList.add(actor4);
        actorsList.add(actor5);
        actorsList.add(actor6);

        return actorsList;
    }
}
