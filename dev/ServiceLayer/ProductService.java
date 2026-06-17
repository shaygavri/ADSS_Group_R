import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ProductService {
    private DomainController domain;
    public ProductService(DomainController domain) {
        this.domain = domain;
    }

    /**
     * Utility method to print a standard error message for invalid fields.
     * @param fieldName The name of the field that failed validation.
     * @return false always, to indicate a failed operation.
     */
    private boolean errorType(String fieldName) {
        System.out.println( fieldName + " is invalid or missing.");
        return false; }

    /**
     * Validates and adds a new batch of products to the inventory.
     * @return true if the product was successfully added, false if validation failed.
     */
    public boolean addProductBatch(String name, String brand, String category, String sub_cat_name, String size, int shopQuantity, int warehouseQuantity, int rank, int deliveryTime, double cost,
                                LocalDate expirationDate, int damagedAmount, String supplierID, String aisle, String shelf) {
        if (name == null || name.isEmpty()) return errorType("Product Name");
        if (brand == null || brand.isEmpty()) return errorType("Brand");
        if (category == null || category.isEmpty()) return errorType("Category");
        if (sub_cat_name == null || sub_cat_name.isEmpty()) return errorType("Sub-Category");
        if (aisle == null || aisle.isEmpty()) return errorType("Aisle");
        if (shelf == null || shelf.isEmpty()) return errorType("Shelf");
        if (supplierID == null || supplierID.isEmpty()) return errorType("Supplier ID");
        if (size == null || size.trim().isEmpty()) {return errorType("Size");}
        if (cost <= 0) return errorType("Cost Price (must be positive)");
        if (shopQuantity < 0) return errorType("Shop Quantity (cannot be negative)");
        if (warehouseQuantity < 0) return errorType("Warehouse Quantity (cannot be negative)");
        if (rank < 1 || rank > 10) return errorType("Demand Rank (must be 1-10)");
        if (deliveryTime < 1) return errorType("Delivery Time (must be at least 1 day)");

        if (expirationDate == null || expirationDate.isBefore(LocalDate.now())) {
            return errorType("Expiration Date (cannot be in the past)");
        }

        return domain.addProductBatch(name, brand, category, sub_cat_name, size,
                shopQuantity, warehouseQuantity, rank, deliveryTime,
                cost, expirationDate, damagedAmount, supplierID, aisle, shelf);
    }
    /**
     * Retrieves a list of products associated with a specific category.
     * This method validates the input and communicates with the domain layer to fetch results.
     * * @param category The name of the category to search for.
     * @return A list of {@code Product} objects belonging to the category.
     * Returns an empty list if the input is null, empty, or no products are found.
     */

    public List<Product> getProductByCat(String category) {
        if (category == null || category.isEmpty()) {
            return new ArrayList<>();
        }
        List<Product> results = domain.getProductByCat(category);
        if (results.isEmpty()) {
            System.out.println("No products found under the category: " + category);
        }
        return results;
    }
    /** Returns the product with thd ID provided
    * If now products exist under this ID the user will receive NULL.
    * */

    public Product getProductByID(String id) {
        if (id == null || id.isEmpty()) return null;
        Product id1 = domain.getProductByID(id);
        if (id1 == null) return null;
        else return id1;
    }
    /**
     * Retrieves a list of products associated with a specific sub-category.
     * Validates the input string and fetches matching products from the domain layer.
     * * @param subCat The name of the sub-category to search for.
     * @return A list of {@code Product} objects matching the sub-category.
     * Returns an empty list if the input is null, empty, or no matches are found.
     */
    public List<Product> getProductBySubCat(String subCat) {
        if (subCat == null || subCat.isEmpty()) {
            return new ArrayList<>();
        }
        List<Product> results = domain.getProductBySubCat(subCat);
        if (results.isEmpty()) {
            System.out.println("No products found under the category: " + subCat);
        }
        return results;
    }
    /**
     * Updates the quantity of an existing product in both store and warehouse locations.
     * * @param id The unique identifier of the product.
     * @param store The new quantity available on the shop floor.
     * @param warehouse The new quantity available in the warehouse.
     * @return true if the update was successful, false if the product ID was not found.
     */

    public boolean updateProductQuantity(String id, int store, int warehouse) {
        if (id == null || id.isEmpty()) return errorType("Product ID");
        if (store < 0|| warehouse<0) return errorType("Amount cannot be negative");

        return domain.updateQuantities(id, store, warehouse);
    }
    /**
     * Reports damaged items for a specific product and adjusts stock levels.
     * This method verifies that the product exists and has sufficient shop stock.
     * It then subtracts the damaged quantity from the shop stock and adds it to
     * the total damaged count in the domain layer.
     * @param id The unique identifier of the product.
     * @param damagedQty The number of units to be reported as damaged.
     * @return true if the stock was successfully adjusted; false if the product
     * was not found or if there is insufficient shop stock.
     */
    public boolean updateDamaged(String id, int damagedQty) {
        Product p = domain.getProductByID(id);
        if (p == null) return errorType("Product ID");

        if (p.getShopQuantity() < damagedQty) {
            System.out.println("Not enough shop stock to report as damaged");
            return false;
        }
        int newShop = p.getShopQuantity() - damagedQty;
        int newDamaged = p.getDamagedAmount() + damagedQty;
        domain.updateDamaged(id, newShop, newDamaged);
        return true;
    }
    /**
     * Updates the active status of a product (enabling or disabling it in the system)
     * Performs validation on the product ID and ensures the product exists
     * before calling the domain layer to update the status.
     * @param id The unique identifier of the product.
     * @param isActive The new status to be set (true for Active, false for Inactive).
     * @return true if the status was updated successfully; false if the ID is
     * invalid or the product does not exist.
     */

    public boolean updateProductActiveStatus(String id, boolean isActive) {
        if (id == null || id.isEmpty()) return errorType("Product ID");
        Product p = domain.getProductByID(id);
        if (p == null) return errorType("Product not found");

        return domain.updateNewStatus(id, isActive);
    }

    }