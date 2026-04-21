package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DomainLayer.Employee;
import DomainLayer.Role;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Employee domain class.
 * Covers construction guards, role management, vacation-day logic,
 * availability management, and password verification.
 */
public class EmployeeTest {

    private Employee employee;
    private Role cashier;
    private Role shiftManager;

    @BeforeEach
    void setUp() {
        cashier = new Role(Role.CASHIER_ID, "Cashier", "Checkout and customers");
        shiftManager = new Role(Role.SHIFT_MANAGER_ID, "Shift Manager", "Manages the shift");

        employee = new Employee(
                1,
                "alice",
                new Employee.BankAccount(10, 100, 123456),
                50,
                "pass123",
                Employee.EmploymentType.FULL_TIME,
                "111222333");
    }

    // ── Test 1 ──────────────────────────────────────────────────────────────
    /** A valid Employee is constructed with the correct initial state. */
    @Test
    void constructor_validInput_createsEmployee() {
        assertEquals("alice", employee.getUserName());
        assertEquals("111222333", employee.getId());
        assertEquals(1, employee.getBranchID());
        assertEquals(50, employee.getHourlySalary());
        assertEquals(Employee.EmploymentType.FULL_TIME, employee.getEmploymentType());
        assertEquals(20, employee.getVacationDays()); // default
        assertFalse(employee.getIsLoggedIn());
    }

    // ── Test 2 ──────────────────────────────────────────────────────────────
    /** A null or blank username must throw IllegalArgumentException. */
    @Test
    void constructor_nullUserName_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Employee(1, null, new Employee.BankAccount(10, 100, 1),
                50, "pass", Employee.EmploymentType.FULL_TIME, "999"));

        assertThrows(IllegalArgumentException.class, () -> new Employee(1, "  ", new Employee.BankAccount(10, 100, 1),
                50, "pass", Employee.EmploymentType.FULL_TIME, "999"));
    }

    // ── Test 3 ──────────────────────────────────────────────────────────────
    /** addRole() should append a new role to the employee's role list. */
    @Test
    void addRole_newRole_isAdded() {
        employee.addRole(cashier);
        assertTrue(employee.getRoles().contains(cashier));
        assertEquals(1, employee.getRoles().size());
    }

    // ── Test 4 ──────────────────────────────────────────────────────────────
    /** Adding the same role twice must not duplicate it. */
    @Test
    void addRole_duplicate_notAddedTwice() {
        employee.addRole(cashier);
        employee.addRole(cashier);
        assertEquals(1, employee.getRoles().size());
    }

    // ── Test 5 ──────────────────────────────────────────────────────────────
    /**
     * useVacationDays() deducts days and returns true when there are enough days.
     */
    @Test
    void useVacationDays_sufficientDays_returnsTrue() {
        boolean result = employee.useVacationDays(5);
        assertTrue(result);
        assertEquals(15, employee.getVacationDays());
    }

    // ── Test 6 ──────────────────────────────────────────────────────────────
    /**
     * useVacationDays() returns false (and does not deduct) when days are
     * insufficient.
     */
    @Test
    void useVacationDays_insufficientDays_returnsFalse() {
        boolean result = employee.useVacationDays(25); // only 20 available
        assertFalse(result);
        assertEquals(20, employee.getVacationDays()); // unchanged
    }

    // ── Test 7 ──────────────────────────────────────────────────────────────
    /**
     * useVacationDays() with 0 or negative days must throw
     * IllegalArgumentException.
     */
    @Test
    void useVacationDays_zeroDays_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> employee.useVacationDays(0));
        assertThrows(IllegalArgumentException.class, () -> employee.useVacationDays(-3));
    }

    // ── Test 8 ──────────────────────────────────────────────────────────────
    /** addVacationDays() correctly increments the vacation-day counter. */
    @Test
    void addVacationDays_positiveDays_increments() {
        employee.addVacationDays(10);
        assertEquals(30, employee.getVacationDays());
    }

    // ── Test 9 ──────────────────────────────────────────────────────────────
    /** checkPassword() returns true for the correct password (case-insensitive). */
    @Test
    void checkPassword_correctPassword_returnsTrue() {
        assertTrue(employee.checkPassword("pass123"));
        assertTrue(employee.checkPassword("PASS123")); // case-insensitive
        assertFalse(employee.checkPassword("wrongpass"));
        assertFalse(employee.checkPassword(null));
    }

    // ── Test 10 ─────────────────────────────────────────────────────────────
    /** replaceAvailability() swaps an existing shift slot for a new one. */
    @Test
    void replaceAvailability_validSwap_works() {
        employee.addAvailability(2);
        employee.replaceAvailability(2, 6);

        assertFalse(employee.hasAvailability(2));
        assertTrue(employee.hasAvailability(6));
    }
}
