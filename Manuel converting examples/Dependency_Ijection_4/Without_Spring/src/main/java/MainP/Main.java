package MainP;

import ProductsP.Product;
import ProductsP.ProductRepository;
import ProductsP.ProductRepositoryImpl;
import ProductsP.ProductServiceImpl;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ProductRepository productRepository = new ProductRepositoryImpl();
        ProductServiceImpl productService = new ProductServiceImpl();
        productService.setProductRepository(productRepository);
        List<Product> products = productService.getAllProducts();
        System.out.println("Products:");

        for (Product product : products) {
            System.out.println(product.getName());
        }
    }

}
