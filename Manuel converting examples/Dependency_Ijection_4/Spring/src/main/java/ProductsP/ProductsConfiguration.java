package ProductsP;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductsConfiguration {
    @Bean(name = "productRepository")
    public ProductRepository getProductRepository() {
        return new ProductRepositoryImpl();
    }

    @Bean(name = "productService")
    public ProductService getProductService() {
        ProductServiceImpl productService = new ProductServiceImpl();
        productService.setProductRepository(getProductRepository());
        return productService;
    }
}
