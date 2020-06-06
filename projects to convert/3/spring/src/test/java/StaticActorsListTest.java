import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class StaticActorsListTest {
    private static List<Actor> actorsTestSubject;

    @BeforeClass
    public static void setup() {
        actorsTestSubject = new ArrayList<>();

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
        actorsTestSubject.add(actor1);
        actorsTestSubject.add(actor2);
        actorsTestSubject.add(actor3);
    }

    @Test
    public void actorsListSizeTest() {
        int expectedSize = 3;
        Assert.assertEquals(expectedSize, actorsTestSubject.size());
    }

    @Test
    public void averageAgeTest() {
        double averageAgeExpected = 122/3;
        double sum = 0;
        int index = 0;

        for(Actor actor : actorsTestSubject) {
            index++;
            sum += actor.getAge();
        }

        if(index > 0) {
            Assert.assertEquals(averageAgeExpected, sum/index, 1);
        }
    }

    @Test
    public void ID331Test() {
        String actorNameWithID331 = "Maria Karry";
        Integer expectedID = 331;

        for(Actor actor : actorsTestSubject) {
            if(actor.getFullName().equals(actorNameWithID331)) {
                Assert.assertEquals(expectedID, actor.getID());
                return;
            }
        }

        Assert.fail();

    }

}
