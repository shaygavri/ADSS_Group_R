package tests.tests;

import DomainLayer.Employee;
import DomainLayer.EmployeeController;
import DomainLayer.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EmployeeController.
 * Because EmployeeController is a singleton we reset its static instance
 * before every test using reflection so that each test starts clean.
 */
public class EmployeeControllerTest {

    private EmployeeController controller;
    private Role cashier;

    /** Reset the singleton before each test so tests are independent. */
    @BeforeEach
    void setUp() throws Exception {
        Field instanceField = EmployeeController.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        controller = EmployeeController.getInstance();

        controller.addBranch(1);

        cashier = new Role(Role.CASHIER_ID, "Cashier", "Checkout and customers");
        controller.addRole(cashier);
    }

    /** Helper – creates a basic test employee. */
    private Employee makeEmployee(String username, String id) {
        return new Employee(
                1, username,
                new Employee.BankAccount(10, 100, 111),
                40, "pass",
                Employee.EmploymentType.PART_TIME,
                id);
    }

    // ── Test 20 ─────────────────────────────────────────────────────────────
    /** addEmployee() returns true for a new, unique employee. */
    @Test
    void addEmployee_unique_returnsTrue() {
        Employee e = makeEmployee("bob", "111");
        boolean added = controller.addEmployee(e);
        assertTrue(added);
        assertNotNull(controller.getEmployee("bob"));
    }

    // ── Test 21 ─────────────────────────────────────────────────────────────
    /** addEmployee() returns false when the username already exists. */
    @Test
    void addEmployee_duplicate_returnsFalse() {
        controller.addEmployee(makeEmployee("bob", "111"));
        boolean addedAgain = controller.addEmployee(makeEmployee("bob", "222"));
        assertFalse(addedAgain);
        // list still contains exactly one employee named "bob"
        assertEquals(1, controller.getEmployees().size());
    }

    // ── Test 22 ─────────────────────────────────────────────────────────────
    /**
     * addRoleToEmployee() correctly assigns an existing role to an existing
     * employee.
     */
    @Test
    void addRoleToEmployee_validRoleAndEmployee_returnsTrue() {
        controller.addEmployee(makeEmployee("carol", "333"));
        boolean assigned = controller.addRoleToEmployee("carol", Role.CASHIER_ID);
        assertTrue(assigned);
        assertTrue(controller.employeeHasRole("carol", Role.CASHIER_ID));
    }

    // ── Test 23 ─────────────────────────────────────────────────────────────
    /** removeEmployee() moves the employee from active to firedEmployees list. */
    @Test
    void removeEmployee_existing_movesToFired() {
        controller.addEmployee(makeEmployee("dave", "444"));
        boolean removed = controller.removeEmployee("dave");
        assertTrue(removed);
        assertNull(controller.getEmployee("dave"));
        assertEquals(1, controller.getFiredEmployees().size());
        assertEquals("dave", controller.getFiredEmployees().get(0).getUserName());
    }
}
