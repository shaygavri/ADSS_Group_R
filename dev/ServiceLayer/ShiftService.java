package ServiceLayer;

import DomainLayer.EmployeeController;
import DomainLayer.Role;
import DomainLayer.Shift;
import DomainLayer.ShiftController;
import DomainLayer.TransportationIntegrationController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class ShiftService {
    private final ShiftController shiftController;
    private final EmployeeController employeeController;
    private final TransportationIntegrationController transportationIntegrationController;

    public ShiftService() {
        this.shiftController = ShiftController.getInstance();
        this.employeeController = EmployeeController.getInstance();
        this.transportationIntegrationController = TransportationIntegrationController.getInstance();
    }

    private void syncCurrentWeekIfNeeded() {
        shiftController.syncCurrentWeekIfNeeded();
    }

    public boolean publishNextWeek() {
        syncCurrentWeekIfNeeded();
        transportationIntegrationController.syncNextWeekDeliveriesWithShifts();
        System.out.println(shiftController.nextWeekPublishSummaryToString());
        if (!shiftController.publishNextWeek()) {
            System.out.println("could not publish next week, some shifts are still missing required roles:");
            System.out.println(shiftController.nextWeekMissingRequirementsToString());
            return false;
        }

        System.out.println("next week published successfully");
        return true;
    }

    public boolean publishNextWeekRequirements(String deadlineOption, String customDate) {
        syncCurrentWeekIfNeeded();
        transportationIntegrationController.syncNextWeekDeliveriesWithShifts();
        LocalDateTime deadline = parseAvailabilityDeadline(deadlineOption, customDate);
        if (deadline == null) {
            return false;
        }

        if (!shiftController.publishNextWeekRequirements(deadline)) {
            System.out.println("could not publish next week requirements");
            return false;
        }

        System.out.println("next week requirements published successfully. employees can submit availability until " + deadline);
        return true;
    }

    public boolean setAsCurrentWeek() {
        syncCurrentWeekIfNeeded();
        if (!shiftController.setAsCurrentWeek()) {
            System.out.println("could not set next week as current week, publish it first");
            return false;
        }

        System.out.println("next week is now the current week");
        return true;
    }

    public boolean changeShiftRequirement(String branchIdStr, String dateStr, String shiftTypeStr,
                                          String roleIdStr, String amountStr) {
        syncCurrentWeekIfNeeded();
        return changeShiftRequirement(branchIdStr, dateStr, shiftTypeStr, roleIdStr, amountStr, "NEXT");
    }

    public boolean markHolidayDay(String branchIdStr, String shiftNumberStr, String weekStr) {
        syncCurrentWeekIfNeeded();
        int branchId;
        int dayNumber;
        ShiftController.ShiftWeek week;

        try {
            branchId = Integer.parseInt(branchIdStr.trim());
            dayNumber = Integer.parseInt(shiftNumberStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("branch id and day number must be numbers");
            return false;
        }

        try {
            week = ShiftController.ShiftWeek.valueOf(weekStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("week must be CURRENT or NEXT");
            return false;
        }

        if (week == ShiftController.ShiftWeek.NEXT && shiftController.isNextWeekLocked(branchId)) {
            System.out.println("cannot mark a holiday day after next week was published");
            return false;
        }
        if (week == ShiftController.ShiftWeek.NEXT
                && shiftController.hasPublishedNextWeekRequirements(branchId)
                && !shiftController.canEmployeesUpdateAvailability(branchId)) {
            System.out.println("cannot mark a holiday day because the one-day deadline already passed");
            return false;
        }

        if (dayNumber < 1 || dayNumber > 7) {
            System.out.println("day number must be between 1 and 7");
            return false;
        }

        int shiftNumber = (dayNumber - 1) * 2;

        if (!shiftController.markHolidayDay(branchId, shiftNumber, week)) {
            System.out.println("could not mark holiday day");
            return false;
        }

        System.out.println("holiday day updated successfully");
        return true;
    }

    public void showNextWeekStatus() {
        syncCurrentWeekIfNeeded();
        System.out.println(shiftController.nextWeekStatusToString());
    }

    public boolean changeShiftRequirementByShiftNumber(String branchIdStr, String shiftNumberStr,
                                                       String roleInput, String amountStr, String weekStr) {
        syncCurrentWeekIfNeeded();
        int branchId;
        int shiftNumber;
        int amount;
        ShiftController.ShiftWeek week;
        Role role;

        try {
            branchId = Integer.parseInt(branchIdStr.trim());
            shiftNumber = Integer.parseInt(shiftNumberStr.trim());
            amount = Integer.parseInt(amountStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("branch id, shift number and amount must be numbers");
            return false;
        }

        role = employeeController.getRoleByIdOrName(roleInput);
        if (role == null) {
            System.out.println("role not found");
            return false;
        }

        try {
            week = ShiftController.ShiftWeek.valueOf(weekStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("week must be CURRENT or NEXT");
            return false;
        }

        if (week == ShiftController.ShiftWeek.NEXT) {
            if (shiftController.isNextWeekLocked(branchId)) {
                System.out.println("cannot change next week requirements after next week was published");
                return false;
            }
            if (shiftController.hasPublishedNextWeekRequirements(branchId)
                    && !shiftController.canEmployeesUpdateAvailability(branchId)) {
                System.out.println("cannot change next week requirements because the one-day deadline already passed");
                return false;
            }
        }

        if (shiftNumber < 0 || shiftNumber > 13) {
            System.out.println("shift number must be between 0 and 13");
            return false;
        }

        if (amount < 0) {
            System.out.println("amount cannot be negative");
            return false;
        }

        if (!shiftController.changeShiftRequirement(branchId, shiftNumber, role.getRoleID(), amount, week)) {
            System.out.println("could not change shift requirement");
            return false;
        }

        System.out.println("shift requirement changed successfully");
        return true;
    }

    public boolean changeShiftRequirement(String branchIdStr, String dateStr, String shiftTypeStr,
                                          String roleInput, String amountStr, String weekStr) {
        syncCurrentWeekIfNeeded();
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

        if (week == ShiftController.ShiftWeek.NEXT) {
            if (shiftController.isNextWeekLocked(branchId)) {
                System.out.println("cannot change next week requirements after next week was published");
                return false;
            }
            if (shiftController.hasPublishedNextWeekRequirements(branchId)
                    && !shiftController.canEmployeesUpdateAvailability(branchId)) {
                System.out.println("cannot change next week requirements because the one-day deadline already passed");
                return false;
            }
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
        syncCurrentWeekIfNeeded();
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

    public void showAvailableEmployeesForShiftByRole(String branchIdStr, String shiftNumberStr, String roleInput) {
        syncCurrentWeekIfNeeded();
        int branchId;
        int shiftNumber;
        Role role;

        try {
            branchId = Integer.parseInt(branchIdStr.trim());
            shiftNumber = Integer.parseInt(shiftNumberStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("branch id and shift number must be numbers");
            return;
        }

        role = employeeController.getRoleByIdOrName(roleInput);
        if (role == null) {
            System.out.println("role not found");
            return;
        }

        if (shiftNumber < 0 || shiftNumber > 13) {
            System.out.println("shift number must be between 0 and 13");
            return;
        }

        System.out.println(shiftController.availableEmployeesForShiftByRoleToString(branchId, shiftNumber, role.getRoleID()));
    }

    public boolean assignEmployee(String employeeUserName, String branchIdStr, String dateStr,
                                  String shiftTypeStr, String roleInput) {
        syncCurrentWeekIfNeeded();
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

    public boolean assignEmployeeByShiftNumber(String employeeUserName, String branchIdStr,
                                               String shiftNumberStr, String roleInput) {
        syncCurrentWeekIfNeeded();
        int branchId;
        int shiftNumber;
        Role role;

        try {
            branchId = Integer.parseInt(branchIdStr.trim());
            shiftNumber = Integer.parseInt(shiftNumberStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("branch id and shift number must be numbers");
            return false;
        }

        role = employeeController.getRoleByIdOrName(roleInput);
        if (role == null) {
            System.out.println("role not found");
            return false;
        }

        if (shiftController.isNextWeekLocked(branchId)) {
            System.out.println("cannot assign employees after next week was published");
            return false;
        }

        if (shiftNumber < 0 || shiftNumber > 13) {
            System.out.println("shift number must be between 0 and 13");
            return false;
        }

        if (!shiftController.assignEmployee(employeeUserName, branchId, shiftNumber, role.getRoleID())) {
            System.out.println("could not assign employee to shift. check branch, availability, role, and shift requirements");
            return false;
        }

        System.out.println("employee assigned successfully");
        return true;
    }

    public boolean removeEmployeeFromShiftByShiftNumber(String employeeUserName, String branchIdStr,
                                                        String shiftNumberStr) {
        syncCurrentWeekIfNeeded();
        int branchId;
        int shiftNumber;

        try {
            branchId = Integer.parseInt(branchIdStr.trim());
            shiftNumber = Integer.parseInt(shiftNumberStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("branch id and shift number must be numbers");
            return false;
        }

        if (shiftController.isNextWeekLocked(branchId)) {
            System.out.println("cannot remove employees after next week was published");
            return false;
        }

        if (shiftNumber < 0 || shiftNumber > 13) {
            System.out.println("shift number must be between 0 and 13");
            return false;
        }

        if (!shiftController.removeEmployeeFromShift(employeeUserName, branchId, shiftNumber)) {
            System.out.println("could not remove employee from shift");
            return false;
        }

        System.out.println("employee removed from shift successfully");
        return true;
    }

    // ADDED: shows the requirements for a specific shift in next week by default
    public void showShiftRequirements(String branchIdStr, String dateStr, String shiftTypeStr) {
        showShiftRequirements(branchIdStr, dateStr, shiftTypeStr, "NEXT");
    }

    // ADDED: shows the requirements for a specific shift in current or next week
    public void showShiftRequirements(String branchIdStr, String dateStr, String shiftTypeStr, String weekStr) {
        syncCurrentWeekIfNeeded();
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

    public void showShiftRequirementsByShiftNumber(String branchIdStr, String shiftNumberStr, String weekStr) {
        syncCurrentWeekIfNeeded();
        int branchId;
        int shiftNumber;
        ShiftController.ShiftWeek week;

        try {
            branchId = Integer.parseInt(branchIdStr.trim());
            shiftNumber = Integer.parseInt(shiftNumberStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("branch id and shift number must be numbers");
            return;
        }

        try {
            week = ShiftController.ShiftWeek.valueOf(weekStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("week must be CURRENT or NEXT");
            return;
        }

        if (shiftNumber < 0 || shiftNumber > 13) {
            System.out.println("shift number must be between 0 and 13");
            return;
        }

        System.out.println(shiftController.shiftRequirementsToString(branchId, shiftNumber, week));
    }

    public void showCurrentWeekShift() {
        syncCurrentWeekIfNeeded();
        System.out.println(shiftController.currentWeekShiftsToString());
    }

    public void showNextWeekShift() {
        syncCurrentWeekIfNeeded();
        System.out.println(shiftController.nextWeekShiftsToString());
    }

    public void showAllShiftsHistory() {
        syncCurrentWeekIfNeeded();
        System.out.println(shiftController.allShiftsHistoryToString());
    }

    public void showShiftsHistoryByBranch(String branchIdStr) {
        syncCurrentWeekIfNeeded();
        int branchId;

        try {
            branchId = Integer.parseInt(branchIdStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("branch id must be a number");
            return;
        }

        System.out.println(shiftController.shiftsHistoryByBranchToString(branchId));
    }

    private LocalDateTime parseAvailabilityDeadline(String deadlineOption, String customDate) {
        if (deadlineOption == null || deadlineOption.isBlank()) {
            System.out.println("deadline option cannot be empty");
            return null;
        }

        String normalized = deadlineOption.trim().toUpperCase();
        switch (normalized) {
            case "1", "1_DAY" -> {
                return LocalDateTime.now().plusDays(1);
            }
            case "2", "2_DAYS" -> {
                return LocalDateTime.now().plusDays(2);
            }
            case "3", "CUSTOM" -> {
                if (customDate == null || customDate.isBlank()) {
                    System.out.println("custom date cannot be empty");
                    return null;
                }

                try {
                    return LocalDate.parse(customDate.trim()).atTime(LocalTime.MAX);
                } catch (DateTimeParseException e) {
                    System.out.println("custom date must be in yyyy-mm-dd format");
                    return null;
                }
            }
            default -> {
                System.out.println("deadline option must be 1, 2 or 3");
                return null;
            }
        }
    }
}
