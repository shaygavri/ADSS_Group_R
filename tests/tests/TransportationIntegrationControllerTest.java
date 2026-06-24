
import DomainLayer.Employee;
import DomainLayer.EmployeeController;
import DomainLayer.Role;
import DomainLayer.Shift;
import DomainLayer.ShiftController;
import DomainLayer.TransportationIntegrationController;
import TransportationIntegrationMock.MockTransportController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the employee + transport mock integration flow.
 */
public class TransportationIntegrationControllerTest {

    private EmployeeController employeeController;
    private ShiftController shiftController;
    private TransportationIntegrationController integrationController;
    private MockTransportController mockTransportController;

    private Employee driver;
    private Employee storekeeper;
    private Role driverRole;
    private Role storekeeperRole;

    @BeforeEach
    void setUp() throws Exception {
        resetSingleton(EmployeeController.class, "instance");
        resetSingleton(ShiftController.class, "instance");
        resetSingleton(TransportationIntegrationController.class, "instance");
        resetSingleton(MockTransportController.class, "instance");

        employeeController = EmployeeController.getInstance();
        shiftController = ShiftController.getInstance();
        integrationController = TransportationIntegrationController.getInstance();
        mockTransportController = MockTransportController.getInstance();

        employeeController.addBranch(1);

        driverRole = new Role(Role.DRIVER_ID, "Driver", "Responsible for transporting deliveries");
        storekeeperRole = new Role(Role.STOREKEEPER_ID, "Storekeeper", "Responsible for warehouse and inventory");
        employeeController.addRole(driverRole);
        employeeController.addRole(storekeeperRole);

        driver = new Employee(
                1, "driver1",
                new Employee.BankAccount(10, 100, 111111),
                60, "pass",
                Employee.EmploymentType.FULL_TIME,
                "123456789"
        );
        driver.addAvailability(0);
        driver.addAvailability(1);
        driver.addAvailability(2);
        driver.addAvailability(3);
        employeeController.addEmployee(driver);

        storekeeper = new Employee(
                1, "store1",
                new Employee.BankAccount(10, 100, 222222),
                45, "pass",
                Employee.EmploymentType.PART_TIME,
                "987654321"
        );
        storekeeper.addRole(storekeeperRole);
        storekeeper.addAvailability(0);
        storekeeper.addAvailability(1);
        storekeeper.addAvailability(2);
        employeeController.addEmployee(storekeeper);
    }

    // ── Test 37 ─────────────────────────────────────────────────────────────
    /** Registering an employee as a driver adds role, license, and mock record. */
    @Test
    void registerEmployeeAsDriver_addsRoleLicenseAndMockRegistration() {
        boolean result = integrationController.registerEmployeeAsDriver(
                driver.getUserName(), Employee.DriverLicenseType.C
        );

        assertTrue(result);
        assertTrue(employeeController.employeeHasRole(driver.getUserName(), Role.DRIVER_ID));
        assertEquals(Employee.DriverLicenseType.C, driver.getDriverLicenseType());
        assertTrue(mockTransportController.isRegisteredDriver(driver.getId()));
    }

    // ── Test 38 ─────────────────────────────────────────────────────────────
    /** Changing driver license through integration updates both modules. */
    @Test
    void changeEmployeeDriverLicenseType_updatesEmployeeAndMockDriver() {
        integrationController.registerEmployeeAsDriver(driver.getUserName(), Employee.DriverLicenseType.B);

        boolean changed = integrationController.changeEmployeeDriverLicenseType(
                driver.getUserName(), Employee.DriverLicenseType.CE
        );

        assertTrue(changed);
        assertEquals(Employee.DriverLicenseType.CE, driver.getDriverLicenseType());
        assertEquals("CE", mockTransportController.getDriver(driver.getId()).getLicenseType().name());
    }

    // ── Test 39 ─────────────────────────────────────────────────────────────
    /** Adding a next-week delivery creates Driver and Storekeeper requirements. */
    @Test
    void addTransportDelivery_nextWeek_addsDriverAndStorekeeperRequirements() {
        LocalDate nextSunday = shiftController.getNextWeekStartDate(1);

        boolean added = integrationController.addTransportDelivery(
                1, 1, nextSunday, Shift.ShiftType.MORNING,
                Employee.DriverLicenseType.C1, "Beer Supplier"
        );

        assertTrue(added);
        assertEquals(1, shiftController.getRequiredEmployeeCountForRole(
                1, nextSunday, Shift.ShiftType.MORNING, Role.DRIVER_ID, ShiftController.ShiftWeek.NEXT));
        assertEquals(1, shiftController.getRequiredEmployeeCountForRole(
                1, nextSunday, Shift.ShiftType.MORNING, Role.STOREKEEPER_ID, ShiftController.ShiftWeek.NEXT));
    }

