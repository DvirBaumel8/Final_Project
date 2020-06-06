package MainP;

import ProductsP.ProductsConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ProductsConfiguration.class)
public class MainConfiguration {

}
