public class ProductDTO {
    private String productId;
    private String productName;
    private String brand;
    private int categoryId;
    private int shopQuantity;
    private int warehouseQuantity;
    private int totalQuantity;
    private String aisle;
    private String shelf;
    private int minLimit;
    private int rank;
    private int deliveryTime;
    private double costPrice;
    private double sellPrice;
    private double bestPrice;
    private String entryDate;
    private String expirationDate;
    private int damagedAmount;
    private boolean active;

    public ProductDTO(String productId, String productName, String brand, int categoryId, int shopQuantity, int warehouseQuantity, int totalQuantity, String aisle, String shelf, int minLimit, int rank, int deliveryTime, double costPrice, double sellPrice, double bestPrice, String entryDate, String expirationDate, int damagedAmount, boolean active) {
        this.productId = productId;
        this.productName = productName;
        this.brand = brand;
        this.categoryId = categoryId;
        this.shopQuantity = shopQuantity;
        this.warehouseQuantity = warehouseQuantity;
        this.totalQuantity = totalQuantity;
        this.aisle = aisle;
        this.shelf = shelf;
        this.minLimit = minLimit;
        this.rank = rank;
        this.deliveryTime = deliveryTime;
        this.costPrice = costPrice;
        this.sellPrice = sellPrice;
        this.bestPrice = bestPrice;
        this.entryDate = entryDate;
        this.expirationDate = expirationDate;
        this.damagedAmount = damagedAmount;
        this.active = active;}

    public String getProductId() {
        return productId;}

    public String getProductName() {
        return productName;}

    public String getBrand() {
        return brand;}

    public int getCategoryId() {
        return categoryId;}

    public int getShopQuantity() {
        return shopQuantity;}

    public int getWarehouseQuantity() {
        return warehouseQuantity;}

    public int getTotalQuantity() {
        return totalQuantity;}

    public String getAisle() {
        return aisle;}

    public String getShelf() {
        return shelf;}

    public int getMinLimit() {
        return minLimit;}

    public int getRank() {
        return rank;}

    public int getDeliveryTime() {
        return deliveryTime;}

    public double getCostPrice() {
        return costPrice;}

    public double getSellPrice() {
        return sellPrice;}

    public double getBestPrice() {
        return bestPrice;}

    public String getEntryDate() {
        return entryDate;}

    public String getExpirationDate() {
        return expirationDate;}

    public int getDamagedAmount() {
        return damagedAmount;}

    public boolean isActive() {
        return active;}
}