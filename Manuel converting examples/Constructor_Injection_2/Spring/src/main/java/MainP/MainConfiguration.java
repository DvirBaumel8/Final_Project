package MainP;

import Vehicles.Engine;
import Vehicles.VehiclesConfiguration;
import Vehicles.Wheel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

@Configuration
@Import(VehiclesConfiguration.class)
public class MainConfiguration {

}
