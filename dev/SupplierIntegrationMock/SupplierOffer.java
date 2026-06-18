/**
 * Represents a calculated supplier offer.
 * In the current mock implementation, this can be a dummy offer.
 */
public class SupplierOffer {
    private Supplier supplier;
    private String productId;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    public SupplierOffer(Supplier supplier, String productId, int quantity, double unitPrice) {
        this.supplier = supplier;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public String getSupplierId() {
        if (supplier == null) {
            return null;}
        return supplier.getSupplierID();
    }
    public String getSupplierName() {
        if (supplier == null) {
            return "Unknown supplier";
        }
        return supplier.getSupplierName();
    }

    public String getProductId() {
        return productId;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getUnitPrice() {
        return unitPrice;
    }
    public double getTotalPrice() {
        return totalPrice;
    }

    @Override
    public String toString() {
        return "SupplierOffer{" +
                "supplierId='" + getSupplierId() + '\'' +
                ", supplierName='" + getSupplierName() + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                '}';
    }
}