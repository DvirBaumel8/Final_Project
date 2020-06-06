import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class ActorsListTest {
    private static List<Actor> actorsTestSubject;

    @BeforeClass
    public static void setup() {
        actorsTestSubject = Main.createActorsList();
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
    public void ID330Test() {
        String actorNameWithID331 = "Tom Henks";
        Integer expectedID = 332;

        for(Actor actor : actorsTestSubject) {
            if(actor.getFullName().equals(actorNameWithID331)) {
                Assert.assertEquals(expectedID, actor.getID());
                return;
            }
        }

        Assert.fail();
    }

    @Test
    public void twoMalesTest() {
        int counter = 0;
        int expectedMales = 2;

        for(Actor actor : actorsTestSubject) {
            if(actor.getGender().equals(Actor.Gender.Male)) {
                counter++;
            }
        }

        Assert.assertEquals(expectedMales, counter);
    }


}
