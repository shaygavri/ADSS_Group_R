package PresentationLayer;

import ServiceLayer.Service;
import java.util.Scanner;

// User menu - for HR manager
public class Menu_HR {
    private static final String[] SHIFT_OPTIONS = {
            "0 - Sunday Morning",
            "1 - Sunday Evening",
            "2 - Monday Morning",
            "3 - Monday Evening",
            "4 - Tuesday Morning",
            "5 - Tuesday Evening",
            "6 - Wednesday Morning",
            "7 - Wednesday Evening",
            "8 - Thursday Morning",
            "9 - Thursday Evening",
            "10 - Friday Morning",
            "11 - Friday Evening",
            "12 - Saturday Morning",
            "13 - Saturday Evening"
    };
    private static final String[] DAY_OPTIONS = {
            "1 - Sunday",
            "2 - Monday",
            "3 - Tuesday",
            "4 - Wednesday",
            "5 - Thursday",
            "6 - Friday",
            "7 - Saturday"
    };

    public String in;
    public String userName;
    public Service service;

    public void printMenu() {
        System.out.println("\n========== HR MENU ==========");

        System.out.println("\n--- Branch Management ---");
        System.out.println("1. show branches IDs");
        System.out.println("2. add branch");
        System.out.println("3. remove branch");

        System.out.println("\n--- Employee and Role Management ---");
        System.out.println("4. create new role");
        System.out.println("5. add role to employee");
        System.out.println("6. hire employee");
        System.out.println("7. fire employee");
        System.out.println("8. change employee's hourly salary");
        System.out.println("9. show all employees and their roles");
        System.out.println("10. show all existing roles");

        System.out.println("\n--- Shift and Scheduling Management ---");
        System.out.println("11. publish next week requirements");
        System.out.println("12. show next week status");
        System.out.println("13. change shift requirement (current/next week)");
        System.out.println("14. show available employees for shift by role");
        System.out.println("15. assign employee");
        System.out.println("16. remove employee from shift");
        System.out.println("17. show current week shift");
        System.out.println("18. show next week shift");
        System.out.println("19. show all shifts history");
        System.out.println("20. show shifts history by branch");
        System.out.println("21. show requirements for shift (current/next week)");
        System.out.println("22. define holiday day");
        System.out.println("23. publish next week");

        System.out.println("24. logout");

        getAnswer();
    }

