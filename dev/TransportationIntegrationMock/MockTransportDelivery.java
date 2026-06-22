package TransportationIntegrationMock;

import DomainLayer.Shift;

import java.time.LocalDate;

public class MockTransportDelivery {
    private final int deliveryId;
    private final int branchId;
    private final LocalDate date;
    private final Shift.ShiftType shiftType;
    private final MockDriverLicenseType requiredLicenseType;
    private final String destination;
    private String assignedDriverId;

    public MockTransportDelivery(int deliveryId, int branchId, LocalDate date,
                                 Shift.ShiftType shiftType, MockDriverLicenseType requiredLicenseType,
                                 String destination) {
        if (deliveryId <= 0) {
            throw new IllegalArgumentException("delivery id must be positive");
        }
        if (branchId <= 0) {
            throw new IllegalArgumentException("branch id must be positive");
        }
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        if (shiftType == null) {
            throw new IllegalArgumentException("shift type cannot be null");
        }
        if (requiredLicenseType == null) {
            throw new IllegalArgumentException("required license type cannot be null");
        }
        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("destination cannot be empty");
        }

        this.deliveryId = deliveryId;
        this.branchId = branchId;
        this.date = date;
        this.shiftType = shiftType;
        this.requiredLicenseType = requiredLicenseType;
        this.destination = destination.trim();
        this.assignedDriverId = null;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public int getBranchId() {
        return branchId;
    }

    public LocalDate getDate() {
        return date;
    }

    public Shift.ShiftType getShiftType() {
        return shiftType;
    }

    public MockDriverLicenseType getRequiredLicenseType() {
        return requiredLicenseType;
    }

    public String getDestination() {
        return destination;
    }

    public String getAssignedDriverId() {
        return assignedDriverId;
    }

    public boolean needsDriver() {
        return assignedDriverId == null;
    }

    public void assignDriver(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException("employee id cannot be empty");
        }

        this.assignedDriverId = employeeId.trim();
    }

    public void unassignDriver() {
        this.assignedDriverId = null;
    }

    @Override
    public String toString() {
        return "MockTransportDelivery{" +
                "deliveryId=" + deliveryId +
                ", branchId=" + branchId +
                ", date=" + date +
                ", shiftType=" + shiftType +
                ", requiredLicenseType=" + requiredLicenseType +
                ", destination='" + destination + '\'' +
                ", assignedDriverId='" + assignedDriverId + '\'' +
                '}';
    }
}
