import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DomainController {
    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private SaleRepository saleRepository;
    private OrderRepository orderRepository;
    private PeriodicOrderRuleRepository periodicOrderRuleRepository;

    public DomainController(CategoryRepository categoryRepository,
                            ProductRepository productRepository,
                            SaleRepository saleRepository,
                            OrderRepository orderRepository,
                            PeriodicOrderRuleRepository periodicOrderRuleRepository) {

        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.orderRepository = orderRepository;
        this.periodicOrderRuleRepository = periodicOrderRuleRepository;
    }
    public ProductRepository getProductRepository() {
        return productRepository;}

    public CategoryRepository getCategoryRepository() {
        return categoryRepository;}
    public PeriodicOrderRuleRepository getPeriodicOrderRuleRepository() {
        return periodicOrderRuleRepository;}
    public SaleRepository getSaleRepository() {
        return saleRepository;}

    public OrderRepository getOrderRepository() {
        return orderRepository;}
    private boolean productAlreadyExists(String productName, String brand, String category, String sub_cat_name, String size) {
        for (Product p : productRepository.findAll()) {
            if (p.getProductName().equalsIgnoreCase(productName) && p.getBrand().equalsIgnoreCase(brand) && p.getCategory().equalsIgnoreCase(category) && p.getSub_cat_name().equalsIgnoreCase(sub_cat_name) && p.getSize().equalsIgnoreCase(size)) {
                return true;}
        }
        return false;
    }
    public boolean addProductBatch(String productName, String brand, String category, String sub_cat_name, String size, int shopQuantity, int warehouseQuantity, int rank, int deliveryTime, double cost_price, LocalDate expirationDate, int damagedAmount, String supplierID, String aisle, String shelf) {
        if (productAlreadyExists(productName, brand, category, sub_cat_name, size)) {
            return false;}
        Category categoryObject = findCategory(category, sub_cat_name, size);
        if (categoryObject == null) {
            addCategory(category, sub_cat_name, size);
            categoryObject = findCategory(category, sub_cat_name, size);}
        if (categoryObject == null) {
            return false;}
        String generatedID = "P" + System.currentTimeMillis();
        Product newProduct = new Product(generatedID, productName, brand, categoryObject, shopQuantity, warehouseQuantity, rank, deliveryTime, cost_price, expirationDate, damagedAmount, aisle, shelf);
        return productRepository.add(newProduct);
    }
    public Product getProductByID(String id) {
        return productRepository.findById(id);
    }

    public boolean updateQuantities(String id, int newQty, int newWarehouseQty) {
        Product p = getProductByID(id);
        if (p == null) {
            return false;}
        return productRepository.updateQuantities(id, newQty, newWarehouseQty, p.getDamagedAmount());
    }

    public void updateDamaged(String id, int newShopQty, int newDamagedTotal) {
        Product p = getProductByID(id);
        if (p != null) {
            productRepository.updateQuantities(id, newShopQty, p.getWarehouseQuantity(), newDamagedTotal);}
    }

    public boolean updateNewStatus(String id, boolean newStatus) {
        Product p = getProductByID(id);
        if (p == null) {
            return false;
        }
        p.setActive(newStatus);
        return productRepository.update(p);
    }

    public List<Product> getProductBySubCat(String sub_cat_name) {
        List<Product> products = new ArrayList<>();
        for (Product p : productRepository.findAll()) {
            if (p.getSub_cat_name().equalsIgnoreCase(sub_cat_name)) {
                products.add(p);
            }
        }
        return products;
    }

    public List<Product> getProductByCat(String category) {
        List<Product> products = new ArrayList<>();
        for (Product p : productRepository.findAll()) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                products.add(p);
            }
        }
        return products;
    }
    public Product findProductByDetails(String productName, String brand, String category, String sub_cat_name, String size) {
        for (Product p : productRepository.findAll()) {
            if (p.getProductName().equalsIgnoreCase(productName) && p.getBrand().equalsIgnoreCase(brand) && p.getCategory().equalsIgnoreCase(category) && p.getSub_cat_name().equalsIgnoreCase(sub_cat_name) && p.getSize().equalsIgnoreCase(size)) {
                return p;}
        }
        return null;
    }

    public boolean addPeriodicOrderRule(String productId, int quantity, int dayOfMonth) {
        if (getProductByID(productId) == null) {
            return false;}
        if (quantity <= 0) {
            return false;}
        if (dayOfMonth < 1 || dayOfMonth > 31) {
            return false;}
        for (PeriodicOrderRuleDTO rule : periodicOrderRuleRepository.findAll()) {
            if (rule.isActive() && rule.getProductId().equals(productId) && rule.getDayOfMonth() == dayOfMonth) {
                return false;
            }
        }
        return periodicOrderRuleRepository.createRule(productId, quantity, dayOfMonth) != null;
    }

    public List<Product> getProductBySubSubCat(String size) {
        List<Product> products = new ArrayList<>();
        for (Product p : productRepository.findAll()) {
            if (p.getSize().equalsIgnoreCase(size)) {
                products.add(p);
            }
        }
        return products;
    }

    public List<Product> displayPeriodicReportByDate(LocalDate start, LocalDate end) {
        List<Product> report = new ArrayList<>();
        for (Product p : productRepository.findAll()) {
            LocalDate addedDate = p.getEntryTime();
            if (!addedDate.isBefore(start) && !addedDate.isAfter(end) && p.getDamagedAmount() > 0) {
                report.add(p);
            }
        }
        return report;
    }

    public List<Product> getProductsForSystemAlerts() {
        return productRepository.findShortageProducts();}

    public boolean addSale(String id, String category, String subCat, double rate, LocalDate start, LocalDate end) {
        Sale existingSale = saleRepository.findById(id);
        if (existingSale != null) {
            return false;}
        Sale newSale = new Sale(id, subCat, category, rate, start, end);
        boolean added = saleRepository.add(newSale);
        if (added) {
            applyAllSalesToAllProducts();}
        return added;
    }

    public void applyAllSalesToAllProducts() {
        List<Sale> sales = saleRepository.findAll();
        for (Product p : productRepository.findAll()) {
            p.updatePriceAfterDiscount(sales);
            productRepository.update(p);}
    }

    public List<Sale> displayAllStoreSales() {
        return saleRepository.findAll();}

    public boolean addCategory(String categoryName, String subCategoryName, String subSubCategoryName) {
        Category existingCategory = findCategory(categoryName, subCategoryName, subSubCategoryName);
        if (existingCategory != null) {
            return false;}
        Category newCategory = new Category(categoryName, subCategoryName, subSubCategoryName);
        return categoryRepository.add(newCategory);
    }

    public Category findCategory(String categoryName, String subCategoryName, String subSubCategoryName) {
        if (categoryName == null || subCategoryName == null || subSubCategoryName == null) {
            return null;}
        return categoryRepository.find(categoryName, subCategoryName, subSubCategoryName);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();}
}