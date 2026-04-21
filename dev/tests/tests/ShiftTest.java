package tests.tests;

import DomainLayer.Employee;
import DomainLayer.Role;
import DomainLayer.Shift;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Shift domain class.
 * Covers requirement management, closed-day behaviour, and shift-manager
 * detection.
 */
public class ShiftTest {

    private Shift shift;
    private Role cashier;
    private Role shiftManager;

    @BeforeEach
    void setUp() {
        cashier = new Role(Role.CASHIER_ID, "Cashier", "Checkout");
        shiftManager = new Role(Role.SHIFT_MANAGER_ID, "Shift Manager", "Manages shift");

        shift = new Shift(0, 1, LocalDate.now(), Shift.ShiftType.MORNING);
    }

    // ── Test 14 ─────────────────────────────────────────────────────────────
    /** addRequirement() stores the role->count entry when the shift is open. */
    @Test
    void addRequirement_notClosed_isAdded() {
        shift.addRequirement(cashier, 2);
        assertEquals(2, shift.getRequiredCountForRole(cashier));
    }

    // ── Test 15 ─────────────────────────────────────────────────────────────
    /** addRequirement() is silently ignored when the shift is a closed day. */
    @Test
    void addRequirement_closedDay_isIgnored() {
        shift.setClosedDay(true);
        shift.addRequirement(cashier, 2);
        assertEquals(0, shift.getRequiredCountForRole(cashier));
    }

    // ── Test 16 ─────────────────────────────────────────────────────────────
    /** fulfillRequirement() decrements the count by one (from 2 → 1). */
    @Test
    void fulfillRequirement_decrementsCount() {
        shift.addRequirement(cashier, 2);
        boolean fulfilled = shift.fulfillRequirement(cashier);
        assertTrue(fulfilled);
        assertEquals(1, shift.getRequiredCountForRole(cashier));
    }

    // ── Test 17 ─────────────────────────────────────────────────────────────
    /** When the last count is fulfilled the role entry is removed entirely. */
    @Test
    void fulfillRequirement_lastOne_removesEntry() {
        shift.addRequirement(cashier, 1);
        shift.fulfillRequirement(cashier);
        assertFalse(shift.requiresRole(cashier));
        assertEquals(0, shift.getRequiredCountForRole(cashier));
    }

    // ── Test 18 ─────────────────────────────────────────────────────────────
    /** setClosedDay(true) clears all existing requirements. */
    @Test
    void setClosedDay_true_clearsRequirements() {
        shift.addRequirement(cashier, 3);
        shift.addRequirement(shiftManager, 1);
        shift.setClosedDay(true);
        assertTrue(shift.getRequirements().isEmpty());
        assertTrue(shift.isClosedDay());
    }

    // ── Test 19 ─────────────────────────────────────────────────────────────
    /**
     * hasShiftManager() returns true only when a shift-manager role is assigned.
     */
    @Test
    void hasShiftManager_withShiftManager_returnsTrue() {
        assertFalse(shift.hasShiftManager());

        Employee manager = new Employee(
                1, "mgr1", new Employee.BankAccount(10, 100, 999),
                70, "pass", Employee.EmploymentType.FULL_TIME, "888777666");
        manager.addRole(shiftManager);
        shift.assignEmployee(manager, shiftManager);

        assertTrue(shift.hasShiftManager());
    }
}
