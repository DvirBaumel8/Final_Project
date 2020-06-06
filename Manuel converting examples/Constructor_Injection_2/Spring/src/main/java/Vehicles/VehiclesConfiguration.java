package Vehicles;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ComponentScan("Vehicles")
public class VehiclesConfiguration {

    @Bean
    @Scope("prototype")
    public Engine engineV6() {
        return new Engine("v6", 2.6);
    }

    @Bean
    @Scope("prototype")
    public Engine engineV4() {
        return new Engine("v4", 1.6);
    }

    @Bean
    @Scope("prototype")
    public Wheel[] wheelsPC() {
        Wheel[] wheels = new Wheel[4];
        for(int i =0; i < 4; i ++) {
            wheels[i] = new Wheel(23,"PC");
        }
        return wheels;
    }

    @Bean
    @Scope("prototype")
    public Wheel[] wheelsBP() {
        Wheel[] wheels = new Wheel[4];
        for(int i =0; i < 4; i ++) {
            wheels[i] = new Wheel(17,"BP");
        }
        return wheels;
    }
}
