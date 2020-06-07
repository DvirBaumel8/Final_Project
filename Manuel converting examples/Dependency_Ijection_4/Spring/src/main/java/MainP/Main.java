package MainP;

import ProductsP.Product;
import ProductsP.ProductService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ApplicationContext appContext = new AnnotationConfigApplicationContext(MainConfiguration.class);
        try {
            ProductService productService = appContext.getBean(ProductService.class);
            System.out.println("Products:");

            for (Product product : productService.getAllProducts()) {
                System.out.println(product.getName());
            }
        }
        catch (BeansException ex) {
            System.out.println("Something went wrong..");
        }

    }
}
