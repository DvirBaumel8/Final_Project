package MainP;

import Company.CompanyConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CompanyConfiguration.class)
public class MainConfiguration {

}
