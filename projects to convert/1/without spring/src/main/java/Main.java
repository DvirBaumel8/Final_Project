import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Actor actor1 = new Actor(330, "Brad pit", 40, Actor.Gender.Male);

        Actor actor2 = new Actor();
        actor2.setID(331);
        actor2.setFullName("Maria karry");
        actor2.setAge(38);
        actor2.setGender(Actor.Gender.Female);

        Actor actor3 = new Actor(332, "Tom Henks");
        actor3.setAge(44);
        actor3.setGender(Actor.Gender.Male);

        List<Actor> actorsList = new ArrayList<>();
        actorsList.add(actor1);
        actorsList.add(actor2);
        actorsList.add(actor3);

        for (Actor actor : actorsList) {
            System.out.println(actor);
        }
    }

}