    public void getAnswer() {
        Scanner scanner = new Scanner(System.in);
        int input = scanner.nextInt();
        scanner.nextLine();

        switch (input) {
            case 1 -> {
                service.showBranches();
                printMenu();
            }

            case 2 -> {
                System.out.println("Enter new branch ID:");
                String branchId = scanner.nextLine();
                service.addBranch(branchId);
                printMenu();
            }

            case 3 -> {
                System.out.println("Enter branch ID to remove:");
                String branchId = scanner.nextLine();
                service.removeBranch(branchId);
                printMenu();
            }

            case 4 -> {
                System.out.println("Enter role ID:");
                String roleId = scanner.nextLine();

                System.out.println("Enter role name:");
                String roleName = scanner.nextLine();

                System.out.println("Enter role description:");
                String description = scanner.nextLine();

                service.createRole(roleId, roleName, description);
                printMenu();
            }

            case 5 -> {
                System.out.println("Enter employee username:");
                String employeeUserName = scanner.nextLine();

                service.showRoleOptions();
                System.out.println("Enter role ID or role name to add:");
                String roleInput = scanner.nextLine();

                service.addRoleToEmployee(employeeUserName, roleInput);
                printMenu();
            }

            case 6 -> {
                System.out.println("Enter employee username:");
                String employeeUserName = scanner.nextLine();

                System.out.println("Enter employee ID:");
                String employeeId = scanner.nextLine();

                System.out.println("Enter employee password:");
                String password = scanner.nextLine();

                System.out.println("Enter branch ID:");
                String branchId = scanner.nextLine();

                System.out.println("Enter hourly salary:");
                String hourlySalary = scanner.nextLine();

                System.out.println("Enter employment type (FULL_TIME / PART_TIME):");
                String employmentType = scanner.nextLine();

                System.out.println("Enter bank number:");
                String bankNumber = scanner.nextLine();

                System.out.println("Enter bank branch number:");
                String bankBranchNumber = scanner.nextLine();

                System.out.println("Enter bank account number:");
                String bankAccountNumber = scanner.nextLine();

                service.hireEmployee(
                        employeeUserName,
                        employeeId,
                        password,
                        branchId,
                        hourlySalary,
                        employmentType,
                        bankNumber,
                        bankBranchNumber,
                        bankAccountNumber);
                printMenu();
            }

            case 7 -> {
                System.out.println("Enter employee username to fire:");
                String employeeUserName = scanner.nextLine();
                service.fireEmployee(employeeUserName);
                printMenu();
            }

            case 8 -> {
                System.out.println("Enter employee username:");
                String employeeUserName = scanner.nextLine();

                System.out.println("Enter new hourly salary:");
                String newSalary = scanner.nextLine();

                service.changeEmployeeSalary(employeeUserName, newSalary);
                printMenu();
            }

            case 9 -> {
                service.showAllEmployeesAndRoles();
                printMenu();
            }

            case 10 -> {
                service.showAllRoles();
                printMenu();
            }

            case 11 -> {
                System.out.println("Choose availability deadline:");
                System.out.println("1. 1 day from now");
                System.out.println("2. 2 days from now");
                System.out.println("3. custom date");
                String deadlineOption = scanner.nextLine();

                String customDate = "";
                if ("3".equals(deadlineOption.trim())) {
                    System.out.println("Enter custom date (yyyy-mm-dd):");
                    customDate = scanner.nextLine();
                }

                service.publishNextWeekRequirements(deadlineOption, customDate);
                printMenu();
            }

            case 12 -> {
                service.showNextWeekStatus();
                printMenu();
            }

            case 13 -> {
                System.out.println("Enter branch ID:");
                String branchId = scanner.nextLine();

                System.out.println("Enter week (CURRENT / NEXT):");
                String week = scanner.nextLine();

                printShiftOptions();

                System.out.println("Enter shift number (0-13):");
                String shiftNumber = scanner.nextLine();

                service.showRoleOptions();
                System.out.println("Enter role ID or role name:");
                String roleInput = scanner.nextLine();

                System.out.println("Enter new required amount:");
                String amount = scanner.nextLine();

                service.changeShiftRequirementByShiftNumber(branchId, shiftNumber, roleInput, amount, week);
                printMenu();
            }

            case 14 -> {
                System.out.println("Enter branch ID:");
                String branchId = scanner.nextLine();

                printShiftOptions();

                System.out.println("Enter shift number (0-13):");
                String shiftNumber = scanner.nextLine();

                service.showRoleOptions();
                System.out.println("Enter role ID or role name:");
                String roleInput = scanner.nextLine();

                service.showAvailableEmployeesForShiftByRole(branchId, shiftNumber, roleInput);
                printMenu();
            }

            case 15 -> {
                System.out.println("Enter branch ID:");
                String branchId = scanner.nextLine();

                printShiftOptions();

                System.out.println("Enter shift number (0-13):");
                String shiftNumber = scanner.nextLine();

                System.out.println("Current requirements for this shift:");
                service.showShiftRequirementsByShiftNumber(branchId, shiftNumber, "NEXT");

                System.out.println("Enter employee username:");
                String employeeUserName = scanner.nextLine();

                service.showRoleOptions();
                System.out.println("Enter role ID or role name from this shift's requirements:");
                String roleInput = scanner.nextLine();

                service.assignEmployeeByShiftNumber(employeeUserName, branchId, shiftNumber, roleInput);
                printMenu();
            }

            case 16 -> {
                System.out.println("Enter employee username:");
                String employeeUserName = scanner.nextLine();

                System.out.println("Enter branch ID:");
                String branchId = scanner.nextLine();

                printShiftOptions();

                System.out.println("Enter shift number (0-13):");
                String shiftNumber = scanner.nextLine();

                service.removeEmployeeFromShiftByShiftNumber(employeeUserName, branchId, shiftNumber);
                printMenu();
            }

            case 17 -> {
                service.showCurrentWeekShift();
                printMenu();
            }

            case 18 -> {
                service.showNextWeekShift();
                printMenu();
            }

            case 19 -> {
                service.showAllShiftsHistory();
                printMenu();
            }

            case 20 -> {
                System.out.println("Enter branch ID:");
                String branchId = scanner.nextLine();
                service.showShiftsHistoryByBranch(branchId);
                printMenu();
            }

            // ADDED: show requirements for a specific shift in current or next week (Task #1)
            case 21 -> {
                System.out.println("Enter branch ID:");
                String branchId20 = scanner.nextLine();

                System.out.println("Enter week (CURRENT / NEXT):");
                String week20 = scanner.nextLine();

                printShiftOptions();

                System.out.println("Enter shift number (0-13):");
                String shiftNumber20 = scanner.nextLine();

                service.showShiftRequirementsByShiftNumber(branchId20, shiftNumber20, week20);
                printMenu();
            }

            case 22 -> {
                System.out.println("Enter branch ID:");
                String branchId = scanner.nextLine();

                System.out.println("Enter week (CURRENT / NEXT):");
                String week = scanner.nextLine();

                printDayOptions();

                System.out.println("Choose a day you want as holiday day:");
                String dayNumber = scanner.nextLine();

                service.markHolidayDay(branchId, dayNumber, week);
                printMenu();
            }

            case 23 -> {
                service.publishNextWeek();
                printMenu();
            }

            case 24 -> {
                service.logout(userName);
                return;
            }

            default -> {
                System.out.println("invalid input, please try again");
                printMenu();
            }
        }
    }

    private void printShiftOptions() {
        System.out.println("Choose the shift:");
        for (String shiftOption : SHIFT_OPTIONS) {
            System.out.println(shiftOption);
        }
    }

    private void printDayOptions() {
        for (String dayOption : DAY_OPTIONS) {
            System.out.println(dayOption);
        }
    }
}
