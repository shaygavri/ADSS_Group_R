package TransportationIntegrationMock;

import DomainLayer.Shift;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MockTransportController {
    private static MockTransportController instance;

    private final Map<String, MockTransportDriver> driversByEmployeeId;
    private final Map<Integer, MockTransportDelivery> deliveriesById;

    private MockTransportController() {
        this.driversByEmployeeId = new LinkedHashMap<>();
        this.deliveriesById = new LinkedHashMap<>();
    }

    public static MockTransportController getInstance() {
        if (instance == null) {
            instance = new MockTransportController();
        }
        return instance;
    }

    public void reset() {
        driversByEmployeeId.clear();
        deliveriesById.clear();
    }

    public boolean registerOrUpdateDriver(String employeeId, String employeeUserName, int branchId,
                                          MockDriverLicenseType licenseType) {
        String normalizedEmployeeId = normalizeEmployeeId(employeeId);

        MockTransportDriver existingDriver = driversByEmployeeId.get(normalizedEmployeeId);
        if (existingDriver != null) {
            existingDriver.updateDetails(employeeUserName, branchId, licenseType);
            existingDriver.activate();
            return false;
        }

        MockTransportDriver driver = new MockTransportDriver(
                normalizedEmployeeId,
                employeeUserName,
                branchId,
                licenseType
        );
        driversByEmployeeId.put(normalizedEmployeeId, driver);
        return true;
    }

    public boolean removeDriver(String employeeId) {
        MockTransportDriver driver = getDriver(employeeId);
        if (driver == null) {
            return false;
        }

        driver.deactivate();
        unassignDriverFromAllDeliveries(driver.getEmployeeId());
        return true;
    }

    public MockTransportDriver getDriver(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            return null;
        }

        return driversByEmployeeId.get(employeeId.trim());
    }

    public boolean isRegisteredDriver(String employeeId) {
        MockTransportDriver driver = getDriver(employeeId);
        return driver != null && driver.isActive();
    }

    public boolean addDelivery(MockTransportDelivery delivery) {
        if (delivery == null) {
            throw new IllegalArgumentException("delivery cannot be null");
        }
        if (deliveriesById.containsKey(delivery.getDeliveryId())) {
            return false;
        }

        deliveriesById.put(delivery.getDeliveryId(), delivery);
        return true;
    }

    public boolean createDelivery(int deliveryId, int branchId, LocalDate date,
                                  Shift.ShiftType shiftType, MockDriverLicenseType requiredLicenseType,
                                  String destination) {
        return addDelivery(new MockTransportDelivery(
                deliveryId,
                branchId,
                date,
                shiftType,
                requiredLicenseType,
                destination
        ));
    }

    public MockTransportDelivery getDelivery(int deliveryId) {
        return deliveriesById.get(deliveryId);
    }

    public boolean removeDelivery(int deliveryId) {
        return deliveriesById.remove(deliveryId) != null;
    }

    public List<MockTransportDelivery> getDeliveriesForShift(int branchId, LocalDate date, Shift.ShiftType shiftType) {
        ArrayList<MockTransportDelivery> result = new ArrayList<>();
        if (branchId <= 0 || date == null || shiftType == null) {
            return result;
        }

        for (MockTransportDelivery delivery : deliveriesById.values()) {
            if (delivery.getBranchId() == branchId
                    && delivery.getDate().equals(date)
                    && delivery.getShiftType() == shiftType) {
                result.add(delivery);
            }
        }

        return result;
    }

    public List<MockTransportDelivery> getOpenDeliveriesForShift(int branchId, LocalDate date,
                                                                 Shift.ShiftType shiftType) {
        ArrayList<MockTransportDelivery> result = new ArrayList<>();
        for (MockTransportDelivery delivery : getDeliveriesForShift(branchId, date, shiftType)) {
            if (delivery.needsDriver()) {
                result.add(delivery);
            }
        }
        return result;
    }

    public List<MockTransportDelivery> getNextDeliveries(LocalDate nextWeekStartDate) {
        ArrayList<MockTransportDelivery> result = new ArrayList<>();
        if (nextWeekStartDate == null) {
            return result;
        }

        LocalDate nextWeekEndDate = nextWeekStartDate.plusDays(6);
        for (MockTransportDelivery delivery : deliveriesById.values()) {
            if (!delivery.getDate().isBefore(nextWeekStartDate)
                    && !delivery.getDate().isAfter(nextWeekEndDate)) {
                result.add(delivery);
            }
        }

        return result;
    }

    public List<MockTransportDelivery> getNextDeliveries(int branchId, LocalDate nextWeekStartDate) {
        ArrayList<MockTransportDelivery> result = new ArrayList<>();
        if (branchId <= 0 || nextWeekStartDate == null) {
            return result;
        }

        for (MockTransportDelivery delivery : getNextDeliveries(nextWeekStartDate)) {
            if (delivery.getBranchId() == branchId) {
                result.add(delivery);
            }
        }

        return result;
    }

    public boolean hasDeliveriesForShift(int branchId, LocalDate date, Shift.ShiftType shiftType) {
        return !getDeliveriesForShift(branchId, date, shiftType).isEmpty();
    }

    public boolean hasOpenDeliveriesForShift(int branchId, LocalDate date, Shift.ShiftType shiftType) {
        return !getOpenDeliveriesForShift(branchId, date, shiftType).isEmpty();
    }

    public int countOpenDeliveriesForShift(int branchId, LocalDate date, Shift.ShiftType shiftType) {
        return getOpenDeliveriesForShift(branchId, date, shiftType).size();
    }

    public boolean assignDriverToDelivery(int deliveryId, String employeeId) {
        MockTransportDelivery delivery = getDelivery(deliveryId);
        MockTransportDriver driver = getDriver(employeeId);

        if (delivery == null || driver == null || !driver.isActive()) {
            return false;
        }
        if (delivery.getBranchId() != driver.getBranchId()) {
            return false;
        }
        if (!driver.canDrive(delivery.getRequiredLicenseType())) {
            return false;
        }

        delivery.assignDriver(driver.getEmployeeId());
        return true;
    }

    public boolean canAssignDriverToDelivery(int deliveryId, String employeeId) {
        MockTransportDelivery delivery = getDelivery(deliveryId);
        MockTransportDriver driver = getDriver(employeeId);

        return delivery != null
                && driver != null
                && driver.isActive()
                && delivery.getBranchId() == driver.getBranchId()
                && driver.canDrive(delivery.getRequiredLicenseType());
    }

    public boolean unassignDriverFromDelivery(int deliveryId) {
        MockTransportDelivery delivery = getDelivery(deliveryId);
        if (delivery == null || delivery.needsDriver()) {
            return false;
        }

        delivery.unassignDriver();
        return true;
    }

    public ArrayList<MockTransportDriver> getAllDrivers() {
        return new ArrayList<>(driversByEmployeeId.values());
    }

    public ArrayList<MockTransportDelivery> getAllDeliveries() {
        return new ArrayList<>(deliveriesById.values());
    }

    private void unassignDriverFromAllDeliveries(String employeeId) {
        for (MockTransportDelivery delivery : deliveriesById.values()) {
            if (employeeId.equals(delivery.getAssignedDriverId())) {
                delivery.unassignDriver();
            }
        }
    }

    private String normalizeEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException("employee id cannot be empty");
        }

        return employeeId.trim();
    }
}
