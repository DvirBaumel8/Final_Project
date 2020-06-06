package MainP;

import Cinema.MoviesConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MoviesConfiguration.class)
public class MainConfiguration {

}
