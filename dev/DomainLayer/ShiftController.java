package DomainLayer;

import java.time.LocalDate;
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
        ArrayList<Integer> branches = employeeController.getBranches();
        if (branches.isEmpty()) {
            return false;
        }

        for (Integer branchId : branches) {
            ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
            organizer.updateAvailableEmployees(employeeController.getEmployees());

            if (!organizer.publishNextWeek()) {
                return false;
            }
        }

        return true;
    }

    public boolean setAsCurrentWeek() {
        ArrayList<Integer> branches = employeeController.getBranches();
        if (branches.isEmpty()) {
            return false;
        }

        for (Integer branchId : branches) {
            ShiftOrganizer organizer = getOrCreateOrganizer(branchId);

            if (!organizer.setNextWeekAsCurrentWeek()) {
                return false;
            }
        }

        return true;
    }

    public boolean changeShiftRequirement(int branchId, LocalDate date, Shift.ShiftType shiftType,
                                          int roleId, int amount) {
        return changeShiftRequirement(branchId, date, shiftType, roleId, amount, ShiftWeek.NEXT);
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

    public String currentWeekShiftsToString() {
        ArrayList<Integer> branches = employeeController.getBranches();
        if (branches.isEmpty()) {
            return "No branches in the system";
        }

        StringBuilder builder = new StringBuilder();
        for (Integer branchId : branches) {
            ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
            builder.append("===== Branch ").append(branchId).append(" Current Week =====\n");
            builder.append(organizer.showCurrentWeekShifts());
        }

        return builder.toString();
    }

    public String nextWeekShiftsToString() {
        ArrayList<Integer> branches = employeeController.getBranches();
        if (branches.isEmpty()) {
            return "No branches in the system";
        }

        StringBuilder builder = new StringBuilder();
        for (Integer branchId : branches) {
            ShiftOrganizer organizer = getOrCreateOrganizer(branchId);
            builder.append("===== Branch ").append(branchId).append(" Next Week =====\n");
            builder.append(organizer.showNextWeekShifts());
        }

        return builder.toString();
    }

    public String allShiftsHistoryToString() {
        ArrayList<Integer> branches = employeeController.getBranches();
        if (branches.isEmpty()) {
            return "No branches in the system";
        }

        StringBuilder builder = new StringBuilder();
        for (Integer branchId : branches) {
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
