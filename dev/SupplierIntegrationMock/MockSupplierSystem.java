import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of the supplier system.
 *
 * This class simulates an external supplier system.
 * It keeps suppliers, supply agreements and delivery schedules in memory only.
 */
public class MockSupplierSystem {
    private List<Supplier> suppliers;
    private List<SupplyAgreement> agreements;
    private List<DeliverySchedule> schedules;
    private boolean shouldFailSending;

    public MockSupplierSystem() {
        this.suppliers = new ArrayList<>();
        this.agreements = new ArrayList<>();
        this.schedules = new ArrayList<>();
        this.shouldFailSending = false;
        initializeMockData();
    }

    public void initializeMockData() {
        Supplier supplier1 = new Supplier("Default Supplier", "S1");
        Supplier supplier2 = new Supplier("Backup Supplier", "S2");
        Supplier supplier3 = new Supplier("Cheap Supplier", "S3");
        suppliers.add(supplier1);
        suppliers.add(supplier2);
        suppliers.add(supplier3);

        /*
         * Dummy supply agreements.
         * For now, the mock does not have to be fully realistic.
         * Later, if needed, getBestOffer can use these agreements.
         */
        agreements.add(new SupplyAgreement(supplier1, "P1", 1, 10.0));
        agreements.add(new SupplyAgreement(supplier2, "P1", 1, 12.0));
        agreements.add(new SupplyAgreement(supplier3, "P1", 1, 9.0));
        agreements.add(new SupplyAgreement(supplier1, "P2", 1, 8.0));
        agreements.add(new SupplyAgreement(supplier2, "P2", 1, 7.5));
        schedules.add(new DeliverySchedule(supplier1, DayOfWeek.MONDAY, LocalTime.of(9, 0)));
        schedules.add(new DeliverySchedule(supplier2, DayOfWeek.WEDNESDAY, LocalTime.of(10, 30)));
        schedules.add(new DeliverySchedule(supplier3, DayOfWeek.THURSDAY, LocalTime.of(8, 30)));
    }

    public boolean addSupplier(Supplier supplier) {
        if (supplier == null) {
            return false;
        }
        if (findSupplierById(supplier.getSupplierID()) != null) {
            return false;
        }
        suppliers.add(supplier);
        return true;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public List<SupplyAgreement> getAgreements() {
        return agreements;
    }

    public List<DeliverySchedule> getSchedules() {
        return schedules;
    }

    public Supplier findSupplierById(String supplierId) {
        if (supplierId == null) {
            return null;
        }
        for (Supplier supplier : suppliers) {
            if (supplier.getSupplierID().equalsIgnoreCase(supplierId)) {
                return supplier;
            }
        }
        return null;
    }

    public List<Supplier> getSuppliersForProduct(String productId) {
        /*
         * Dummy function:
         * For now, return all suppliers as if all of them can supply the product.
         */
        return suppliers;
    }

    public SupplierOffer getBestOffer(String productId, int quantity) {
        /*
         * Dummy function:
         * The lecturer allows dummy functions when the requirement depends on another model.
         * For now, return a fixed supplier offer.
         */
        if (productId == null || quantity <= 0 || suppliers.isEmpty()) {
            return null;
        }
        Supplier selectedSupplier = suppliers.get(0);
        double dummyUnitPrice = 10.0;
        return new SupplierOffer(selectedSupplier, productId, quantity, dummyUnitPrice);
    }

    public List<DayOfWeek> getFixedDeliveryDays(String supplierId) {
        List<DayOfWeek> deliveryDays = new ArrayList<>();
        if (supplierId == null) {
            return deliveryDays;
        }
        for (DeliverySchedule schedule : schedules) {
            if (schedule.getSupplierId().equalsIgnoreCase(supplierId)) {
                deliveryDays.add(schedule.getDeliveryDay());
            }
        }
        return deliveryDays;
    }

    public boolean sendOrder(Order order) {
        /*
         * Dummy function:
         * Usually returns true, as if the supplier accepted the order.
         * shouldFailSending exists only for future tests.
         */
        if (shouldFailSending) {
            return false;}
        return order != null;
    }

    public void setShouldFailSending(boolean shouldFailSending) {
        this.shouldFailSending = shouldFailSending;
    }
}