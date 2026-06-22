package DomainLayer;

import TransportationIntegrationMock.MockDriverLicenseType;
import TransportationIntegrationMock.MockTransportController;
import TransportationIntegrationMock.MockTransportDelivery;

import java.time.LocalDate;

public class TransportationIntegrationController {
    private static TransportationIntegrationController instance;

    private final EmployeeController employeeController;
    private final ShiftController shiftController;
    private final MockTransportController transportController;

    private TransportationIntegrationController() {
        this.employeeController = EmployeeController.getInstance();
        this.shiftController = ShiftController.getInstance();
        this.transportController = MockTransportController.getInstance();
    }

    public static TransportationIntegrationController getInstance() {
        if (instance == null) {
            instance = new TransportationIntegrationController();
        }
        return instance;
    }

    public boolean registerEmployeeAsDriver(String userName, Employee.DriverLicenseType licenseType) {
        Employee employee = employeeController.getEmployee(userName);
        if (employee == null || licenseType == null) {
            return false;
        }

        ensureDriverRoleExists();
        if (!employeeController.employeeHasRole(userName, Role.DRIVER_ID)
                && !employeeController.addRoleToEmployee(userName, Role.DRIVER_ID)) {
            return false;
        }
        if (!employeeController.setEmployeeDriverLicenseType(userName, licenseType)) {
            return false;
        }

        transportController.registerOrUpdateDriver(
                employee.getId(),
                employee.getUserName(),
                employee.getBranchID(),
                toMockLicenseType(licenseType)
        );
        return true;
    }

    public boolean addTransportDelivery(int deliveryId, int branchId, LocalDate date,
                                        Shift.ShiftType shiftType, Employee.DriverLicenseType requiredLicenseType,
                                        String destination) {
        if (!employeeController.branchExists(branchId) || date == null || shiftType == null
                || requiredLicenseType == null || destination == null || destination.isBlank()) {
            return false;
        }

        ShiftController.ShiftWeek week = shiftController.findShiftWeek(branchId, date, shiftType);
        if (week == null) {
            return false;
        }

        if (!transportController.createDelivery(
                deliveryId,
                branchId,
                date,
                shiftType,
                toMockLicenseType(requiredLicenseType),
                destination
        )) {
            return false;
        }

        ensureDriverRoleExists();
        if (!shiftController.increaseShiftRequirement(branchId, date, shiftType, Role.DRIVER_ID, 1, week)) {
            transportController.removeDelivery(deliveryId);
            return false;
        }

        int assignedStorekeepers = shiftController.getAssignedEmployeeCountForRole(
                branchId, date, shiftType, Role.STOREKEEPER_ID, week
        );
        int requiredStorekeepers = shiftController.getRequiredEmployeeCountForRole(
                branchId, date, shiftType, Role.STOREKEEPER_ID, week
        );
        if (assignedStorekeepers == 0 && requiredStorekeepers == 0) {
            if (!shiftController.increaseShiftRequirement(branchId, date, shiftType, Role.STOREKEEPER_ID, 1, week)) {
                return false;
            }
        }

        return true;
    }

    public boolean syncNextWeekDeliveriesWithShifts() {
        boolean changed = false;

        for (Branch branch : employeeController.getBranches()) {
            int branchId = branch.getId();
            LocalDate nextWeekStartDate = shiftController.getNextWeekStartDate(branchId);
            if (nextWeekStartDate == null) {
                continue;
            }

            for (MockTransportDelivery delivery : transportController.getNextDeliveries(branchId, nextWeekStartDate)) {
                if (syncSingleDeliveryRequirements(delivery)) {
                    changed = true;
                }
            }
        }

        return changed;
    }

    public boolean assignDriverToDelivery(String userName, int deliveryId) {
        Employee employee = employeeController.getEmployee(userName);
        MockTransportDelivery delivery = transportController.getDelivery(deliveryId);
        if (employee == null || delivery == null) {
            return false;
        }

        ShiftController.ShiftWeek week = shiftController.findShiftWeek(
                delivery.getBranchId(), delivery.getDate(), delivery.getShiftType()
        );
        if (week == null) {
            return false;
        }

        Employee.DriverLicenseType requiredLicenseType = fromMockLicenseType(delivery.getRequiredLicenseType());
        if (!employeeController.employeeHasRole(userName, Role.DRIVER_ID)) {
            return false;
        }
        if (!employeeController.employeeCanDrive(userName, requiredLicenseType)) {
            return false;
        }

        transportController.registerOrUpdateDriver(
                employee.getId(),
                employee.getUserName(),
                employee.getBranchID(),
                toMockLicenseType(employee.getDriverLicenseType())
        );

        if (!shiftController.isEmployeeAssignedToShift(
                userName,
                delivery.getBranchId(),
                delivery.getDate(),
                delivery.getShiftType(),
                Role.DRIVER_ID,
                week
        )) {
            if (week != ShiftController.ShiftWeek.NEXT) {
                return false;
            }
            if (!shiftController.assignEmployee(
                    userName,
                    delivery.getBranchId(),
                    delivery.getDate(),
                    delivery.getShiftType(),
                    Role.DRIVER_ID
            )) {
                return false;
            }
        }

        if (!transportController.assignDriverToDelivery(deliveryId, employee.getId())) {
            return false;
        }

        return hasStorekeeperForDelivery(deliveryId);
    }

    public boolean hasStorekeeperForDelivery(int deliveryId) {
        MockTransportDelivery delivery = transportController.getDelivery(deliveryId);
        if (delivery == null) {
            return false;
        }

        ShiftController.ShiftWeek week = shiftController.findShiftWeek(
                delivery.getBranchId(), delivery.getDate(), delivery.getShiftType()
        );
        if (week == null) {
            return false;
        }

        return shiftController.getAssignedEmployeeCountForRole(
                delivery.getBranchId(),
                delivery.getDate(),
                delivery.getShiftType(),
                Role.STOREKEEPER_ID,
                week
        ) > 0;
    }

