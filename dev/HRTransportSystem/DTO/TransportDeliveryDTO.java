package DTO;

import java.time.LocalDate;

public class TransportDeliveryDTO {
    private int deliveryId;
    private int branchId;
    private LocalDate date;
    private String shiftType;
    private String requiredLicenseType;
    private String destination;
    private String assignedDriverId;

    public TransportDeliveryDTO(int deliveryId, int branchId, LocalDate date, String shiftType,
                                String requiredLicenseType, String destination, String assignedDriverId) {
        this.deliveryId = deliveryId;
        this.branchId = branchId;
        this.date = date;
        this.shiftType = shiftType;
        this.requiredLicenseType = requiredLicenseType;
        this.destination = destination;
        this.assignedDriverId = assignedDriverId;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }

    public String getRequiredLicenseType() {
        return requiredLicenseType;
    }

    public void setRequiredLicenseType(String requiredLicenseType) {
        this.requiredLicenseType = requiredLicenseType;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getAssignedDriverId() {
        return assignedDriverId;
    }

    public void setAssignedDriverId(String assignedDriverId) {
        this.assignedDriverId = assignedDriverId;
    }
}
