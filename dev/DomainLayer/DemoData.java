package DomainLayer;

public class DemoData {

    public static void load() {
        EmployeeController controller = EmployeeController.getInstance();
        ShiftController shiftController = ShiftController.getInstance();

        // branches
        controller.addBranch(1);

        // roles
        Role hrManager = new Role(Role.HR_MANAGER_ID, "HR Manager", "Responsible for HR management");
        Role cashier = new Role(Role.CASHIER_ID, "Cashier", "Responsible for checkout and customers");
        Role storekeeper = new Role(Role.STOREKEEPER_ID, "Storekeeper", "Responsible for warehouse and inventory");
        Role shiftManager = new Role(Role.SHIFT_MANAGER_ID, "Shift Manager", "Responsible for managing the shift");
        Role driver = new Role(Role.DRIVER_ID, "Driver", "Responsible for transporting deliveries");
        controller.addRole(hrManager);
        controller.addRole(cashier);
        controller.addRole(storekeeper);
        controller.addRole(shiftManager);
        controller.addRole(driver);

        // HR manager
        Employee hr = new Employee(
                1,
                "hr1",
                new Employee.BankAccount(10, 100, 111111),
                80,
                "1234",
                Employee.EmploymentType.FULL_TIME,
                "123456789"
        );
        hr.addRole(hrManager);
        hr.addAvailability(0);
        hr.addAvailability(1);
        hr.addAvailability(2);
        hr.addAvailability(3);
        controller.addEmployee(hr);

        // Cashier
        Employee cashierEmp = new Employee(
                1,
                "cashier1",
                new Employee.BankAccount(10, 100, 222222),
                40,
                "1234",
                Employee.EmploymentType.PART_TIME,
                "234567891"
        );
        cashierEmp.addRole(cashier);
        cashierEmp.addAvailability(0);
        cashierEmp.addAvailability(2);
        cashierEmp.addAvailability(4);
        controller.addEmployee(cashierEmp);

        // Storekeeper 1
        Employee storekeeper1 = new Employee(
                1,
                "store1",
                new Employee.BankAccount(10, 100, 333333),
                45,
                "1234",
                Employee.EmploymentType.PART_TIME,
                "345678912"
        );
        storekeeper1.addRole(storekeeper);
        storekeeper1.addAvailability(1);
        storekeeper1.addAvailability(3);
        storekeeper1.addAvailability(5);
        controller.addEmployee(storekeeper1);

        // Employee with 3 roles: cashier + storekeeper + shift manager
        Employee multiRoleEmployee = new Employee(
                1,
                "multi1",
                new Employee.BankAccount(10, 101, 555555),
                60,
                "1234",
                Employee.EmploymentType.FULL_TIME,
                "567891234"
        );
        multiRoleEmployee.addRole(cashier);
        multiRoleEmployee.addRole(storekeeper);
        multiRoleEmployee.addRole(shiftManager);
        multiRoleEmployee.addAvailability(0);
        multiRoleEmployee.addAvailability(1);
        multiRoleEmployee.addAvailability(6);
        multiRoleEmployee.addAvailability(7);
        multiRoleEmployee.addAvailability(12);
        controller.addEmployee(multiRoleEmployee);

        // Driver
        Employee driverEmployee = new Employee(
                1,
                "driver1",
                new Employee.BankAccount(10, 102, 666666),
                55,
                "1234",
                Employee.EmploymentType.FULL_TIME,
                "678912345"
        );
        driverEmployee.addRole(driver);
        driverEmployee.setDriverLicenseType(Employee.DriverLicenseType.C);
        driverEmployee.addAvailability(0);
        driverEmployee.addAvailability(1);
        driverEmployee.addAvailability(2);
        driverEmployee.addAvailability(3);
        driverEmployee.addAvailability(4);
        controller.addEmployee(driverEmployee);

        // default requirements for all shifts in the current and next week
        shiftController.setDefaultRequirementForAllShifts(1, cashier, 1);
        shiftController.setDefaultRequirementForAllShifts(1, storekeeper, 1);
        shiftController.setDefaultRequirementForAllShifts(1, shiftManager, 1);
    }
}
