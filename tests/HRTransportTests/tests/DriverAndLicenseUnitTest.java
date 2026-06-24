package tests;

import DomainLayer.Employee;
import DomainLayer.EmployeeController;
import DomainLayer.Role;
import DomainLayer.Shift;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the new driver-license functionality in the employee and
 * shift domain classes.
 */
public class DriverAndLicenseUnitTest {

    private Employee employee;
    private Role driverRole;

    @BeforeEach
    void setUp() throws Exception {
        Field instanceField = EmployeeController.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        employee = new Employee(
                1,
                "driverCandidate",
                new Employee.BankAccount(10, 100, 123456),
                55,
                "pass",
                Employee.EmploymentType.FULL_TIME,
                "111222333"
        );
        driverRole = new Role(Role.DRIVER_ID, "Driver", "Responsible for transporting deliveries");
    }

    // ── Test 31 ─────────────────────────────────────────────────────────────
    /** A higher driver license type covers all lower license requirements. */
    @Test
    void canDrive_higherLicenseCoversLowerRequirement() {
        employee.setDriverLicenseType(Employee.DriverLicenseType.CE);

        assertTrue(employee.canDrive(Employee.DriverLicenseType.C));
        assertTrue(employee.canDrive(Employee.DriverLicenseType.C1));
        assertTrue(employee.canDrive(Employee.DriverLicenseType.B));
    }

    // ── Test 32 ─────────────────────────────────────────────────────────────
    /** A lower driver license type must not cover higher license requirements. */
    @Test
    void canDrive_lowerLicenseDoesNotCoverHigherRequirement() {
        employee.setDriverLicenseType(Employee.DriverLicenseType.B);

        assertFalse(employee.canDrive(Employee.DriverLicenseType.C1));
        assertFalse(employee.canDrive(Employee.DriverLicenseType.C));
    }

    // ── Test 33 ─────────────────────────────────────────────────────────────
    /** Removing the Driver role clears the employee's driver-license field. */
    @Test
    void removeRole_driverRole_clearsDriverLicenseType() {
        employee.addRole(driverRole);
        employee.setDriverLicenseType(Employee.DriverLicenseType.C);

        employee.removeRole(driverRole);

        assertNull(employee.getDriverLicenseType());
        assertFalse(employee.hasDriverLicense());
    }

    // ── Test 34 ─────────────────────────────────────────────────────────────
    /** Changing driver license without a Driver role must fail. */
    @Test
    void changeEmployeeDriverLicenseType_withoutDriverRole_returnsFalse() {
        EmployeeController controller = EmployeeController.getInstance();
        controller.addBranch(1);
        controller.addRole(driverRole);
        controller.addEmployee(employee);

        boolean changed = controller.changeEmployeeDriverLicenseType(
                employee.getUserName(), Employee.DriverLicenseType.C1
        );

        assertFalse(changed);
        assertNull(controller.getEmployeeDriverLicenseType(employee.getUserName()));
    }

    // ── Test 35 ─────────────────────────────────────────────────────────────
    /** Changing driver license for an existing Driver updates the stored type. */
    @Test
    void changeEmployeeDriverLicenseType_existingDriver_updatesLicense() {
        EmployeeController controller = EmployeeController.getInstance();
        controller.addBranch(1);
        controller.addRole(driverRole);
        controller.addEmployee(employee);
        controller.addRoleToEmployee(employee.getUserName(), Role.DRIVER_ID);
        controller.setEmployeeDriverLicenseType(employee.getUserName(), Employee.DriverLicenseType.B);

        boolean changed = controller.changeEmployeeDriverLicenseType(
                employee.getUserName(), Employee.DriverLicenseType.C
        );

        assertTrue(changed);
        assertEquals(Employee.DriverLicenseType.C,
                controller.getEmployeeDriverLicenseType(employee.getUserName()));
    }

    // ── Test 36 ─────────────────────────────────────────────────────────────
    /** Shift counts by role include only employees assigned to that exact role. */
    @Test
    void shiftAssignmentCountForRole_countsOnlyMatchingRole() {
        Role cashier = new Role(Role.CASHIER_ID, "Cashier", "Checkout");
        Shift shift = new Shift(0, 1, LocalDate.now(), Shift.ShiftType.MORNING);

        Employee driver1 = new Employee(1, "d1", new Employee.BankAccount(10, 1, 1), 50,
                "pass", Employee.EmploymentType.FULL_TIME, "101");
        Employee driver2 = new Employee(1, "d2", new Employee.BankAccount(10, 1, 2), 50,
                "pass", Employee.EmploymentType.FULL_TIME, "102");
        Employee cashierEmployee = new Employee(1, "c1", new Employee.BankAccount(10, 1, 3), 40,
                "pass", Employee.EmploymentType.PART_TIME, "103");

        shift.assignEmployee(driver1, driverRole);
        shift.assignEmployee(driver2, driverRole);
        shift.assignEmployee(cashierEmployee, cashier);

        assertEquals(2, shift.getAssignmentCountForRole(driverRole));
        assertEquals(1, shift.getAssignmentCountForRole(cashier));
    }
}
