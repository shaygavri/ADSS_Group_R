import java.util.List;

public interface ProductDAO {
    boolean add(ProductDTO productDTO);
    boolean update(ProductDTO productDTO);
    ProductDTO findById(String productId);
    List<ProductDTO> findAll();
    List<ProductDTO> findShortageProducts();
    boolean updateQuantities(String productId, int shopQuantity, int warehouseQuantity, int damagedAmount);
}