    public boolean isDeliveryFullyIntegrated(int deliveryId) {
        MockTransportDelivery delivery = transportController.getDelivery(deliveryId);
        if (delivery == null || delivery.needsDriver()) {
            return false;
        }

        Employee employee = getEmployeeById(delivery.getAssignedDriverId());
        if (employee == null) {
            return false;
        }

        ShiftController.ShiftWeek week = shiftController.findShiftWeek(
                delivery.getBranchId(), delivery.getDate(), delivery.getShiftType()
        );
        if (week == null) {
            return false;
        }

        boolean driverScheduled = shiftController.isEmployeeAssignedToShift(
                employee.getUserName(),
                delivery.getBranchId(),
                delivery.getDate(),
                delivery.getShiftType(),
                Role.DRIVER_ID,
                week
        );

        return driverScheduled && hasStorekeeperForDelivery(deliveryId);
    }

    public String deliveryIntegrationStatusToString(int deliveryId) {
        MockTransportDelivery delivery = transportController.getDelivery(deliveryId);
        if (delivery == null) {
            return "delivery not found";
        }

        Employee assignedDriver = getEmployeeById(delivery.getAssignedDriverId());
        boolean hasStorekeeper = hasStorekeeperForDelivery(deliveryId);
        boolean fullyIntegrated = isDeliveryFullyIntegrated(deliveryId);

        StringBuilder builder = new StringBuilder();
        builder.append("===== Delivery Integration Status =====\n");
        builder.append("Delivery ID: ").append(delivery.getDeliveryId()).append("\n");
        builder.append("Branch: ").append(delivery.getBranchId()).append("\n");
        builder.append("Date: ").append(delivery.getDate()).append("\n");
        builder.append("Shift: ").append(delivery.getShiftType()).append("\n");
        builder.append("Required License: ").append(delivery.getRequiredLicenseType()).append("\n");
        builder.append("Assigned Driver: ");
        if (assignedDriver == null) {
            builder.append("None\n");
        } else {
            builder.append(assignedDriver.getUserName())
                    .append(" (").append(assignedDriver.getId()).append(")\n");
        }
        builder.append("Storekeeper Assigned: ").append(hasStorekeeper ? "Yes" : "No").append("\n");
        builder.append("Fully Integrated: ").append(fullyIntegrated ? "Yes" : "No").append("\n");
        builder.append("=======================================");
        return builder.toString();
    }

    private boolean syncSingleDeliveryRequirements(MockTransportDelivery delivery) {
        ShiftController.ShiftWeek week = shiftController.findShiftWeek(
                delivery.getBranchId(), delivery.getDate(), delivery.getShiftType()
        );
        if (week != ShiftController.ShiftWeek.NEXT) {
            return false;
        }

        boolean changed = false;

        int assignedDrivers = shiftController.getAssignedEmployeeCountForRole(
                delivery.getBranchId(), delivery.getDate(), delivery.getShiftType(), Role.DRIVER_ID, week
        );
        int requiredDrivers = shiftController.getRequiredEmployeeCountForRole(
                delivery.getBranchId(), delivery.getDate(), delivery.getShiftType(), Role.DRIVER_ID, week
        );
        int totalDriverCoverage = assignedDrivers + requiredDrivers;

        int neededDrivers = transportController.getDeliveriesForShift(
                delivery.getBranchId(), delivery.getDate(), delivery.getShiftType()
        ).size();

        if (totalDriverCoverage < neededDrivers) {
            changed = shiftController.increaseShiftRequirement(
                    delivery.getBranchId(),
                    delivery.getDate(),
                    delivery.getShiftType(),
                    Role.DRIVER_ID,
                    neededDrivers - totalDriverCoverage,
                    week
            ) || changed;
        }

        int assignedStorekeepers = shiftController.getAssignedEmployeeCountForRole(
                delivery.getBranchId(), delivery.getDate(), delivery.getShiftType(), Role.STOREKEEPER_ID, week
        );
        int requiredStorekeepers = shiftController.getRequiredEmployeeCountForRole(
                delivery.getBranchId(), delivery.getDate(), delivery.getShiftType(), Role.STOREKEEPER_ID, week
        );
        int totalStorekeeperCoverage = assignedStorekeepers + requiredStorekeepers;

        if (totalStorekeeperCoverage < 1) {
            changed = shiftController.increaseShiftRequirement(
                    delivery.getBranchId(),
                    delivery.getDate(),
                    delivery.getShiftType(),
                    Role.STOREKEEPER_ID,
                    1,
                    week
            ) || changed;
        }

        return changed;
    }

    private void ensureDriverRoleExists() {
        if (employeeController.getRole(Role.DRIVER_ID) == null) {
            employeeController.addRole(new Role(
                    Role.DRIVER_ID,
                    "Driver",
                    "Responsible for transporting deliveries"
            ));
        }
    }

    private MockDriverLicenseType toMockLicenseType(Employee.DriverLicenseType licenseType) {
        return MockDriverLicenseType.valueOf(licenseType.name());
    }

    private Employee.DriverLicenseType fromMockLicenseType(MockDriverLicenseType licenseType) {
        return Employee.DriverLicenseType.valueOf(licenseType.name());
    }

    private Employee getEmployeeById(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            return null;
        }

        for (Employee employee : employeeController.getEmployees()) {
            if (employeeId.equals(employee.getId())) {
                return employee;
            }
        }

        return null;
    }
}
