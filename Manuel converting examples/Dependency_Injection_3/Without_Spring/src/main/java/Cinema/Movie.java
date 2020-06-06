package Cinema;

import java.lang.reflect.Modifier;

public class Movie {
    private String name;
    private Actor[] actors;
    private String genre;

    public Movie(String name, String genre) {
        this.name = name;
        this.genre = genre;
    }

    public void setActors(Actor[] actors) {
        this.actors = actors;
    }

    public String getName() {
        return name;
    }

    public Actor[] getActors() {
        return actors;
    }

    public String getGenre() {
        return genre;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(String.format("Movie name: %s.\n", name));
        str.append(String.format("Movie genre: %s.\n\n", genre));
        str.append("Movie actors: \n");
        for(int i =0; i < actors.length; i++) {
            str.append(String.format("Actor_%d: %s", i + 1, actors[i]));
        }

        return str.toString();
    }
}
