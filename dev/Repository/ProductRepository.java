import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private ProductDAO productDAO;
    private CategoryRepository categoryRepository;

    public ProductRepository(ProductDAO productDAO, CategoryRepository categoryRepository) {
        this.productDAO = productDAO;
        this.categoryRepository = categoryRepository;
    }
    public boolean add(Product product) {
        if (product == null) {
            return false;}
        ProductDTO dto = toDTO(product);
        return productDAO.add(dto);
    }

    public boolean update(Product product) {
        if (product == null) {
            return false;
        }
        ProductDTO dto = toDTO(product);
        return productDAO.update(dto);
    }

    public Product findById(String productId) {
        ProductDTO dto = productDAO.findById(productId);
        if (dto == null) {
            return null;
        }
        return toDomain(dto);
    }

    public List<Product> findAll() {
        List<ProductDTO> dtoList = productDAO.findAll();
        List<Product> products = new ArrayList<>();
        for (ProductDTO dto : dtoList) {
            products.add(toDomain(dto));}
        return products;
    }

    public List<Product> findShortageProducts() {
        List<ProductDTO> dtoList = productDAO.findShortageProducts();
        List<Product> products = new ArrayList<>();
        for (ProductDTO dto : dtoList) {
            products.add(toDomain(dto));}
        return products;
    }

    public boolean updateQuantities(String productId, int shopQuantity, int warehouseQuantity, int damagedAmount) {
        return productDAO.updateQuantities(productId, shopQuantity, warehouseQuantity, damagedAmount);
    }

    private ProductDTO toDTO(Product product) {
        int categoryId = categoryRepository.findCategoryId(product.getCategory(), product.getSub_cat_name(), product.getSize());
        if (categoryId == -1) {
            categoryRepository.add(product.getCategoryObject());
            categoryId = categoryRepository.findCategoryId(product.getCategory(), product.getSub_cat_name(), product.getSize());
        }
        return new ProductDTO(product.getProductID(), product.getProductName(), product.getBrand(), categoryId, product.getShopQuantity(), product.getWarehouseQuantity(), product.getTotalQuantity(), product.getAisle(), product.getShelf(), product.getMin_limit(), product.getRank(), product.getDeliveryTime(), product.getCost_price(), product.getSell_price(), product.getBest_price(), product.getEntryTime().toString(), product.getExpirationDate().toString(), product.getDamagedAmount(), product.getIsActive());
    }

    private Product toDomain(ProductDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId());
        if (category == null) {
            category = new Category("Unknown", "Unknown", "Unknown");
        }
        Product product = new Product(dto.getProductId(), dto.getProductName(), dto.getBrand(), category, dto.getShopQuantity(), dto.getWarehouseQuantity(), dto.getRank(), dto.getDeliveryTime(), dto.getCostPrice(), LocalDate.parse(dto.getExpirationDate()), dto.getDamagedAmount(), dto.getAisle(), dto.getShelf());
        product.setSell_price(dto.getSellPrice());
        product.setBest_price(dto.getBestPrice());
        product.setActive(dto.isActive());

        return product;
    }
}