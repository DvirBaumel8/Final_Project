import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Actor> staticActors = ActorsRepo.getStaticActors();

        for(Actor actor : staticActors) {
            System.out.println(actor);
        }

        ActorsRepo actorsRepo = new ActorsRepo();
        List<Actor> actors = actorsRepo.getActors();

        for(Actor actor : actors) {
            System.out.println(actor);
        }
    }

}
