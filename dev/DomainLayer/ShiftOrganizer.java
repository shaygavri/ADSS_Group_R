package DomainLayer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShiftOrganizer {
    private static final int SHIFTS_IN_WEEK = 14;

    private int branchId;
    private Shift[] currentWeekShifts;
    private Shift[] nextWeekShifts;
    private Map<Shift, Map<Role, List<Employee>>> availableEmployeesPerShiftByRole;
    private LocalDate weekStartDate;
    private boolean availabilityChangesAllowed;
    private boolean isPublished;
    private List<Shift[]> shiftsHistory;

    public ShiftOrganizer(int branchId, Shift[] currentWeek, Shift[] nextWeek) {
        if (branchId <= 0) {
            throw new IllegalArgumentException("branchId must be positive");
        }

        this.branchId = branchId;
        this.currentWeekShifts = prepareWeek(currentWeek, getStartOfCurrentWeek());
        this.nextWeekShifts = prepareWeek(nextWeek, this.currentWeekShifts[0].getDate().plusWeeks(1));
        this.weekStartDate = this.currentWeekShifts[0].getDate();
        this.availableEmployeesPerShiftByRole = new HashMap<>();
        this.availabilityChangesAllowed = true;
        this.isPublished = false;
        this.shiftsHistory = new ArrayList<>();
    }

    public ShiftOrganizer(int branchId) {
        this(branchId, null, null);
    }

    public int getBranchId() {
        return branchId;
    }

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public boolean isAvailabilityChangesAllowed() {
        return availabilityChangesAllowed;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public Shift[] getCurrentWeekShifts() {
        return currentWeekShifts.clone();
    }

    public Shift[] getNextWeekShifts() {
        return nextWeekShifts.clone();
    }

    public Shift getCurrentWeekShift(int shiftIndex) {
        validateShiftIndex(shiftIndex);
        return currentWeekShifts[shiftIndex];
    }

    public Shift getNextWeekShift(int shiftIndex) {
        validateShiftIndex(shiftIndex);
        return nextWeekShifts[shiftIndex];
    }

    public boolean publishNextWeek() {
        if (!hasShiftManagerForEveryNextWeekShift()) {
            return false;
        }

        isPublished = true;
        availabilityChangesAllowed = false;
        return true;
    }

    public boolean setNextWeekAsCurrentWeek() {
        if (!isPublished) {
            return false;
        }

        shiftsHistory.add(currentWeekShifts.clone());
        currentWeekShifts = nextWeekShifts;
        weekStartDate = currentWeekShifts[0].getDate();
        nextWeekShifts = createWeekShifts(weekStartDate.plusWeeks(1));

        availableEmployeesPerShiftByRole.clear();
        availabilityChangesAllowed = true;
        isPublished = false;
        return true;
    }

    public boolean changeShiftRequirement(int shiftIndex, Role role, int minimumCount) {
        return changeShiftRequirement(shiftIndex, role, minimumCount, true);
    }

    public boolean changeShiftRequirement(int shiftIndex, Role role, int minimumCount, boolean forNextWeek) {
        validateShiftIndex(shiftIndex);
        if (role == null || minimumCount < 0) {
            return false;
        }

        Shift shift = forNextWeek ? nextWeekShifts[shiftIndex] : currentWeekShifts[shiftIndex];
        if (minimumCount == 0) { // If 0 removes the role, if greater sets the role to be {...role : minimumCount,...} (NOT ADD!)
            shift.removeRequirement(role);
        } else {
            shift.addRequirement(role, minimumCount);
        }
        return true;
    }

    // Should be integrated with an Employee changes his availability.
    public void updateAvailableEmployees(List<Employee> employees) {
        availableEmployeesPerShiftByRole.clear();

        if (employees == null) {
            return;
        }

        for (Employee employee : employees) {
            if (employee == null || employee.getBranchID() != branchId) {
                continue;
            }

            for (Integer shiftIndex : employee.getAvailabilities()) {
                if (shiftIndex == null || shiftIndex < 0 || shiftIndex >= SHIFTS_IN_WEEK) {
                    continue;
                }

                Shift shift = nextWeekShifts[shiftIndex];
                for (Role role : employee.getRoles()) {
                    addAvailableEmployee(shift, role, employee);
                }
            }
        }
    }

    // Only for next week
    public List<Employee> getAvailableEmployeesForShiftByRole(int shiftIndex, Role role) {
        validateShiftIndex(shiftIndex);
        if (role == null) {
            return new ArrayList<>();
        }

        Shift shift = nextWeekShifts[shiftIndex];
        Map<Role, List<Employee>> employeesByRole = availableEmployeesPerShiftByRole.get(shift);
        if (employeesByRole == null) {
            return new ArrayList<>();
        }

        for (Role existingRole : employeesByRole.keySet()) {
            if (existingRole.equals(role)) {
                return new ArrayList<>(employeesByRole.get(existingRole));
            }
        }

        return new ArrayList<>();
    }

    public boolean assignEmployee(int shiftIndex, Employee employee, Role role) {
        validateShiftIndex(shiftIndex);
        if (employee == null || role == null) {
            return false;
        }
        if (employee.getBranchID() != branchId || !employee.hasAvailability(shiftIndex) || !employeeHasRole(employee, role)) {
            return false;
        }

        Shift shift = nextWeekShifts[shiftIndex];
        shift.assignEmployee(employee, role);
        return true;
    }

    // Only for next week
    public boolean removeEmployeeFromShift(int shiftIndex, Employee employee) {
        validateShiftIndex(shiftIndex);
        if (employee == null) {
            return false;
        }
        return nextWeekShifts[shiftIndex].removeEmployee(employee);
    }

    public String showCurrentWeekShifts() {
        return shiftsToString(currentWeekShifts);
    }

    public String showNextWeekShifts() {
        return shiftsToString(nextWeekShifts);
    }

    public String showAllShiftsHistory() {
        if (shiftsHistory.isEmpty()) {
            return "No shifts history yet";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < shiftsHistory.size(); i++) {
            builder.append("===== Week ").append(i + 1).append(" =====\n");
            builder.append(shiftsToString(shiftsHistory.get(i)));
        }
        return builder.toString();
    }

    public String showShiftsHistoryByBranch(int branchId) {
        if (this.branchId != branchId) {
            return "No shifts history for branch " + branchId;
        }
        return showAllShiftsHistory();
    }

    public List<Shift[]> getShiftsHistory() {
        List<Shift[]> copy = new ArrayList<>();
        for (Shift[] week : shiftsHistory) {
            copy.add(week.clone());
        }
        return copy;
    }

    private void addAvailableEmployee(Shift shift, Role role, Employee employee) {
        availableEmployeesPerShiftByRole
                .computeIfAbsent(shift, s -> new HashMap<>())
                .computeIfAbsent(role, r -> new ArrayList<>());

        List<Employee> employees = availableEmployeesPerShiftByRole.get(shift).get(role);
        if (!employees.contains(employee)) {
            employees.add(employee);
        }
    }

    private boolean hasShiftManagerForEveryNextWeekShift() {
        for (Shift shift : nextWeekShifts) {
            if (!shift.hasShiftManager()) {
                return false;
            }
        }
        return true;
    }

    private boolean employeeHasRole(Employee employee, Role role) {
        for (Role employeeRole : employee.getRoles()) {
            if (employeeRole.equals(role)) {
                return true;
            }
        }
        return false;
    }


    private Shift[] prepareWeek(Shift[] shifts, LocalDate defaultStartDate) {
        if (shifts == null) {
            return createWeekShifts(defaultStartDate);
        }
        if (shifts.length != SHIFTS_IN_WEEK) {
            throw new IllegalArgumentException("week must contain 14 shifts");
        }
        return shifts.clone();
    }

    private Shift[] createWeekShifts(LocalDate startDate) {
        Shift[] shifts = new Shift[SHIFTS_IN_WEEK];

        for (int day = 0; day < 7; day++) {
            LocalDate date = startDate.plusDays(day);
            shifts[day * 2] = new Shift(day * 2, branchId, date, Shift.ShiftType.MORNING);
            shifts[day * 2 + 1] = new Shift(day * 2 + 1, branchId, date, Shift.ShiftType.EVENING);
        }

        return shifts;
    }

    private LocalDate getStartOfCurrentWeek() {
        LocalDate today = LocalDate.now();
        while (today.getDayOfWeek() != DayOfWeek.SUNDAY) {
            today = today.minusDays(1);
        }
        return today;
    }

    private void validateShiftIndex(int shiftIndex) {
        if (shiftIndex < 0 || shiftIndex >= SHIFTS_IN_WEEK) {
            throw new IllegalArgumentException("shift must be between 0 and 13");
        }
    }

    private String shiftsToString(Shift[] shifts) {
        StringBuilder builder = new StringBuilder();
        for (Shift shift : shifts) {
            builder.append(shift).append("\n");
        }
        return builder.toString();
    }
}
