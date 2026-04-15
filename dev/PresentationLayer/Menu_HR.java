package PresentationLayer;
import ServiceLayer.Service;
import java.util.Scanner;

// User menu - for HR manager
public class Menu_HR {

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

        System.out.println("\n--- Shift and Scheduling Management ---");
        System.out.println("10. publish next week");
        System.out.println("11. set as current week");
        System.out.println("12. change shift requirement");
        System.out.println("13. show available employees for shift by role");
        System.out.println("14. assign employee");
        System.out.println("15. show current week shift");
        System.out.println("16. show next week shift");
        System.out.println("17. show all shifts history");
        System.out.println("18. show shifts history by branch");

        System.out.println("19. logout");

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

                System.out.println("Enter role ID to add:");
                String roleId = scanner.nextLine();

                service.addRoleToEmployee(employeeUserName, roleId);
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
                        bankAccountNumber
                );
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
                service.publishNextWeek();
                printMenu();
            }

            case 11 -> {
                service.setAsCurrentWeek();
                printMenu();
            }

            case 12 -> {
                System.out.println("Enter branch ID:");
                String branchId = scanner.nextLine();

                System.out.println("Enter shift date (yyyy-mm-dd):");
                String date = scanner.nextLine();

                System.out.println("Enter shift type (MORNING / EVENING):");
                String shiftType = scanner.nextLine();

                System.out.println("Enter role ID:");
                String roleId = scanner.nextLine();

                System.out.println("Enter new required amount:");
                String amount = scanner.nextLine();

                service.changeShiftRequirement(branchId, date, shiftType, roleId, amount);
                printMenu();
            }

            case 13 -> {
                System.out.println("Enter branch ID:");
                String branchId = scanner.nextLine();

                System.out.println("Enter shift date (yyyy-mm-dd):");
                String date = scanner.nextLine();

                System.out.println("Enter shift type (MORNING / EVENING):");
                String shiftType = scanner.nextLine();

                System.out.println("Enter role ID:");
                String roleId = scanner.nextLine();

                service.showAvailableEmployeesForShiftByRole(branchId, date, shiftType, roleId);
                printMenu();
            }

            case 14 -> {
                System.out.println("Enter employee username:");
                String employeeUserName = scanner.nextLine();

                System.out.println("Enter branch ID:");
                String branchId = scanner.nextLine();

                System.out.println("Enter shift date (yyyy-mm-dd):");
                String date = scanner.nextLine();

                System.out.println("Enter shift type (MORNING / EVENING):");
                String shiftType = scanner.nextLine();

                System.out.println("Enter role ID:");
                String roleId = scanner.nextLine();

                service.assignEmployee(employeeUserName, branchId, date, shiftType, roleId);
                printMenu();
            }

            case 15 -> {
                service.showCurrentWeekShift();
                printMenu();
            }

            case 16 -> {
                service.showNextWeekShift();
                printMenu();
            }

            case 17 -> {
                service.showAllShiftsHistory();
                printMenu();
            }

            case 18 -> {
                System.out.println("Enter branch ID:");
                String branchId = scanner.nextLine();
                service.showShiftsHistoryByBranch(branchId);
                printMenu();
            }

            case 19 -> {
                service.logout(userName);
                return;
            }

            default -> {
                System.out.println("invalid input, please try again");
                printMenu();
            }
        }
    }
}
