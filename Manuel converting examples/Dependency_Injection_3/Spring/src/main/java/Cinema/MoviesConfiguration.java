package Cinema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ActorsConfiguration.class)
public class MoviesConfiguration {

    @Autowired
    private Actor BradPit;
    @Autowired
    private Actor MariaKarry;
    @Autowired
    private Actor TomHanks;

    @Bean
    public Movie needForSpeed1() {
        Movie movie = new Movie("Need for Speed 1", "Racing");
        Actor[] nfsActors = new Actor[3];
        nfsActors[0] = BradPit;
        nfsActors[1] = MariaKarry;
        nfsActors[2] = TomHanks;
        movie.setActors(nfsActors);

        return movie;
    }

    @Bean
    public Movie friendsTheMovie() {
        Movie movie = new Movie("Friends the movie", "Comedy");
        Actor[] friendsActors = new Actor[2];
        friendsActors[0] = BradPit;
        friendsActors[1] = MariaKarry;
        movie.setActors(friendsActors);

        return movie;
    }
}
