import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Core Controller of the Domain Layer.
 * Manages the primary data structures of the system, including the product inventory,
 * supplier registry, and active sales. This class is responsible for direct data
 * manipulation, unique ID generation, and state persistence.
 */

public class DomainController {
    private Map<String, Product> inventory;
    private List<Supplier> suppliers;
    private List<Sale> sales;
    private List<Category> categories;
    private static int idCounter = 50;

    public DomainController() {
        this.inventory = new HashMap<>();
        this.suppliers = new ArrayList<>();
        this.sales = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    /**
     * Searches for a supplier by their unique ID.
     * @param supplierID The unique identifier for the supplier.
     * @return The Supplier object if found, or null if no match exists.
     */
    public Supplier findSupplierById(String supplierID) {
        for (Supplier supplier : suppliers) {
            if (supplier.getSupplierID().equalsIgnoreCase(supplierID)) {
                return supplier;
            }
        }
        return null;

    }
    /**
     * Creates and stores a new product batch in the system.
     * Automatically generates a unique product ID starting from "P51" and links
     * the product to an existing supplier.
     * * @return true if the product was created and added to inventory,
     * false if the supplier ID was not found.
     */
    public boolean addProductBatch(String productName, String brand, String category, String sub_cat_name, String size, int shopQuantity, int warehouseQuantity, int rank, int deliveryTime, double cost_price, LocalDate expirationDate, int damagedAmount, String supplierID, String aisle, String shelf) {
        Supplier supplier = findSupplierById(supplierID);
        if (supplier == null) {return false;}
        Category categoryObject = findCategory(category, sub_cat_name, size);
        if (categoryObject == null) {return false;}
        idCounter++;
        String generatedID = "P" + idCounter;
        Product newProduct = new Product(generatedID, productName, brand, categoryObject, shopQuantity, warehouseQuantity, rank,deliveryTime, cost_price, expirationDate, damagedAmount, supplier, aisle, shelf);
        inventory.put(generatedID, newProduct);
        return true;
    }
    /**
     * Retrieves a product from the inventory map using its ID.
     * @param id The unique product identifier.
     * @return The Product object, or null if not found.
     */
    public Product getProductByID(String id) {
        return inventory.get(id);
    }

    /**
     * Directly updates the shop and warehouse quantities for a specific product.
     * Also triggers a recalculation of the product's total inventory count.
     * @return true if the product exists and was updated, false otherwise.
     */
    public boolean updateQuantities(String id, int newQty, int newWarehouseQty) {
        Product p = getProductByID(id);
        if (p != null) {
            p.setShopQuantity(newQty);
            p.setWarehouseQuantity(newWarehouseQty);
            p.setTotalQuantity(newQty, newWarehouseQty);
            return true;
        } else {
            return false;
        }
    }
    /**
     * Updates the damaged items count and adjusts the remaining shop stock.
     * @param id Product ID.
     * @param newShopQty The updated quantity left in the shop.
     * @param newDamagedTotal The new total count of damaged items.
     */
    public void updateDamaged(String id, int newShopQty, int newDamagedTotal) {
        Product p = getProductByID(id);
        if (p != null) {
            p.setShopQuantity(newShopQty);
            p.setDamagedAmount(newDamagedTotal);
        }
    }

    public boolean updateNewStatus(String id, boolean newStatus) {
        Product p = getProductByID(id);
        if (p != null) {
            p.setActive(newStatus);
            return true;
        }
        return false;
    }
    /**
     * Filters the inventory to find products belonging to a specific sub category.
     * @param sub_cat_name The category name.
     * @return A list of matching Product objects.
     */

    public List<Product> getProductBySubCat(String sub_cat_name) {
        List<Product> products = new ArrayList<>();
        for (Product p : inventory.values()) {
            if (p.getSub_cat_name().equalsIgnoreCase(sub_cat_name)) {
                products.add(p);
            }
        }
        return products;
    }
    /**
     * Filters the inventory to find products belonging to a specific category.
     * @param Category The category name.
     * @return A list of matching Product objects.
     */
    public List<Product> getProductByCat(String Category) {
        List<Product> products = new ArrayList<>();
        for (Product p : inventory.values()) {
            if (p.getCategory().equalsIgnoreCase(Category)) {
                products.add(p);
            }
        }
        return products;
    }

    /**
     * Filters the inventory to find products belonging to a specific sub-sub-category (size).
     * @param size The size of the products.
     * @return A list of matching Product objects.
     */
    public List<Product> getProductBySubSubCat(String size) {
        List<Product> products = new ArrayList<>();
        for (Product p : inventory.values()) {
            if (p.getSize().equalsIgnoreCase(size)) {
                products.add(p);}
        }
        return products;
    }
    /**
     * Generates a report of items with recorded damage within a specific date range.
     * @param start The start date of the reporting period.
     * @param end The end date of the reporting period.
     * @return A list of products damaged during the specified timeframe.
     */
    public List<Product> displayPeriodicReportByDate(LocalDate start, LocalDate end) {
        List<Product> report = new ArrayList<>();

        for (Product p : inventory.values()) {
            LocalDate addedDate = p.getEntryTime();
            if (!addedDate.isBefore(start) && !addedDate.isAfter(end) && p.getDamagedAmount() > 0) {
                report.add(p);
            }
        }
        return report;
    }

    /**
     * Identifies all products whose total quantity has fallen below their minimum stock limit.
     * @return A list of products requiring restocking alerts.
     */
    public List<Product> getProductsForSystemAlerts() {
        List<Product> lowStock = new ArrayList<>();
        for (Product p : inventory.values()) {
            if (p.getTotalQuantity() < p.getMin_limit()) {
                lowStock.add(p);
            }
        }
        return lowStock;
    }
    /**
     * Registers a new supplier in the system.
     * @param name Supplier name.
     * @param id Unique Supplier ID.
     * @param rate Default discount rate.
     * @return true if the supplier was added, false if the ID already exists.
     */
    public boolean addNewSup(String name, String id, double rate) {
        for (Supplier s : suppliers) {
            if (s.getSupplierID().equals(id)) {
                return false;
            }
        }
        Supplier newSup = new Supplier(name, id, rate);
        suppliers.add(newSup);
        return true;
    }

    public List<Supplier> displayAllSup() { // לא בטוחה אם גם צריך לעבור לסרוויס
        return suppliers;
    }

    public boolean updateSupDicountRate(String id, double discount) {
        for (Supplier s : suppliers) {
            if (s.getSupplierID().equalsIgnoreCase(id)) {
                s.setDiscountRate(discount);
                return true;
            }
        }
        return false;
    }
    /**
     * Registers a new sale and immediately applies the discount to all relevant products in inventory.
     * @return true if the sale was added successfully, false if the Sale ID is a duplicate.
     */
    public boolean addSale(String id, String category, String subCat, double rate, LocalDate start, LocalDate end) {
        for (Sale s : sales) {
            if (s.getSaleID().equals(id)) {
                return false;
            }
        }
        Sale newSale = new Sale(id, subCat, category, rate, start, end);
        sales.add(newSale);
        for (Product p : inventory.values()) {
            p.updatePriceAfterDiscount(sales);
        }
        return true;
    }

    /**
     * Iterates through the entire inventory to re-calculate product prices based on all active sales.
     */
    public void applyAllSalesToAllProducts() {
        for (Product p : inventory.values()) {
            p.updatePriceAfterDiscount(sales);
        }
    }

    public List<Sale> displayAllStoreSales() {
        return sales;
    }

    /**
     * Adds a new category classification to the system.
     *
     * @return true if the category was added,
     * false if the exact classification already exists.
     */
    public boolean addCategory(String categoryName,
                               String subCategoryName,
                               String subSubCategoryName) {

        Category existingCategory = findCategory(categoryName, subCategoryName, subSubCategoryName);
        if (existingCategory != null) {return false;}
        Category newCategory = new Category(
                categoryName,
                subCategoryName,
                subSubCategoryName
        );
        categories.add(newCategory);return true;}
    /**
     * Finds an exact category classification.
     * @return the matching Category object, or null if it does not exist.
     */
    public Category findCategory(String categoryName, String subCategoryName, String subSubCategoryName) {
        if (categoryName == null || subCategoryName == null || subSubCategoryName == null) {return null;}
        for (Category category : categories) {
            boolean sameCategory = category.getCategoryName().equalsIgnoreCase(categoryName);
            boolean sameSubCategory = category.getSubCategoryName().equalsIgnoreCase(subCategoryName);
            boolean sameSubSubCategory = category.getSubSubCategoryName().equalsIgnoreCase(subSubCategoryName);

            if (sameCategory&& sameSubCategory && sameSubSubCategory) {
                return category;}
        }
        return null;}
    /**
     * Returns all category classifications currently stored in the system.
     */
    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);}
}
