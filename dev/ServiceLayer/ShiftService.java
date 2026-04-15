package ServiceLayer;

import DomainLayer.Shift;
import DomainLayer.ShiftController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ShiftService {
    private final ShiftController shiftController;

    public ShiftService() {
        this.shiftController = ShiftController.getInstance();
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
        int branchId;
        int roleId;
        int amount;
        LocalDate date;
        Shift.ShiftType shiftType;

        try {
            branchId = Integer.parseInt(branchIdStr.trim());
            roleId = Integer.parseInt(roleIdStr.trim());
            amount = Integer.parseInt(amountStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("branch id, role id and amount must be numbers");
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

        if (amount < 0) {
            System.out.println("amount cannot be negative");
            return false;
        }

        if (!shiftController.changeShiftRequirement(branchId, date, shiftType, roleId, amount)) {
            System.out.println("could not change shift requirement");
            return false;
        }

        System.out.println("shift requirement changed successfully");
        return true;
    }

    public void showAvailableEmployeesForShiftByRole(String branchIdStr, String dateStr,
                                                     String shiftTypeStr, String roleIdStr) {
        int branchId;
        int roleId;
        LocalDate date;
        Shift.ShiftType shiftType;

        try {
            branchId = Integer.parseInt(branchIdStr.trim());
            roleId = Integer.parseInt(roleIdStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("branch id and role id must be numbers");
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

        System.out.println(shiftController.availableEmployeesForShiftByRoleToString(branchId, date, shiftType, roleId));
    }

    public boolean assignEmployee(String employeeUserName, String branchIdStr, String dateStr,
                                  String shiftTypeStr, String roleIdStr) {
        int branchId;
        int roleId;
        LocalDate date;
        Shift.ShiftType shiftType;

        try {
            branchId = Integer.parseInt(branchIdStr.trim());
            roleId = Integer.parseInt(roleIdStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("branch id and role id must be numbers");
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

        if (!shiftController.assignEmployee(employeeUserName, branchId, date, shiftType, roleId)) {
            System.out.println("could not assign employee to shift");
            return false;
        }

        System.out.println("employee assigned successfully");
        return true;
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
