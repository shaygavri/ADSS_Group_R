package tests.tests;

import DomainLayer.Employee;
import DomainLayer.Role;
import DomainLayer.ShiftOrganizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ShiftOrganizer.
 * A fresh ShiftOrganizer (branch 1) is created before each test so tests are
 * independent.
 */
public class ShiftOrganizerTest {

    private ShiftOrganizer organizer;
    private Role cashier;
    private Role shiftManagerRole;
    private Employee cashierEmployee;

    /** Shift index 0 = Sunday morning (first slot of next week). */
    private static final int SHIFT_IDX = 0;

    @BeforeEach
    void setUp() {
        organizer = new ShiftOrganizer(1);
        cashier = new Role(Role.CASHIER_ID, "Cashier", "Checkout");
        shiftManagerRole = new Role(Role.SHIFT_MANAGER_ID, "Shift Manager", "Manages shift");

        cashierEmployee = new Employee(
                1, "emp_cashier",
                new Employee.BankAccount(10, 100, 555),
                40, "pass",
                Employee.EmploymentType.PART_TIME,
                "777888999");
        cashierEmployee.addRole(cashier);
        cashierEmployee.addAvailability(SHIFT_IDX); // available for Sunday morning
    }

    // ── Test 24 ─────────────────────────────────────────────────────────────
    /**
     * changeShiftRequirement() stores the role-count pair when inputs are valid.
     */
    @Test
    void changeShiftRequirement_validInput_returnsTrue() {
        boolean result = organizer.changeShiftRequirement(SHIFT_IDX, cashier, 2, true);
        assertTrue(result);
        assertEquals(2, organizer.getNextWeekShift(SHIFT_IDX).getRequiredCountForRole(cashier));
    }

    // ── Test 25 ─────────────────────────────────────────────────────────────
    /** An employee who is available and has the correct role can be assigned. */
    @Test
    void assignEmployee_available_returnsTrue() {
        organizer.changeShiftRequirement(SHIFT_IDX, cashier, 1, true);
        organizer.updateAvailableEmployees(List.of(cashierEmployee));

        boolean assigned = organizer.assignEmployee(SHIFT_IDX, cashierEmployee, cashier);
        assertTrue(assigned);
        assertTrue(organizer.getNextWeekShift(SHIFT_IDX).isAssigned(cashierEmployee));
    }

    // ── Test 26 ─────────────────────────────────────────────────────────────
    /** An employee without availability for the shift cannot be assigned. */
    @Test
    void assignEmployee_notAvailable_returnsFalse() {
        organizer.changeShiftRequirement(1, cashier, 1, true); // shift index 1 (Sunday evening)
        organizer.updateAvailableEmployees(List.of(cashierEmployee));

        // cashierEmployee has availability only for index 0, not index 1
        boolean assigned = organizer.assignEmployee(1, cashierEmployee, cashier);
        assertFalse(assigned);
    }

    // ── Test 27 ─────────────────────────────────────────────────────────────
    /** Removing an assigned employee adds the requirement back. */
    @Test
    void removeEmployeeFromShift_assigned_restoresRequirement() {
        organizer.changeShiftRequirement(SHIFT_IDX, cashier, 1, true);
        organizer.updateAvailableEmployees(List.of(cashierEmployee));
        organizer.assignEmployee(SHIFT_IDX, cashierEmployee, cashier);

        // requirement was fulfilled (count = 0), now restore
        boolean removed = organizer.removeEmployeeFromShift(SHIFT_IDX, cashierEmployee);
        assertTrue(removed);
        // requirement count should be back to 1
        assertEquals(1, organizer.getNextWeekShift(SHIFT_IDX).getRequiredCountForRole(cashier));
    }

    // ── Test 28 ─────────────────────────────────────────────────────────────
    /**
     * publishNextWeekRequirements() sets the deadline and allows employee updates.
     */
    @Test
    void publishNextWeekRequirements_valid_setsDeadline() {
        LocalDateTime deadline = LocalDateTime.now().plusDays(2);
        boolean published = organizer.publishNextWeekRequirements(deadline);
        assertTrue(published);
        assertTrue(organizer.hasPublishedNextWeekRequirements());
        assertEquals(deadline, organizer.getAvailabilityDeadline());
    }

    // ── Test 29 ─────────────────────────────────────────────────────────────
    /** publishNextWeek() fails when there are unsatisfied requirements. */
    @Test
    void publishNextWeek_unsatisfiedRequirements_returnsFalse() {
        // publish requirements first (required before publishNextWeek)
        organizer.publishNextWeekRequirements(LocalDateTime.now().plusDays(1));
        // add an unfulfilled requirement
        organizer.changeShiftRequirement(SHIFT_IDX, cashier, 1, true);

        boolean published = organizer.publishNextWeek();
        assertFalse(published); // must fail because cashier slot is not filled
    }

    // ── Test 30 ─────────────────────────────────────────────────────────────
    /**
     * markHolidayDay() marks both the morning and evening shifts of a day as
     * closed.
     */
    @Test
    void markHolidayDay_marksBothShifts() {
        // shiftIndex 0 = Sunday morning, 1 = Sunday evening
        organizer.markHolidayDay(0, true);

        assertTrue(organizer.getNextWeekShift(0).isClosedDay(), "Morning shift should be closed");
        assertTrue(organizer.getNextWeekShift(1).isClosedDay(), "Evening shift should be closed");
    }
}
