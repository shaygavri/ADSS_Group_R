package ServiceLayer;

import DomainLayer.EmployeeController;
import DomainLayer.Role;
import DomainLayer.Shift;
import DomainLayer.ShiftController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ShiftService {
    private final ShiftController shiftController;
    private final EmployeeController employeeController;

    public ShiftService() {
        this.shiftController = ShiftController.getInstance();
        this.employeeController = EmployeeController.getInstance();
    }

    public boolean publishNextWeek() {
        if (!shiftController.publishNextWeek()) {
            System.out.println("could not publish next week, make sure every shift has a shift manager");
            return false;
        }

        System.out.println("next week published successfully");
        return true;
    }

    public boolean setAsCurrentWeek() {
        if (!shiftController.setAsCurrentWeek()) {
            System.out.println("could not set next week as current week, publish it first");
            return false;
        }

        System.out.println("next week is now the current week");
        return true;
    }

    public boolean changeShiftRequirement(String branchIdStr, String dateStr, String shiftTypeStr,
                                          String roleIdStr, String amountStr) {
        return changeShiftRequirement(branchIdStr, dateStr, shiftTypeStr, roleIdStr, amountStr, "NEXT");
    }

    public boolean changeShiftRequirement(String branchIdStr, String dateStr, String shiftTypeStr,
                                          String roleInput, String amountStr, String weekStr) {
        int branchId;
        int amount;
        LocalDate date;
        Shift.ShiftType shiftType;
        ShiftController.ShiftWeek week;
        Role role;

        try {
            branchId = Integer.parseInt(branchIdStr.trim());
            amount = Integer.parseInt(amountStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("branch id and amount must be numbers");
            return false;
        }

        role = employeeController.getRoleByIdOrName(roleInput);
        if (role == null) {
            System.out.println("role not found");
            return false;
        }

        try {
            date = LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            System.out.println("date must be in yyyy-mm-dd format");
            return false;
        }

        try {
            shiftType = Shift.ShiftType.valueOf(shiftTypeStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("shift type must be MORNING or EVENING");
            return false;
        }

        try {
            week = ShiftController.ShiftWeek.valueOf(weekStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("week must be CURRENT or NEXT");
            return false;
        }

        if (amount < 0) {
            System.out.println("amount cannot be negative");
            return false;
        }

        if (!shiftController.changeShiftRequirement(branchId, date, shiftType, role.getRoleID(), amount, week)) {
            System.out.println("could not change shift requirement");
            return false;
        }

        System.out.println("shift requirement changed successfully");
        return true;
    }

    public void showAvailableEmployeesForShiftByRole(String branchIdStr, String dateStr,
                                                     String shiftTypeStr, String roleInput) {
        int branchId;
        LocalDate date;
        Shift.ShiftType shiftType;
        Role role;

        try {
            branchId = Integer.parseInt(branchIdStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("branch id must be a number");
            return;
        }

        role = employeeController.getRoleByIdOrName(roleInput);
        if (role == null) {
            System.out.println("role not found");
            return;
        }

        try {
            date = LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            System.out.println("date must be in yyyy-mm-dd format");
            return;
        }

        try {
            shiftType = Shift.ShiftType.valueOf(shiftTypeStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("shift type must be MORNING or EVENING");
            return;
        }

        System.out.println(shiftController.availableEmployeesForShiftByRoleToString(branchId, date, shiftType, role.getRoleID()));
    }

    public boolean assignEmployee(String employeeUserName, String branchIdStr, String dateStr,
                                  String shiftTypeStr, String roleInput) {
        int branchId;
        LocalDate date;
        Shift.ShiftType shiftType;
        Role role;

        try {
            branchId = Integer.parseInt(branchIdStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("branch id must be a number");
            return false;
        }

        role = employeeController.getRoleByIdOrName(roleInput);
        if (role == null) {
            System.out.println("role not found");
            return false;
        }

        try {
            date = LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            System.out.println("date must be in yyyy-mm-dd format");
            return false;
        }

        try {
            shiftType = Shift.ShiftType.valueOf(shiftTypeStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("shift type must be MORNING or EVENING");
            return false;
        }

        if (!shiftController.assignEmployee(employeeUserName, branchId, date, shiftType, role.getRoleID())) {
            System.out.println("could not assign employee to shift");
            return false;
        }

        System.out.println("employee assigned successfully");
        return true;
    }

    // ADDED: shows the requirements for a specific shift in next week by default
    public void showShiftRequirements(String branchIdStr, String dateStr, String shiftTypeStr) {
        showShiftRequirements(branchIdStr, dateStr, shiftTypeStr, "NEXT");
    }

    // ADDED: shows the requirements for a specific shift in current or next week
    public void showShiftRequirements(String branchIdStr, String dateStr, String shiftTypeStr, String weekStr) {
        int branchId;
        LocalDate date;
        Shift.ShiftType shiftType;
        ShiftController.ShiftWeek week;

        try {
            branchId = Integer.parseInt(branchIdStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("branch id must be a number");
            return;
        }

        try {
            date = LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            System.out.println("date must be in yyyy-mm-dd format");
            return;
        }

        try {
            shiftType = Shift.ShiftType.valueOf(shiftTypeStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("shift type must be MORNING or EVENING");
            return;
        }

        try {
            week = ShiftController.ShiftWeek.valueOf(weekStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("week must be CURRENT or NEXT");
            return;
        }

        System.out.println(shiftController.shiftRequirementsToString(branchId, date, shiftType, week));
    }

    public void showCurrentWeekShift() {
        System.out.println(shiftController.currentWeekShiftsToString());
    }

    public void showNextWeekShift() {
        System.out.println(shiftController.nextWeekShiftsToString());
    }

    public void showAllShiftsHistory() {
        System.out.println(shiftController.allShiftsHistoryToString());
    }

    public void showShiftsHistoryByBranch(String branchIdStr) {
        int branchId;

        try {
            branchId = Integer.parseInt(branchIdStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("branch id must be a number");
            return;
        }

        System.out.println(shiftController.shiftsHistoryByBranchToString(branchId));
    }
}
