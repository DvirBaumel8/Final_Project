package ProductsP;

import java.util.List;

public class ProductServiceImpl implements ProductService{
    private ProductRepository productRepository;

    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.getAllProducts();
    }
}
