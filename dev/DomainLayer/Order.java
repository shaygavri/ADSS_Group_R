import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private List<OrderItem> items;
    private String supplierId;
    private String supplierName;
    private double totalPrice;
    private OrderStatus status;
    private OrderType orderType;
    private LocalDate creationDate;
    private LocalDate expectedDeliveryDate;

    public Order(String orderId, List<OrderItem> items, String supplierId, String supplierName, OrderType orderType, LocalDate expectedDeliveryDate) {
        this.orderId = orderId;
        this.items = items != null ? items : new ArrayList<>();
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.orderType = orderType;
        this.creationDate = LocalDate.now();
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.status = OrderStatus.DRAFT;
        this.totalPrice = calculateTotalPrice();}

    public String getOrderId() {
        return orderId;}

    public List<OrderItem> getItems() {
        return items;}

    public String getSupplierId() {
        return supplierId;}

    public String getSupplierName() {
        return supplierName;}

    public double getTotalPrice() {
        return totalPrice;}
    public OrderStatus getStatus() {
        return status;}

    public OrderType getOrderType() {
        return orderType;}

    public LocalDate getCreationDate() {
        return creationDate;}

    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;}

    public void setOrderId(String orderId) {
        this.orderId = orderId;}

    public void setItems(List<OrderItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        this.totalPrice = calculateTotalPrice();}
    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;}
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;}
    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;}
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;}

    public void addItem(OrderItem item) {
        if (item != null) {
            items.add(item);
            totalPrice = calculateTotalPrice();}
    }

    public void changeStatus(OrderStatus status) {
        this.status = status;}
    public double calculateTotalPrice() {
        double sum = 0;
        for (OrderItem item : items) {
            sum += item.calculateTotalPrice();
        }
        return sum;}
    public void refreshTotalPrice() {
        this.totalPrice = calculateTotalPrice();}
    public boolean isDraft() {
        return status == OrderStatus.DRAFT;}
    public boolean isSent() {
        return status == OrderStatus.SENT;}
    public boolean isReceived() {
        return status == OrderStatus.RECEIVED;}
    public boolean isCancelled() {
        return status == OrderStatus.CANCELLED;}
    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", supplierId='" + supplierId + '\'' +
                ", supplierName='" + supplierName + '\'' +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                ", orderType=" + orderType +
                ", creationDate=" + creationDate +
                ", expectedDeliveryDate=" + expectedDeliveryDate +
                ", items=" + items +
                '}';
    }
}