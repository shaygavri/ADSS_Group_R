import java.util.ArrayList;
import java.util.List;

/**
 * Service Layer for managing supplier-related operations.
 * This class handles validation logic for supplier credentials and discount rates
 * before persisting data to the domain layer.
 */

public class SupplierService {
    private DomainController domain;
    public SupplierService(DomainController domain) {
        this.domain = domain;
    }
    /**
     * Utility method to log validation errors for specific fields.
     * @param fieldName The name of the invalid or missing field.
     * @return Always returns false to indicate a failed operation.
     */
    private boolean errorType(String fieldName) {
        System.out.println("'" + fieldName + "' is invalid or missing.");
        return false; }
    /**
     * Validates and adds a new supplier to the system.
     * Ensures that the name and ID are provided and that the discount rate
     * is within a valid range (0-100). Whitespace is trimmed from the name
     * and ID before storage.
     * @param name The name of the supplier.
     * @param id The unique identifier for the supplier.
     * @param rate The default discount rate provided by this supplier.
     * @return true if the supplier was successfully added, false otherwise.
     */
    public boolean addNewSup(String name, String id, double rate) {
        if (name == null || name.isEmpty()) return errorType("Supplier Name");
        if (id == null || id.isEmpty()) return errorType("Supplier ID");
        if (rate < 0 || rate > 100) {
            System.out.println("Discount rate must be between 0 and 100.");
            return false;
        }
        return domain.addNewSup(name.trim(), id.trim(), rate);
    }

    /**
     * Updates the discount rate for an existing supplier.
     * @param id The unique identifier of the supplier.
     * @param discount The new discount rate (must be between 0 and 100).
     * @return true if the update was successful, false if the ID was invalid or out of range.
     */
    public boolean updateSupDiscountRate(String id, double discount) {
        if (id == null || id.isEmpty()) return errorType("Supplier ID");
        if (discount < 0 || discount > 100) {
            System.out.println("Discount rate must be between 0 and 100.");
            return false;
        }
        return domain.updateSupDicountRate(id, discount);
    }
    /**
     * Retrieves a complete list of all suppliers registered in the system.
     * @return A list of Supplier objects, or an empty list if none are found.
     */
    public List<Supplier> getAllSuppliers() {
        List<Supplier> sups = domain.displayAllSup();
        if (sups == null || sups.isEmpty()) {
            return new ArrayList<>();
        }
        return sups;
    }
}
