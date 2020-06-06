import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainConfiguration {
    @Bean
    public HelloWorld helloWorld() {
        HelloWorld bean = new HelloWorld();
        bean.setMessage("Hello world first example");

        return bean;
    }

}
