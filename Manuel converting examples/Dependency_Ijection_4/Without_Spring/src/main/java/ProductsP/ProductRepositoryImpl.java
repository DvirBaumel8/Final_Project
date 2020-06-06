package ProductsP;

import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryImpl implements ProductRepository{
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<Product>();

        productList.add(new Product("Laptop", 100, 2));
        productList.add(new Product("Phone", 250, 3));
        productList.add(new Product("Keyboard", 50, 1));

        return productList;
    }
}
