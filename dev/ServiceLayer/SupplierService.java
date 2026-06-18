import java.util.ArrayList;
import java.util.List;

/**
 * Service Layer for managing supplier-related operations.
 *
 * In the current iteration, suppliers are managed by MockSupplierSystem
 * and not by DomainController.
 */
public class SupplierService {
    private MockSupplierSystem supplierSystem;

    public SupplierService(MockSupplierSystem supplierSystem) {
        this.supplierSystem = supplierSystem;
    }

    private boolean errorType(String fieldName) {
        System.out.println("'" + fieldName + "' is invalid or missing.");
        return false;
    }

    /**
     * Adds a new supplier to the mocked supplier system.
     *
     * The discount rate is kept in the method signature only for backward
     * compatibility with the existing UI.
     * In the new design, supplier prices should be handled by SupplyAgreement.
     */
    public boolean addNewSup(String name, String id, double rate) {
        if (name == null || name.trim().isEmpty()) {
            return errorType("Supplier Name");
        }

        if (id == null || id.trim().isEmpty()) {
            return errorType("Supplier ID");
        }

        if (rate < 0 || rate > 100) {
            System.out.println("Discount rate must be between 0 and 100.");
            return false;
        }

        Supplier supplier = new Supplier(name.trim(), id.trim(), rate);
        return supplierSystem.addSupplier(supplier);
    }

    /**
     * Temporary method for backward compatibility with the existing menu.
     *
     * In the new design, supplier discount should not be updated directly on Supplier.
     * Prices should be represented through SupplyAgreement.
     */
    public boolean updateSupDiscountRate(String id, double discount) {
        if (id == null || id.trim().isEmpty()) {
            return errorType("Supplier ID");
        }

        if (discount < 0 || discount > 100) {
            System.out.println("Discount rate must be between 0 and 100.");
            return false;
        }

        Supplier supplier = supplierSystem.findSupplierById(id.trim());

        if (supplier == null) {
            System.out.println("Supplier was not found.");
            return false;
        }

        supplier.setDiscountRate(discount);
        return true;
    }

    /**
     * Returns all suppliers from the mocked supplier system.
     */
    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = supplierSystem.getSuppliers();

        if (suppliers == null || suppliers.isEmpty()) {
            return new ArrayList<>();
        }

        return suppliers;
    }

    public Supplier findSupplierById(String supplierId) {
        if (supplierId == null || supplierId.trim().isEmpty()) {
            return null;
        }

        return supplierSystem.findSupplierById(supplierId.trim());
    }

    public MockSupplierSystem getSupplierSystem() {
        return supplierSystem;
    }
}