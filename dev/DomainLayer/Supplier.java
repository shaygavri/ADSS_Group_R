
/**
 * Represents a business entity that provides products to the store.
 * This class stores the supplier's identifying information and their
 * specific discount rate, which is used to calculate the actual cost
 * of products in the inventory.
 */

public class Supplier {
    private String supplierName;
    private String supplierID;
    private double discountRate;

    /**
     * Constructs a new Supplier with identifying details and a pricing rule.
     * @param supplierName The official name of the supplier.
     * @param supplierID A unique string identifier for the supplier.
     * @param discountRate The percentage discount provided by this supplier (e.g., 0.15 for 15%).
     */
    public Supplier(String supplierName, String supplierID, double discountRate) {
        this.supplierName = supplierName;
        this.supplierID = supplierID;
        this.discountRate = discountRate;
    }
    /** Get and Set methods */
    public String getSupplierName() {
        return supplierName;
    }
    public String getSupplierID() {
        return supplierID;
    }
    public double getDiscountRate() {
        return discountRate;
    }
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    /**
     * Formats and prints the supplier's information to the console.
     * @param showHeader If true, prints a formatted table header before the supplier data.
     */
    public void displaySupplier(boolean showHeader) {
       if (showHeader) {
           System.out.println("\n" + "=".repeat(55));
           System.out.printf("%-10s | %-20s | %-15s\n", "ID", "Supplier Name", "Discount Rate");
           System.out.println("-".repeat(55));
       }
       System.out.printf("%-10s | %-20.20s | %-10.1f%%\n",
               this.supplierID,
               this.supplierName,
               this.discountRate);
    }
}
