
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ActorsListTest {
    private static List<Actor> actorsTestSubject;

    @BeforeClass
    public static void setup() {
        actorsTestSubject = new ArrayList<>();
        Actor actor1 = new Actor(1, "XXX", 30, Actor.Gender.Male);

        Actor actor2 = new Actor();
        actor2.setID(2);
        actor2.setFullName("YYY");
        actor2.setAge(20);
        actor2.setGender(Actor.Gender.Female);

        actorsTestSubject.add(actor1);
        actorsTestSubject.add(actor2);
    }

    @Test
    public void actorsListSize() {
        int expectedSize = 2;

        Assert.assertEquals(expectedSize, actorsTestSubject.size());
    }

    @Test
    public void verifyOnlyOneFemale() {
        int counter = 0;
        int expectedFemales = 1;

        for(Actor actor : actorsTestSubject) {
            if(actor.getGender().equals(Actor.Gender.Female)) {
                counter++;
            }
        }
        Assert.assertEquals(expectedFemales, counter);
    }

    @Test
    public void averageAgeTest() {
        double expectedAverage = 25.0;
        double sum = 0;
        int index = 0;

        for(Actor actor: actorsTestSubject) {
            index++;
            sum += actor.getAge();
        }

        Assert.assertEquals(expectedAverage, sum/index, 1);
    }
}
