package ServiceLayer;

import DomainLayer.Employee;
import DomainLayer.Shift;
import DomainLayer.TransportationIntegrationController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TransportationIntegrationService {
    private final TransportationIntegrationController integrationController;

    public TransportationIntegrationService() {
        this.integrationController = TransportationIntegrationController.getInstance();
    }

    public boolean registerEmployeeAsDriver(String userName, String licenseTypeStr) {
        Employee.DriverLicenseType licenseType = parseLicenseType(licenseTypeStr);
        if (licenseType == null) {
            System.out.println("license type must be B, C1, C or CE");
            return false;
        }

        if (!integrationController.registerEmployeeAsDriver(userName, licenseType)) {
            System.out.println("could not register employee as driver");
            return false;
        }

        System.out.println("employee registered as driver successfully");
        return true;
    }

    public boolean changeEmployeeDriverLicenseType(String userName, String newLicenseTypeStr) {
        Employee.DriverLicenseType newLicenseType = parseLicenseType(newLicenseTypeStr);
        if (newLicenseType == null) {
            System.out.println("license type must be B, C1, C or CE");
            return false;
        }

        if (!integrationController.changeEmployeeDriverLicenseType(userName, newLicenseType)) {
            System.out.println("could not change employee driver license type");
            return false;
        }

        System.out.println("employee driver license type changed successfully");
        return true;
    }

    public boolean addTransportDelivery(String deliveryIdStr, String branchIdStr, String dateStr,
                                        String shiftTypeStr, String licenseTypeStr, String destination) {
        int deliveryId;
        int branchId;
        LocalDate date;
        Shift.ShiftType shiftType;
        Employee.DriverLicenseType licenseType;

        try {
            deliveryId = Integer.parseInt(deliveryIdStr.trim());
            branchId = Integer.parseInt(branchIdStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("delivery id and branch id must be numbers");
            return false;
        }

        try {
            date = LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            System.out.println("date must be in yyyy-mm-dd format");
            return false;
        }

        try {
            shiftType = Shift.ShiftType.valueOf(shiftTypeStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("shift type must be MORNING or EVENING");
            return false;
        }

        licenseType = parseLicenseType(licenseTypeStr);
        if (licenseType == null) {
            System.out.println("license type must be B, C1, C or CE");
            return false;
        }

        if (!integrationController.addTransportDelivery(
                deliveryId, branchId, date, shiftType, licenseType, destination
        )) {
            System.out.println("could not add transport delivery");
            return false;
        }

        System.out.println("transport delivery added successfully");
        return true;
    }

    public boolean assignDriverToDelivery(String userName, String deliveryIdStr) {
        int deliveryId;

        try {
            deliveryId = Integer.parseInt(deliveryIdStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("delivery id must be a number");
            return false;
        }

        if (!integrationController.assignDriverToDelivery(userName, deliveryId)) {
            System.out.println("could not assign driver to delivery");
            return false;
        }

        System.out.println("driver assigned to delivery successfully");
        return true;
    }

    public void showDeliveryIntegrationStatus(String deliveryIdStr) {
        int deliveryId;

        try {
            deliveryId = Integer.parseInt(deliveryIdStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("delivery id must be a number");
            return;
        }

        System.out.println(integrationController.deliveryIntegrationStatusToString(deliveryId));
    }

    private Employee.DriverLicenseType parseLicenseType(String licenseTypeStr) {
        if (licenseTypeStr == null || licenseTypeStr.isBlank()) {
            return null;
        }

        try {
            return Employee.DriverLicenseType.valueOf(licenseTypeStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
