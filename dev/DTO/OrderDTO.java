public class OrderDTO {
    private String orderId;
    private String supplierId;
    private String supplierName;
    private String status;
    private String orderType;
    private String creationDate;
    private String expectedDeliveryDate;
    private double totalPrice;
    public OrderDTO(String orderId, String supplierId, String supplierName, String status, String orderType, String creationDate, String expectedDeliveryDate, double totalPrice) {
        this.orderId = orderId;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.status = status;
        this.orderType = orderType;
        this.creationDate = creationDate;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.totalPrice = totalPrice;}

    public String getOrderId() {
        return orderId;}

    public String getSupplierId() {
        return supplierId;}

    public String getSupplierName() {
        return supplierName;}

    public String getStatus() {
        return status;}

    public String getOrderType() {
        return orderType;}

    public String getCreationDate() {
        return creationDate;}

    public String getExpectedDeliveryDate() {
        return expectedDeliveryDate;}

    public double getTotalPrice() {
        return totalPrice;}
}