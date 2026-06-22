package DomainLayer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShiftController {
    private static ShiftController instance;
    private final EmployeeController employeeController;
    private final Map<Integer, ShiftOrganizer> shiftOrganizers;

    public enum ShiftWeek {
        CURRENT,
        NEXT
    }

    private ShiftController() {
        this.employeeController = EmployeeController.getInstance();
        this.shiftOrganizers = new HashMap<>();
    }

    public static ShiftController getInstance() {
        if (instance == null) {
            instance = new ShiftController();
        }
        return instance;
    }

    public boolean publishNextWeek() {
        ArrayList<Branch> branches = employeeController.getBranches();
        if (branches.isEmpty()) {
            return false;
        }

        for (Branch branch : branches) {
            int branchId = branch.getId();
            ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
            organizer.updateAvailableEmployees(employeeController.getEmployees());

            if (!organizer.publishNextWeek()) {
                return false;
            }
        }

        return true;
    }

    public boolean publishNextWeekRequirements(LocalDateTime deadline) {
        ArrayList<Branch> branches = employeeController.getBranches();
        if (branches.isEmpty()) {
            return false;
        }

        for (Branch branch : branches) {
            int branchId = branch.getId();
            ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
            if (!organizer.publishNextWeekRequirements(deadline)) {
                return false;
            }
        }

        return true;
    }

    public String nextWeekMissingRequirementsToString() {
        ArrayList<Branch> branches = employeeController.getBranches();
        if (branches.isEmpty()) {
            return "No branches in the system";
        }

        StringBuilder builder = new StringBuilder();
        for (Branch branch : branches) {
            int branchId = branch.getId();
            ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
            String branchReport = organizer.nextWeekMissingRequirementsToString();
            if ("All next week shifts are fully assigned".equals(branchReport)) {
                continue;
            }

            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append("Branch ").append(branchId).append(":\n");
            builder.append(branchReport).append("\n");
        }

        if (builder.length() == 0) {
            return "All next week shifts are fully assigned";
        }

        return builder.toString().trim();
    }

    public void syncCurrentWeekIfNeeded() {
        ArrayList<Branch> branches = employeeController.getBranches();
        for (Branch branch : branches) {
            int branchId = branch.getId();
            ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
            while (organizer.shouldAutoAdvanceToNextWeek()) {
                if (!organizer.setNextWeekAsCurrentWeek()) {
                    break;
                }
            }
        }
    }

    public boolean setAsCurrentWeek() {
        ArrayList<Branch> branches = employeeController.getBranches();
        if (branches.isEmpty()) {
            return false;
        }

        for (Branch branch : branches) {
            int branchId = branch.getId();
            ShiftOrganizer organizer = getOrCreateOrganizer(branchId);

            if (!organizer.setNextWeekAsCurrentWeek()) {
                return false;
            }
        }

        return true;
    }

    public boolean isNextWeekLocked(int branchId) {
        if (!employeeController.branchExists(branchId)) {
            return false;
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        return !organizer.isAvailabilityChangesAllowed();
    }

    public boolean hasPublishedNextWeekRequirements(int branchId) {
        if (!employeeController.branchExists(branchId)) {
            return false;
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        return organizer.hasPublishedNextWeekRequirements();
    }

    public boolean canEmployeesUpdateAvailability(int branchId) {
        if (!employeeController.branchExists(branchId)) {
            return false;
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        return organizer.canEmployeesUpdateAvailability();
    }

    public LocalDateTime getAvailabilityDeadline(int branchId) {
        if (!employeeController.branchExists(branchId)) {
            return null;
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        return organizer.getAvailabilityDeadline();
    }

    public String nextWeekStatusToString() {
        ArrayList<Branch> branches = employeeController.getBranches();
        if (branches.isEmpty()) {
            return "No branches in the system";
        }

        StringBuilder builder = new StringBuilder();
        for (Branch branch : branches) {
            int branchId = branch.getId();
            ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
            builder.append("===== Branch ").append(branchId).append(" =====\n");
            builder.append(organizer.nextWeekStatusToString()).append("\n");
        }
        return builder.toString().trim();
    }

    public String nextWeekPublishSummaryToString() {
        ArrayList<Branch> branches = employeeController.getBranches();
        if (branches.isEmpty()) {
            return "No branches in the system";
        }

        StringBuilder builder = new StringBuilder();
        for (Branch branch : branches) {
            int branchId = branch.getId();
            ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
            builder.append("===== Branch ").append(branchId).append(" =====\n");
            builder.append(organizer.nextWeekPublishSummaryToString()).append("\n");
        }
        return builder.toString().trim();
    }

    public boolean changeShiftRequirement(int branchId, LocalDate date, Shift.ShiftType shiftType,
                                          int roleId, int amount) {
        return changeShiftRequirement(branchId, date, shiftType, roleId, amount, ShiftWeek.NEXT);
    }

    public boolean markHolidayDay(int branchId, int shiftIndex, ShiftWeek week) {
        if (!employeeController.branchExists(branchId) || week == null) {
            return false;
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        try {
            return organizer.markHolidayDay(shiftIndex, week == ShiftWeek.NEXT);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean changeShiftRequirement(int branchId, int shiftIndex, int roleId, int amount, ShiftWeek week) {
        if (!employeeController.branchExists(branchId)) {
            return false;
        }

        Role role = employeeController.getRole(roleId);
        if (role == null || amount < 0 || week == null) {
            return false;
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);

        try {
            return organizer.changeShiftRequirement(shiftIndex, role, amount, week == ShiftWeek.NEXT);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean changeShiftRequirement(int branchId, LocalDate date, Shift.ShiftType shiftType,
                                          int roleId, int amount, ShiftWeek week) {
        if (!employeeController.branchExists(branchId)) {
            return false;
        }

        Role role = employeeController.getRole(roleId);
        if (role == null || date == null || shiftType == null || amount < 0 || week == null) {
            return false;
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        int shiftIndex = findShiftIndex(organizer, date, shiftType, week);
        if (shiftIndex == -1) {
            return false;
        }

        return organizer.changeShiftRequirement(shiftIndex, role, amount, week == ShiftWeek.NEXT);
    }

    public boolean setDefaultRequirementForAllShifts(int branchId, Role role, int amount) {
        if (!employeeController.branchExists(branchId) || role == null || amount < 0) {
            return false;
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);

        for (Shift shift : organizer.getCurrentWeekShifts()) {
            setShiftRequirement(shift, role, amount);
        }

        for (Shift shift : organizer.getNextWeekShifts()) {
            setShiftRequirement(shift, role, amount);
        }

        return true;
    }

    public String availableEmployeesForShiftByRoleToString(int branchId, LocalDate date,
                                                           Shift.ShiftType shiftType, int roleId) {
        if (!employeeController.branchExists(branchId)) {
            return "branch not found";
        }

        Role role = employeeController.getRole(roleId);
        if (role == null || date == null || shiftType == null) {
            return "shift or role not found";
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        int shiftIndex = findNextWeekShiftIndex(organizer, date, shiftType);
        if (shiftIndex == -1) {
            return "shift not found in next week";
        }

        organizer.updateAvailableEmployees(employeeController.getEmployees());
        List<Employee> availableEmployees = organizer.getAvailableEmployeesForShiftByRole(shiftIndex, role);

        if (availableEmployees.isEmpty()) {
            return "No available employees found";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("===== Available Employees =====\n");
        for (Employee employee : availableEmployees) {
            builder.append("Username: ")
                    .append(employee.getUserName())
                    .append(", ID: ")
                    .append(employee.getId())
                    .append("\n");
        }
        builder.append("===============================");

        return builder.toString();
    }

    public LocalDate getNextWeekStartDate(int branchId) {
        if (!employeeController.branchExists(branchId)) {
            return null;
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        return organizer.getNextWeekShift(0).getDate();
    }

    public String availableEmployeesForShiftByRoleToString(int branchId, int shiftIndex, int roleId) {
        if (!employeeController.branchExists(branchId)) {
            return "branch not found";
        }

        Role role = employeeController.getRole(roleId);
        if (role == null) {
            return "shift or role not found";
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        List<Employee> availableEmployees;

        try {
            organizer.updateAvailableEmployees(employeeController.getEmployees());
            availableEmployees = organizer.getAvailableEmployeesForShiftByRole(shiftIndex, role);
        } catch (IllegalArgumentException e) {
            return "shift not found in next week";
        }

        if (availableEmployees.isEmpty()) {
            return "No available employees found";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("===== Available Employees =====\n");
        for (Employee employee : availableEmployees) {
            builder.append("Username: ")
                    .append(employee.getUserName())
                    .append(", ID: ")
                    .append(employee.getId())
                    .append("\n");
        }
        builder.append("===============================");

        return builder.toString();
    }

    public boolean assignEmployee(String userName, int branchId, LocalDate date,
                                  Shift.ShiftType shiftType, int roleId) {
        if (!employeeController.branchExists(branchId)) {
            return false;
        }

        Employee employee = employeeController.getEmployee(userName);
        Role role = employeeController.getRole(roleId);
        if (employee == null || role == null || date == null || shiftType == null) {
            return false;
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        int shiftIndex = findNextWeekShiftIndex(organizer, date, shiftType);
        if (shiftIndex == -1) {
            return false;
        }

        organizer.updateAvailableEmployees(employeeController.getEmployees());
        return organizer.assignEmployee(shiftIndex, employee, role);
    }

    public boolean assignEmployee(String userName, int branchId, int shiftIndex, int roleId) {
        if (!employeeController.branchExists(branchId)) {
            return false;
        }

        Employee employee = employeeController.getEmployee(userName);
        Role role = employeeController.getRole(roleId);
        if (employee == null || role == null) {
            return false;
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);

        try {
            organizer.updateAvailableEmployees(employeeController.getEmployees());
            return organizer.assignEmployee(shiftIndex, employee, role);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isEmployeeAssignedToShift(String userName, int branchId, LocalDate date,
                                             Shift.ShiftType shiftType, int roleId, ShiftWeek week) {
        if (!employeeController.branchExists(branchId)) {
            return false;
        }

        Employee employee = employeeController.getEmployee(userName);
        Role role = employeeController.getRole(roleId);
        if (employee == null || role == null || date == null || shiftType == null || week == null) {
            return false;
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        int shiftIndex = findShiftIndex(organizer, date, shiftType, week);
        if (shiftIndex == -1) {
            return false;
        }

        return organizer.isEmployeeAssignedToShift(shiftIndex, employee, role, week == ShiftWeek.NEXT);
    }

    public int getAssignedEmployeeCountForRole(int branchId, LocalDate date, Shift.ShiftType shiftType,
                                               int roleId, ShiftWeek week) {
        if (!employeeController.branchExists(branchId)) {
            return 0;
        }

        Role role = employeeController.getRole(roleId);
        if (role == null || date == null || shiftType == null || week == null) {
            return 0;
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        int shiftIndex = findShiftIndex(organizer, date, shiftType, week);
        if (shiftIndex == -1) {
            return 0;
        }

        return organizer.getAssignedCountForRole(shiftIndex, role, week == ShiftWeek.NEXT);
    }

    public int getRequiredEmployeeCountForRole(int branchId, LocalDate date, Shift.ShiftType shiftType,
                                               int roleId, ShiftWeek week) {
        if (!employeeController.branchExists(branchId)) {
            return 0;
        }

        Role role = employeeController.getRole(roleId);
        if (role == null || date == null || shiftType == null || week == null) {
            return 0;
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        int shiftIndex = findShiftIndex(organizer, date, shiftType, week);
        if (shiftIndex == -1) {
            return 0;
        }

        Shift shift = week == ShiftWeek.NEXT
                ? organizer.getNextWeekShift(shiftIndex)
                : organizer.getCurrentWeekShift(shiftIndex);
        return shift.getRequiredCountForRole(role);
    }

    public boolean increaseShiftRequirement(int branchId, LocalDate date, Shift.ShiftType shiftType,
                                            int roleId, int amount, ShiftWeek week) {
        if (!employeeController.branchExists(branchId) || amount <= 0 || week == null) {
            return false;
        }

        Role role = employeeController.getRole(roleId);
        if (role == null || date == null || shiftType == null) {
            return false;
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        int shiftIndex = findShiftIndex(organizer, date, shiftType, week);
        if (shiftIndex == -1) {
            return false;
        }

        Shift shift = week == ShiftWeek.NEXT
                ? organizer.getNextWeekShift(shiftIndex)
                : organizer.getCurrentWeekShift(shiftIndex);
        int currentRequired = shift.getRequiredCountForRole(role);
        return organizer.changeShiftRequirement(shiftIndex, role, currentRequired + amount, week == ShiftWeek.NEXT);
    }

    public ShiftWeek findShiftWeek(int branchId, LocalDate date, Shift.ShiftType shiftType) {
        if (!employeeController.branchExists(branchId) || date == null || shiftType == null) {
            return null;
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        if (findShiftIndex(organizer, date, shiftType, ShiftWeek.CURRENT) != -1) {
            return ShiftWeek.CURRENT;
        }
        if (findShiftIndex(organizer, date, shiftType, ShiftWeek.NEXT) != -1) {
            return ShiftWeek.NEXT;
        }

        return null;
    }

    public boolean removeEmployeeFromShift(String userName, int branchId, int shiftIndex) {
        if (!employeeController.branchExists(branchId)) {
            return false;
        }

        Employee employee = employeeController.getEmployee(userName);
        if (employee == null) {
            return false;
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        try {
            return organizer.removeEmployeeFromShift(shiftIndex, employee);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String currentWeekShiftsToString() {
        ArrayList<Branch> branches = employeeController.getBranches();
        if (branches.isEmpty()) {
            return "No branches in the system";
        }

        StringBuilder builder = new StringBuilder();
        for (Branch branch : branches) {
            int branchId = branch.getId();
            ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
            builder.append("===== Branch ").append(branchId).append(" Current Week =====\n");
            builder.append(organizer.showCurrentWeekShifts());
        }

        return builder.toString();
    }

    public String nextWeekShiftsToString() {
        ArrayList<Branch> branches = employeeController.getBranches();
        if (branches.isEmpty()) {
            return "No branches in the system";
        }

        StringBuilder builder = new StringBuilder();
        for (Branch branch : branches) {
            int branchId = branch.getId();
            ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
            builder.append("===== Branch ").append(branchId).append(" Next Week =====\n");
            builder.append(organizer.showNextWeekShifts());
        }

        return builder.toString();
    }

    public String allShiftsHistoryToString() {
        ArrayList<Branch> branches = employeeController.getBranches();
        if (branches.isEmpty()) {
            return "No branches in the system";
        }

        StringBuilder builder = new StringBuilder();
        for (Branch branch : branches) {
            int branchId = branch.getId();
            ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
            builder.append("===== Branch ").append(branchId).append(" Shifts History =====\n");
            builder.append(organizer.showAllShiftsHistory()).append("\n");
        }

        return builder.toString();
    }

    public String shiftsHistoryByBranchToString(int branchId) {
        if (!employeeController.branchExists(branchId)) {
            return "branch not found";
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        return organizer.showShiftsHistoryByBranch(branchId);
    }

    public String shiftRequirementsToString(int branchId, LocalDate date, Shift.ShiftType shiftType) {
        return shiftRequirementsToString(branchId, date, shiftType, ShiftWeek.NEXT);
    }

    public String shiftRequirementsToString(int branchId, int shiftIndex, ShiftWeek week) {
        if (!employeeController.branchExists(branchId)) {
            return "branch not found";
        }
        if (week == null) {
            return "invalid shift parameters";
        }

        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        Shift shift;

        try {
            shift = week == ShiftWeek.NEXT
                    ? organizer.getNextWeekShift(shiftIndex)
                    : organizer.getCurrentWeekShift(shiftIndex);
        } catch (IllegalArgumentException e) {
            return "shift not found in " + weekToText(week) + " week";
        }

        Map<Role, Integer> requirements = shift.getRequirements();
        StringBuilder builder = new StringBuilder();
        builder.append("===== Shift Requirements =====\n");
        builder.append("Week: ").append(weekToText(week)).append("\n");
        builder.append("Day: ").append(shift.getDate().getDayOfWeek())
                .append(", Date: ").append(shift.getDate())
                .append(", Type: ").append(shift.getShiftType()).append("\n");
        if (requirements.isEmpty()) {
            builder.append("No requirements set\n");
        } else {
            for (Map.Entry<Role, Integer> entry : requirements.entrySet()) {
                builder.append(entry.getKey().getRoleName())
                        .append(" (ID: ").append(entry.getKey().getRoleID()).append(")")
                        .append(": ").append(entry.getValue()).append("\n");
            }
        }
        builder.append("==============================");
        return builder.toString();
    }

    // ADDED: returns formatted requirements for a specific shift in current or next week
    public String shiftRequirementsToString(int branchId, LocalDate date, Shift.ShiftType shiftType, ShiftWeek week) {
        if (!employeeController.branchExists(branchId)) {
            return "branch not found";
        }
        if (date == null || shiftType == null || week == null) {
            return "invalid shift parameters";
        }
        ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
        int shiftIndex = findShiftIndex(organizer, date, shiftType, week);
        if (shiftIndex == -1) {
            return "shift not found in " + weekToText(week) + " week";
        }
        Shift shift = week == ShiftWeek.NEXT
                ? organizer.getNextWeekShift(shiftIndex)
                : organizer.getCurrentWeekShift(shiftIndex);
        Map<Role, Integer> requirements = shift.getRequirements();
        StringBuilder builder = new StringBuilder();
        builder.append("===== Shift Requirements =====\n");
        builder.append("Week: ").append(weekToText(week)).append("\n");
        builder.append("Day: ").append(date.getDayOfWeek())
                .append(", Date: ").append(date)
                .append(", Type: ").append(shiftType).append("\n");
        if (requirements.isEmpty()) {
            builder.append("No requirements set\n");
        } else {
            for (Map.Entry<Role, Integer> entry : requirements.entrySet()) {
                builder.append(entry.getKey().getRoleName())
                        .append(" (ID: ").append(entry.getKey().getRoleID()).append(")")
                        .append(": ").append(entry.getValue()).append("\n");
            }
        }
        builder.append("==============================");
        return builder.toString();
    }

    private ShiftOrganizer getOrCreateOrganizer(int branchId) {
        return shiftOrganizers.computeIfAbsent(branchId, ShiftOrganizer::new);
    }

    private void setShiftRequirement(Shift shift, Role role, int amount) {
        if (amount == 0) {
            shift.removeRequirement(role);
        } else {
            shift.addRequirement(role, amount);
        }
    }

    private int findNextWeekShiftIndex(ShiftOrganizer organizer, LocalDate date, Shift.ShiftType shiftType) {
        return findShiftIndex(organizer, date, shiftType, ShiftWeek.NEXT);
    }

    private int findShiftIndex(ShiftOrganizer organizer, LocalDate date, Shift.ShiftType shiftType, ShiftWeek week) {
        Shift[] shifts = week == ShiftWeek.NEXT
                ? organizer.getNextWeekShifts()
                : organizer.getCurrentWeekShifts();

        for (int i = 0; i < shifts.length; i++) {
            if (shifts[i].getDate().equals(date) && shifts[i].getShiftType() == shiftType) {
                return i;
            }
        }

        return -1;
    }

    private String weekToText(ShiftWeek week) {
        return week == ShiftWeek.NEXT ? "next" : "current";
    }
}
