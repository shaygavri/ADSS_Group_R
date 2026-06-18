public class OrderItem {
    private Product product;
    private int quantity;
    private double unitPrice;

    public OrderItem(Product product, int quantity, double unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;}
    public Product getProduct() {
        return product;}

    public String getProductId() {
        if (product == null) {
            return null;}
        return product.getProductID();
    }

    public String getProductName() {
        if (product == null) {return "Unknown product";}
        return product.getProductName();
    }
    public int getQuantity() {
        return quantity;
    }
    public double getUnitPrice() {
        return unitPrice;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    public double calculateTotalPrice() {
        return quantity *unitPrice;
    }
    @Override
    public String toString() {
        return "OrderItem{" +
                "productId='" + getProductId() + '\'' +
                ", productName='" + getProductName() + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + calculateTotalPrice() +
                '}';
    }
}