import java.time.LocalDate;

/**
 * Represents a promotional sale or discount event within the store.
 * This class defines the scope of a sale (by category or sub-category),
 * the discount percentage, and the specific timeframe during which the
 * promotion is valid.
 */
public class Sale {
    private String saleID;
    private String target_subcat;
    private String target_category;
    private double discountPrecent;
    private LocalDate startDate;
    private LocalDate endDate;

    public Sale(String saleID, String target_subcat, String target_category, double discountPrecent,LocalDate startDate, LocalDate endDate) {
        this.saleID = saleID;
        this.target_subcat = target_subcat;
        this.target_category = target_category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discountPrecent =  discountPrecent ;
    }

    /**
     * Checks if the sale is currently valid based on the system's local date.
     * A sale is active if today's date falls within the inclusive range
     * of the start and end dates.
     * @return true if the sale is active today, false otherwise.
     */
    public boolean isSaleActive() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }
    /** Get methods */
    public double getDiscountPrecent() {
        return discountPrecent;
    }
    public String getTarget_category() {
        return target_category;
    }
    public String getTarget_subcat() {
        return target_subcat;
    }
    public String getSaleID() {return saleID;}

    public void setEndDate(LocalDate endDate) {
        if (endDate.isAfter(this.startDate))  {
            this.endDate = endDate;
        }
    }

    /**
     * Formats and prints the sale's details to the console.
     * @param showHeader If true, prints a formatted table header before the sale data.
     */
    public void displaySale(boolean showHeader){
        if (showHeader) {
            System.out.println("\n" + "=".repeat(85));
            System.out.printf("%-10s | %-12s | %-12s | %-8s | %-12s | %-12s\n", "ID", "Category", "Sub-Cat", "Disc%", "Start Date", "End Date");
            System.out.println("-".repeat(85));
        }
        System.out.printf("%-10s | %-12s | %-12s | %-7.1f%% | %-12s | %-12s\n",
                this.saleID, this.target_category, this.target_subcat, this.discountPrecent, this.startDate, this.endDate);
    }
}
