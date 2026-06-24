import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service Layer for managing store sales and promotions.
 * This class handles validation for discount rates and dates, and ensures
 * that sales are correctly applied across the inventory.
 */
public class SaleService {
    private DomainController domain;


    public SaleService(DomainController domain) {
        this.domain = domain;
    }
    /**
     * Utility method to print a standard error message for invalid fields.
     * @param fieldName The name of the field that failed validation.
     * @return false always, to indicate a failed operation.
     */
    private boolean errorType(String fieldName) {
        System.out.println("'" + fieldName + "' is invalid or missing.");
        return false;
    }

    /**
     * Validates and adds a new sale promotion to the system.
     * Checks for valid ID, ensures the discount rate is between 1-100%,
     * and verifies that the sale period is chronological. If the sale is
     * successfully added, it triggers a global price update for all products.
     * @param id The unique identifier for the sale.
     * @param category The target category for the discount.
     * @param subCat The target sub-category for the discount.
     * @param rate The discount percentage (1-100).
     * @param start The effective start date of the sale.
     * @param end The expiration date of the sale.
     * @return true if the sale was added and applied, false otherwise.
     */
    public boolean addNewSale(String id, String category, String subCat, double rate, LocalDate start, LocalDate end) {
        if (id == null || id.isEmpty()) return errorType("Sale ID");
        if (rate <= 0 || rate > 100) {
            System.out.println("Discount rate must be between 1 and 100.");
            return false;
        }
        if (end.isBefore(start)) {
            System.out.println("End date cannot be before start date.");
            return false;
        }
        boolean added = domain.addSale(id, category, subCat, rate, start, end);
        if (added) {
            domain.applyAllSalesToAllProducts();
            return true;
        }
        return false;
    }
    /**
     * Retrieves a list of all active or recorded sales in the store.
     * @return A list of Sale objects, or an empty list if none exist.
     */
    public List<Sale> getAllSales() {
        List<Sale> allSales = domain.displayAllStoreSales();
        if (allSales == null || allSales.isEmpty()) {
            return new ArrayList<>();
        }
        return allSales;
    }
}


