import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Represents a fixed delivery schedule for a supplier in the mock system.
 */
public class DeliverySchedule {
    private Supplier supplier;
    private DayOfWeek deliveryDay;
    private LocalTime deliveryTime;

    public DeliverySchedule(Supplier supplier, DayOfWeek deliveryDay, LocalTime deliveryTime) {
        this.supplier = supplier;
        this.deliveryDay = deliveryDay;
        this.deliveryTime = deliveryTime;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public String getSupplierId() {
        if (supplier == null) {
            return null;
        }

        return supplier.getSupplierID();
    }

    public DayOfWeek getDeliveryDay() {
        return deliveryDay;
    }

    public LocalTime getDeliveryTime() {
        return deliveryTime;
    }

    @Override
    public String toString() {
        return "DeliverySchedule{" +
                "supplier=" + supplier.getSupplierName() +
                ", deliveryDay=" + deliveryDay +
                ", deliveryTime=" + deliveryTime +
                '}';
    }
}