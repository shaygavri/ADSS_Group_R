package DataAccessLayer;

import DomainLayer.*;
import TransportationIntegrationMock.MockDriverLicenseType;
import TransportationIntegrationMock.MockTransportController;

import java.time.LocalDate;

public class DatabaseConfig {

    public static void seed() {
        EmployeeController controller = EmployeeController.getInstance();

        if (controller.getBranches().isEmpty()) {
            seedPersistentData(controller);
        }

        // MockTransportController is in-memory only and never persisted,
        // so deliveries must be re-seeded on every startup.
        seedMockDeliveries();
    }

    private static void seedPersistentData(EmployeeController controller) {
        ShiftController shiftController = ShiftController.getInstance();

        // branches
        controller.addBranch(1);
        controller.addBranch(2);

        // roles
        Role hrManager    = new Role(Role.HR_MANAGER_ID,    "HR Manager",      "Responsible for HR management");
        Role cashier      = new Role(Role.CASHIER_ID,        "Cashier",         "Responsible for checkout and customers");
        Role storekeeper  = new Role(Role.STOREKEEPER_ID,    "Storekeeper",     "Responsible for warehouse and inventory");
        Role shiftManager = new Role(Role.SHIFT_MANAGER_ID,  "Shift Manager",   "Responsible for managing the shift");
        Role driver       = new Role(Role.DRIVER_ID,         "Driver",          "Responsible for transporting deliveries");
        controller.addRole(hrManager);
        controller.addRole(cashier);
        controller.addRole(storekeeper);
        controller.addRole(shiftManager);
        controller.addRole(driver);

        // HR manager - branch 1
        Employee hr = new Employee(
                1, "hr1",
                new Employee.BankAccount(10, 100, 111111),
                80, "1234",
                Employee.EmploymentType.FULL_TIME,
                "123456789");
        hr.addRole(hrManager);
        hr.addAvailability(0);
        hr.addAvailability(1);
        hr.addAvailability(2);
        hr.addAvailability(3);
        controller.addEmployee(hr);

        // Cashier - branch 1
        Employee cashierEmp = new Employee(
                1, "cashier1",
                new Employee.BankAccount(10, 100, 222222),
                40, "1234",
                Employee.EmploymentType.PART_TIME,
                "234567891");
        cashierEmp.addRole(cashier);
        cashierEmp.addAvailability(0);
        cashierEmp.addAvailability(2);
        cashierEmp.addAvailability(4);
        controller.addEmployee(cashierEmp);

        // Storekeeper - branch 1
        Employee storekeeper1 = new Employee(
                1, "store1",
                new Employee.BankAccount(10, 100, 333333),
                45, "1234",
                Employee.EmploymentType.PART_TIME,
                "345678912");
        storekeeper1.addRole(storekeeper);
        storekeeper1.addAvailability(1);
        storekeeper1.addAvailability(3);
        storekeeper1.addAvailability(5);
        controller.addEmployee(storekeeper1);

        // Multi-role employee (cashier + storekeeper + shift manager) - branch 1
        Employee multiRoleEmployee = new Employee(
                1, "multi1",
                new Employee.BankAccount(10, 101, 555555),
                60, "1234",
                Employee.EmploymentType.FULL_TIME,
                "567891234");
        multiRoleEmployee.addRole(cashier);
        multiRoleEmployee.addRole(storekeeper);
        multiRoleEmployee.addRole(shiftManager);
        multiRoleEmployee.addAvailability(0);
        multiRoleEmployee.addAvailability(1);
        multiRoleEmployee.addAvailability(6);
        multiRoleEmployee.addAvailability(7);
        multiRoleEmployee.addAvailability(12);
        controller.addEmployee(multiRoleEmployee);

        // Driver - branch 1, license C
        Employee driver1 = new Employee(
                1, "driver1",
                new Employee.BankAccount(10, 102, 666666),
                55, "1234",
                Employee.EmploymentType.FULL_TIME,
                "678912345");
        driver1.addRole(driver);
        driver1.setDriverLicenseType(Employee.DriverLicenseType.C);
        driver1.addAvailability(0);
        driver1.addAvailability(1);
        driver1.addAvailability(2);
        driver1.addAvailability(3);
        driver1.addAvailability(4);
        controller.addEmployee(driver1);

        // Driver - branch 2, license B
        Employee driver2 = new Employee(
                2, "driver2",
                new Employee.BankAccount(10, 103, 777777),
                50, "1234",
                Employee.EmploymentType.FULL_TIME,
                "789123456");
        driver2.addRole(driver);
        driver2.setDriverLicenseType(Employee.DriverLicenseType.B);
        driver2.addAvailability(0);
        driver2.addAvailability(1);
        driver2.addAvailability(2);
        controller.addEmployee(driver2);

        // default shift requirements for branch 1
        shiftController.setDefaultRequirementForAllShifts(1, cashier, 1);
        shiftController.setDefaultRequirementForAllShifts(1, storekeeper, 1);
        shiftController.setDefaultRequirementForAllShifts(1, shiftManager, 1);
    }

    private static void seedMockDeliveries() {
        LocalDate nextMonday = LocalDate.now().plusDays((8 - LocalDate.now().getDayOfWeek().getValue()) % 7 + 1);
        MockTransportController transport = MockTransportController.getInstance();

        transport.createDelivery(
                1, 1, nextMonday,
                Shift.ShiftType.MORNING, MockDriverLicenseType.B,
                "Tel Aviv Central Warehouse");

        transport.createDelivery(
                2, 1, nextMonday.plusDays(2),
                Shift.ShiftType.EVENING, MockDriverLicenseType.C,
                "Haifa Port Distribution Center");

        transport.createDelivery(
                3, 2, nextMonday.plusDays(1),
                Shift.ShiftType.MORNING, MockDriverLicenseType.B,
                "Jerusalem South Depot");
    }
}
