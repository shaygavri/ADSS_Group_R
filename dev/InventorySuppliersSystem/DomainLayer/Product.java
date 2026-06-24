import java.time.LocalDate;
import java.util.List;

public class Product {
    private String productID;
    private String productName;
    private String brand;
    private Category category;
    private int shopQuantity;
    private int warehouseQuantity;
    private int totalQuantity;
    private String aisle;
    private String shelf;
    private int min_limit;
    private int rank;
    private int deliveryTime;
    private double cost_price;
    private double sell_price;
    private double best_price;
    private LocalDate entryDate = LocalDate.now();
    private LocalDate expirationDate;
    private int damagedAmount;
    private Boolean isActive = true;

    public Product(String productID, String productName, String brand, Category category, int shopQuantity, int warehouseQuantity, int rank, int deliveryTime, double cost_price, LocalDate expirationDate, int damagedAmount, String aisle, String shelf) {
        this.productID = productID;
        this.productName = productName;
        this.brand = brand;
        this.category = category;
        this.shopQuantity = shopQuantity;
        this.warehouseQuantity = warehouseQuantity;
        this.totalQuantity = shopQuantity + warehouseQuantity;
        this.aisle = aisle;
        this.shelf = shelf;
        this.rank = rank;
        this.deliveryTime = deliveryTime;
        this.min_limit = rank * deliveryTime;
        this.cost_price = cost_price;
        this.expirationDate = expirationDate;
        this.damagedAmount = damagedAmount;
        this.sell_price = costPriceAfterDiscount() * 1.35;
        this.best_price = sell_price;}

    public String getProductID() {
        return productID;}

    public String getProductName() {
        return productName;}

    public String getBrand() {
        return brand;}

    public String getCategory() {
        return category.getCategoryName();}

    public String getSub_cat_name() {
        return category.getSubCategoryName();}

    public String getSubSubCategory() {
        return category.getSubSubCategoryName();}

    public Category getCategoryObject() {
        return category;}

    public int getShopQuantity() {
        return shopQuantity;}

    public int getWarehouseQuantity() {
        return warehouseQuantity;}

    public String getAisle() {
        return aisle;}

    public String getShelf() {
        return shelf;}

    public int getMin_limit() {
        return min_limit;}

    public double getCost_price() {
        return cost_price;}

    public double getBest_price() {
        return best_price;}

    public int getDamagedAmount() {
        return damagedAmount;}

    public LocalDate getExpirationDate() {
        return expirationDate;}

    public String getSize() {
        return category.getSubSubCategoryName();}

    public LocalDate getEntryTime() {
        return entryDate;}
    public int getRank() {return rank;}
    public int getDeliveryTime() {return deliveryTime;}
    public double getSell_price() {return sell_price;}
    public void setBest_price(double best_price) {this.best_price = best_price;}
    public Boolean getIsActive() {return isActive;}
    public int getTotalQuantity() {return totalQuantity;}
    public void setTotalQuantity(int shopQuantity, int warehouseQuantity) {
        this.totalQuantity = shopQuantity + warehouseQuantity;}

    public void setShopQuantity(int shopQuantity) {
        this.shopQuantity = shopQuantity;}

    public void setWarehouseQuantity(int warehouseQuantity) {
        this.warehouseQuantity = warehouseQuantity;}

    public void setSell_price(double sell_price) {
        this.sell_price = sell_price;}

    public void setActive(Boolean active) {
        isActive = active;}

    public void setProductName(String productName) {
        this.productName = productName;}

    public void setDamagedAmount(int damagedAmount) {
        this.damagedAmount = damagedAmount;}

    public double costPriceAfterDiscount() {
        return this.cost_price;}

    public void updatePriceAfterDiscount(List<Sale> saleList) {
        double best = this.sell_price;
        if (saleList != null) {
            for (Sale sale : saleList) {
                boolean categoryMatches = sale.getTarget_category().equalsIgnoreCase(getCategory());
                boolean subCategoryMatches = sale.getTarget_subcat().equalsIgnoreCase(getSub_cat_name());
                if (sale.isSaleActive() && (categoryMatches || subCategoryMatches)) {
                    double discountPrice = this.sell_price * (1 - sale.getDiscountPrecent() / 100);

                    if (discountPrice < best) {
                        best = discountPrice;
                    }
                }
            }
        }

        this.best_price = best;}
    public void displayProduct(Product p, boolean showHeader) {
        String headerFormat =
                "%-16s | %-18s | %-10s | %5s | %5s | %5s | %5s | %7s | %7s | %-10s | %-10s | %-5s%n";

        String rowFormat =
                "%-16s | %-18.18s | %-10.10s | %5d | %5d | %5d | %5d | %7.1f | %7.1f | %-10s | %-10s | %-5s%n";

        if (showHeader) {
            System.out.printf(
                    "\n" + headerFormat,
                    "ID",
                    "Product Name",
                    "Cat",
                    "Shop",
                    "Whs",
                    "Dmg",
                    "Total",
                    "Cost",
                    "Price", "Expiry", "Added", "Loc"
            );
            System.out.println("-".repeat(150));}
        System.out.printf(
                rowFormat,
                p.getProductID(),
                p.getProductName(),
                p.getCategory(),
                p.getShopQuantity(),
                p.getWarehouseQuantity(),
                p.getDamagedAmount(),
                p.getTotalQuantity(),
                p.getCost_price(),
                p.getBest_price(),
                p.getExpirationDate(),
                p.getEntryTime(),
                p.getAisle() + "-" + p.getShelf());
    }
}