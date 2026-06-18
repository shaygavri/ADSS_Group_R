public class OrderItemDTO {
    private String orderItemId;
    private String orderId;
    private String productId;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    public OrderItemDTO(String orderItemId, String orderId, String productId, int quantity, double unitPrice, double totalPrice) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }
    public String getOrderItemId() {
        return orderItemId;}
    public String getOrderId() {
        return orderId;}
    public String getProductId() {
        return productId;}
    public int getQuantity() {
        return quantity;}
    public double getUnitPrice() {
        return unitPrice;}
    public double getTotalPrice() {
        return totalPrice;}
}