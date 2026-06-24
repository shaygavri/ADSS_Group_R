/**
 * Represents a supply agreement in the mocked supplier system.
 * It defines which supplier can provide which product and at what unit price.
 */
public class SupplyAgreement {
    private Supplier supplier;
    private String productId;
    private int minQuantity;
    private double unitPrice;

    public SupplyAgreement(Supplier supplier, String productId, int minQuantity, double unitPrice) {
        this.supplier = supplier;
        this.productId = productId;
        this.minQuantity = minQuantity;
        this.unitPrice = unitPrice;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public String getProductId() {
        return productId;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public boolean canSupply(String productId, int quantity) {
        if (productId == null) {
            return false;
        }
        return this.productId.equalsIgnoreCase(productId);
    }

    public double calculatePrice(int quantity) {
        return quantity * unitPrice;
    }

    @Override
    public String toString() {
        return "SupplyAgreement{" +
                "supplier=" + supplier.getSupplierName() +
                ", productId='" + productId + '\'' +
                ", minQuantity=" + minQuantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}