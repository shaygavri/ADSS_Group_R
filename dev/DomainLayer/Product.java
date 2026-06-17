import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Represents a single product within the inventory system.
 * This class stores comprehensive product details, including stock levels,
 * location tracking, and supplier information. It also manages dynamic
 * price calculations based on supplier discounts and active store sales.
 */
public class Product {
    private String productID;
    private String productName;
    private String brand;
    private Category category;
    private int shopQuantity;
    private int warehouseQuantity;
    private String aisle;
    private String shelf;
    private int min_limit;
    private int rank;
    private int deliveryTime;
    private double cost_price;
    private double sell_price;
    private double best_price;
    private int price;
    private LocalDate entryDate =  LocalDate.now();
    private LocalDate expirationDate;
    private int damagedAmount;
    private Boolean isActive = true;
    private Supplier supplier;
    private int totalQuantity;

    public Product(String productID, String productName, String brand, Category category, int shopQuantity, int warehouseQuantity, int rank, int deliveryTime, double cost_price, LocalDate expirationDate, int damagedAmount, Supplier supplier, String aisle, String shelf) {
        this.productID = productID;
        this.productName = productName;
        this.brand = brand;
        this.category = category;
        this.shopQuantity = shopQuantity;
        this.warehouseQuantity = warehouseQuantity;
        this.aisle = aisle;
        this.shelf = shelf;
        this.rank = rank;
        this.deliveryTime = deliveryTime;
        this.min_limit = rank * deliveryTime;
        this.cost_price = cost_price;
        this.expirationDate = expirationDate;
        this.damagedAmount = damagedAmount;
        this.supplier = supplier;
        this.sell_price = costPriceAfterDiscount() * 1.35;
        this.best_price = sell_price;
        this.totalQuantity = shopQuantity + warehouseQuantity;
    }

    /** Get methods */
    public String getProductID() {return productID;} // will be set in the service layer
    public String getProductName() {return productName;}
    public String getBrand() {return brand; }
    public String getCategory() {return category.getCategoryName();}
    public String getSub_cat_name() {return category.getSubCategoryName();}
    public String getSubSubCategory() {return category.getSubSubCategoryName();}
    public Category getCategoryObject() {return category;}
    public int getShopQuantity() {return shopQuantity;}
    public int getWarehouseQuantity() {return warehouseQuantity;}
    public String getAisle() {return aisle;}
    public String getShelf() {return shelf;}
    public int getMin_limit() {return min_limit;}
    public double getCost_price() {return cost_price;}
    public double getBest_price() {return best_price;}
    public int getDamagedAmount() {return damagedAmount;}
    public LocalDate getExpirationDate() {return expirationDate;}
    public String getSize() {return category.getSubSubCategoryName();}
    public LocalDate getEntryTime() {return entryDate; }
    public Boolean getIsActive() {return isActive;}
    public Supplier getSupplier() {return supplier;}
    public int getTotalQuantity() {return totalQuantity;}
    /** Set methods */
    public void setTotalQuantity(int shopQuantity,int warehouseQuantity) {this.totalQuantity = shopQuantity+warehouseQuantity;}
    public void setShopQuantity(int shopQuantity) {
        this.shopQuantity = shopQuantity;
    }
    public void setWarehouseQuantity(int warehouseQuantity) {
        this.warehouseQuantity = warehouseQuantity;
    }
    public void setSell_price(double sell_price) {
        this.sell_price = sell_price;
    }
    public void setActive(Boolean active) {
        isActive = active;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public void setDamagedAmount(int damagedAmount) {
        this.damagedAmount = damagedAmount;
    }


    /**
     * Calculates the actual cost price after applying the supplier's specific discount rate.
     * @return The discounted cost price.
     */
    public double costPriceAfterDiscount() {
        if (this.supplier != null) {
            double discountRate = this.supplier.getDiscountRate() / 100.0;
            double discountAmount = this.cost_price * discountRate;
            return this.cost_price - discountAmount;
        }
        return this.cost_price;
    }
    /**
     * Updates the best available price for the product by evaluating all active store sales.
     * The method checks if the product belongs to a target category or sub-category
     * of an active sale and applies the most beneficial (lowest) price.
     * * @param saleList A list of all potential Sale promotions to evaluate.
     */
    public void updatePriceAfterDiscount(List<Sale> saleList) {
        double best = this.sell_price;
        if (saleList != null) {
            for (Sale sale : saleList) {
                boolean categoryMatches =
                        sale.getTarget_category().equalsIgnoreCase(getCategory());
                boolean subCategoryMatches =
                        sale.getTarget_subcat().equalsIgnoreCase(getSub_cat_name());
                if (sale.isSaleActive() && (categoryMatches || subCategoryMatches)) {
                    double discountPrice = this.sell_price * (1 - sale.getDiscountPrecent() / 100);
                    if (discountPrice < best) {best = discountPrice;}
                }
            }
        }
        this.best_price = best;
    }

    /**
     * Formats and prints product details to the console.
     * @param p The product instance to display.
     * @param showHeader If true, prints a formatted table header before the product data.
     */
    public void displayProduct(Product p, boolean showHeader) {
        if (showHeader) {
            System.out.println("\n" + String.format(
                    "%-6s | %-15s | %-8s | %-4s | %-4s | %-4s | %-4s | %-6s | %-6s | %-10s | %-10s | %-5s | %-8s",
                    "ID", "Product Name", "Cat", "Shop", "Whs", "Dmg", "Total", "Cost", "Price", "Expiry", "Added", "Loc", "SupID"));
            System.out.println("-".repeat(135));
        }
        System.out.printf(
                "%-6s | %-15.15s | %-8.8s | %-4d | %-4d | %-4d | %-4d | %-6.1f | %-6.1f | %-10s | %-10s | %-5s | %-8s\n",
                p.getProductID(), p.getProductName(), p.getCategory(), p.getShopQuantity(), p.getWarehouseQuantity(),
                p.getDamagedAmount(), p.getTotalQuantity(), p.getCost_price(), p.getBest_price(),
                p.getExpirationDate(), p.getEntryTime(), p.getAisle() + "-" + p.getShelf(),
                p.supplier.getSupplierID()
        );
    }}

