package MainP;

import Cinema.Actor;
import Cinema.Movie;

public class Main {
    public static void main(String[] args) {
        Actor bradPit = new Actor(330, "Brad Pit", 40, Actor.Gender.Male);

        Actor mariaKarry = new Actor();
        mariaKarry.setGender(Actor.Gender.Female);
        mariaKarry.setID(331);
        mariaKarry.setAge(38);
        mariaKarry.setFullName("Maria Karry");

        Actor tomHanks = new Actor(332, "Tom Henks");
        tomHanks.setGender(Actor.Gender.Male);
        tomHanks.setAge(44);

        Actor[] actorsNFS = new Actor[3];
        actorsNFS[0] = bradPit;
        actorsNFS[1] = mariaKarry;
        actorsNFS[2] = tomHanks;

        Movie movie1 = new Movie("Need for Speed 1", "Racing");
        movie1.setActors(actorsNFS);
        System.out.println(movie1);

        Actor[] friendsActors = new Actor[2];
        friendsActors[0] = bradPit;
        friendsActors[1] = mariaKarry;

        Movie movie2 = new Movie("Friends the movie", "Comedy");
        movie2.setActors(friendsActors);
        System.out.println(movie2);
    }
}