    // ── Test 40 ─────────────────────────────────────────────────────────────
    /** Two deliveries in the same shift require two Driver slots after sync. */
    @Test
    void syncNextWeekDeliveriesWithShifts_twoDeliveriesSameShift_requireTwoDrivers() {
        LocalDate nextSunday = shiftController.getNextWeekStartDate(1);
        mockTransportController.createDelivery(11, 1, nextSunday, Shift.ShiftType.MORNING,
                TransportationIntegrationMock.MockDriverLicenseType.C1, "Milk");
        mockTransportController.createDelivery(12, 1, nextSunday, Shift.ShiftType.MORNING,
                TransportationIntegrationMock.MockDriverLicenseType.C1, "Bread");

        String summary = integrationController.syncNextWeekDeliveriesWithShifts();

        assertFalse(summary.isEmpty());
        assertEquals(2, shiftController.getRequiredEmployeeCountForRole(
                1, nextSunday, Shift.ShiftType.MORNING, Role.DRIVER_ID, ShiftController.ShiftWeek.NEXT));
        assertEquals(1, shiftController.getRequiredEmployeeCountForRole(
                1, nextSunday, Shift.ShiftType.MORNING, Role.STOREKEEPER_ID, ShiftController.ShiftWeek.NEXT));
    }

    // ── Test 41 ─────────────────────────────────────────────────────────────
    /** A qualified scheduled driver with a Storekeeper present can be assigned. */
    @Test
    void assignDriverToDelivery_matchingLicenseAndStorekeeperPresent_returnsTrue() {
        LocalDate nextSunday = shiftController.getNextWeekStartDate(1);
        integrationController.registerEmployeeAsDriver(driver.getUserName(), Employee.DriverLicenseType.C);
        integrationController.addTransportDelivery(21, 1, nextSunday, Shift.ShiftType.MORNING,
                Employee.DriverLicenseType.C1, "Frozen goods");

        shiftController.assignEmployee(storekeeper.getUserName(), 1, nextSunday, Shift.ShiftType.MORNING,
                Role.STOREKEEPER_ID);

        boolean assigned = integrationController.assignDriverToDelivery(driver.getUserName(), 21);

        assertTrue(assigned);
        assertEquals(driver.getId(), mockTransportController.getDelivery(21).getAssignedDriverId());
    }

    // ── Test 42 ─────────────────────────────────────────────────────────────
    /** Driver assignment fails when the driver's license is insufficient. */
    @Test
    void assignDriverToDelivery_withoutEnoughLicense_returnsFalse() {
        LocalDate nextSunday = shiftController.getNextWeekStartDate(1);
        integrationController.registerEmployeeAsDriver(driver.getUserName(), Employee.DriverLicenseType.B);
        integrationController.addTransportDelivery(22, 1, nextSunday, Shift.ShiftType.MORNING,
                Employee.DriverLicenseType.C, "Heavy load");
        shiftController.assignEmployee(storekeeper.getUserName(), 1, nextSunday, Shift.ShiftType.MORNING,
                Role.STOREKEEPER_ID);

        boolean assigned = integrationController.assignDriverToDelivery(driver.getUserName(), 22);

        assertFalse(assigned);
        assertNull(mockTransportController.getDelivery(22).getAssignedDriverId());
    }

    // ── Test 43 ─────────────────────────────────────────────────────────────
    /** A delivery is fully integrated only after driver and storekeeper coverage. */
    @Test
    void isDeliveryFullyIntegrated_afterDriverAssignmentAndStorekeeper_returnsTrue() {
        LocalDate nextSunday = shiftController.getNextWeekStartDate(1);
        integrationController.registerEmployeeAsDriver(driver.getUserName(), Employee.DriverLicenseType.C);
        integrationController.addTransportDelivery(23, 1, nextSunday, Shift.ShiftType.MORNING,
                Employee.DriverLicenseType.B, "Produce");
        shiftController.assignEmployee(storekeeper.getUserName(), 1, nextSunday, Shift.ShiftType.MORNING,
                Role.STOREKEEPER_ID);
        integrationController.assignDriverToDelivery(driver.getUserName(), 23);

        assertTrue(integrationController.isDeliveryFullyIntegrated(23));
    }

    // ── Test 44 ─────────────────────────────────────────────────────────────
    /** A delivery without any Storekeeper assigned must report missing coverage. */
    @Test
    void hasStorekeeperForDelivery_withoutAssignedStorekeeper_returnsFalse() {
        LocalDate nextSunday = shiftController.getNextWeekStartDate(1);
        integrationController.addTransportDelivery(24, 1, nextSunday, Shift.ShiftType.MORNING,
                Employee.DriverLicenseType.B, "Boxes");

        assertFalse(integrationController.hasStorekeeperForDelivery(24));
    }

    // ── Test 45 ─────────────────────────────────────────────────────────────
    /** Requesting status for a missing delivery returns a not-found message. */
    @Test
    void deliveryIntegrationStatus_missingDelivery_returnsNotFoundMessage() {
        String status = integrationController.deliveryIntegrationStatusToString(999);
        assertEquals("delivery not found", status);
    }

    private void resetSingleton(Class<?> clazz, String fieldName) throws Exception {
        Field instanceField = clazz.getDeclaredField(fieldName);
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }
}
