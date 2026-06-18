/**
 * Mock supplier entity.
 * Represents a supplier inside the mocked supplier system.
 *
 * Note:
 * discountRate is kept temporarily only for backward compatibility
 * with old project code. In the new design, supplier prices should be
 * handled by SupplyAgreement, not directly by Supplier.
 */
public class Supplier {
    private String supplierName;
    private String supplierID;

    // Temporary field for compatibility with old code.
    private double discountRate;

    public Supplier(String supplierName, String supplierID) {
        this.supplierName = supplierName;
        this.supplierID = supplierID;
        this.discountRate = 0;
    }

    // Temporary constructor for compatibility with old code.
    public Supplier(String supplierName, String supplierID, double discountRate) {
        this.supplierName = supplierName;
        this.supplierID = supplierID;
        this.discountRate = discountRate;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    // Temporary methods for compatibility with old code.
    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    public void displaySupplier(boolean showHeader) {
        if (showHeader) {
            System.out.println("\n" + "=".repeat(45));
            System.out.printf("%-10s | %-25s\n", "ID", "Supplier Name");
            System.out.println("-".repeat(45));
        }
        System.out.printf("%-10s | %-25.25s\n",
                this.supplierID,
                this.supplierName
        );
    }
    @Override
    public String toString() {
        return "Supplier{" +
                "supplierID='" + supplierID + '\'' +
                ", supplierName='" + supplierName + '\'' +
                '}';
    }
}