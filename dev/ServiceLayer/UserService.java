package ServiceLayer;

import DomainLayer.Employee;
import DomainLayer.EmployeeController;
import DomainLayer.Role;

import java.util.ArrayList;

public class UserService {
    private final EmployeeController employeeController;

    public UserService() {
        this.employeeController = EmployeeController.getInstance();
    }

    public boolean changePassword(String userName, String newPassword) {
        Employee employee = employeeController.getEmployee(userName);

        if (employee == null) {
            System.out.println("employee not found");
            return false;
        }

        if (newPassword == null || newPassword.isBlank()) {
            System.out.println("new password cannot be empty");
            return false;
        }

        employee.setPassword(newPassword);
        System.out.println("password changed successfully");
        return true;
    }

    public boolean pickAvailability(String userName, String input) {
        Employee employee = employeeController.getEmployee(userName);

        if (employee == null) {
            System.out.println("employee not found");
            return false;
        }

        try {
            ArrayList<Integer> shifts = parseShifts(input);
            employee.setAvailabilities(shifts);
            System.out.println("availability saved successfully");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void showMyAvailability(String userName) {
        Employee employee = employeeController.getEmployee(userName);

        if (employee == null) {
            System.out.println("employee not found");
            return;
        }

        ArrayList<Integer> availabilities = employee.getAvailabilities();
        availabilities.sort(Integer::compareTo);

        System.out.println("===== My Availabilities =====");
        if (availabilities.isEmpty()) {
            System.out.println("No availabilities selected");
        } else {
            for (Integer shift : availabilities) {
                System.out.println("Shift " + shift + " - " + shiftToText(shift));
            }
        }
        System.out.println("=============================");
    }

    public boolean changeAvailability(String userName, String oldInput, String newInput) {
        Employee employee = employeeController.getEmployee(userName);

        if (employee == null) {
            System.out.println("employee not found");
            return false;
        }

        int oldShift;
        int newShift;

        try {
            oldShift = Integer.parseInt(oldInput.trim());
            newShift = Integer.parseInt(newInput.trim());
        } catch (NumberFormatException e) {
            System.out.println("invalid shift number");
            return false;
        }

        if (!isValidShift(oldShift) || !isValidShift(newShift)) {
            System.out.println("shift must be between 0 and 13");
            return false;
        }

        if (!employee.hasAvailability(oldShift)) {
            System.out.println("this shift is not currently in your availability list");
            return false;
        }

        if (employee.hasAvailability(newShift)) {
            System.out.println("new shift already exists in your availability list");
            return false;
        }

        try {
            employee.replaceAvailability(oldShift, newShift);
            System.out.println("availability updated successfully");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean updateBankAccount(String userName, String bankStr, String branchStr, String accountStr) {
        Employee employee = employeeController.getEmployee(userName);

        if (employee == null) {
            System.out.println("employee not found");
            return false;
        }

        int bank, branch, account;

        try {
            bank = Integer.parseInt(bankStr.trim());
            branch = Integer.parseInt(branchStr.trim());
            account = Integer.parseInt(accountStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("invalid input, must be numbers");
            return false;
        }

        if (bank <= 0 || branch <= 0 || account <= 0) {
            System.out.println("bank details must be positive numbers");
            return false;
        }

        Employee.BankAccount newAccount = new Employee.BankAccount(bank, branch, account);

        employee.setBankAccount(newAccount);

        System.out.println("bank account updated successfully");
        return true;
    }

    public void showMyPersonalDetails(String userName) {
        Employee employee = employeeController.getEmployee(userName);

        if (employee == null) {
            System.out.println("employee not found");
            return;
        }

        System.out.println(employee);
    }

    public boolean logout(String userName) {
        Employee employee = employeeController.getEmployee(userName);

        if (employee == null) {
            System.out.println("employee not found");
            return false;
        }

        employee.setIsLoggedIn(false);
        System.out.println("logged out successfully");
        return true;
    }

    public boolean login(String userName, String password) {
        Employee employee = employeeController.getEmployee(userName);

        if (employee == null) {
            System.out.println("employee not found");
            return false;
        }

        if (!employee.checkPassword(password)) {
            System.out.println("wrong password");
            return false;
        }

        employee.setIsLoggedIn(true);
        System.out.println("logged in successfully");
        return true;
    }

    public boolean isHRManager(String userName) {
        return employeeController.employeeHasRole(userName, Role.HR_MANAGER_ID);
    }

    public boolean createRole(String roleIdStr, String roleName, String description) {
        int roleId;

        try {
            roleId = Integer.parseInt(roleIdStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("role id must be a number");
            return false;
        }

        try {
            Role role = new Role(roleId, roleName, description);

            if (!employeeController.addRole(role)) {
                System.out.println("role already exists");
                return false;
            }

            System.out.println("role created successfully");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private ArrayList<Integer> parseShifts(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("availability input cannot be empty");
        }

        String[] parts = input.trim().split("\\s+");
        ArrayList<Integer> shifts = new ArrayList<>();

        for (String part : parts) {
            int shift;
            try {
                shift = Integer.parseInt(part);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("all values must be numbers");
            }

            if (!isValidShift(shift)) {
                throw new IllegalArgumentException("shift must be between 0 and 13");
            }

            if (!shifts.contains(shift)) {
                shifts.add(shift);
            }
        }

        return shifts;
    }

    private boolean isValidShift(int shift) {
        return shift >= 0 && shift <= 13;
    }

    private String shiftToText(int shift) {
        String[] shifts = {
                "Sunday Morning",
                "Sunday Evening",
                "Monday Morning",
                "Monday Evening",
                "Tuesday Morning",
                "Tuesday Evening",
                "Wednesday Morning",
                "Wednesday Evening",
                "Thursday Morning",
                "Thursday Evening",
                "Friday Morning",
                "Friday Evening",
                "Saturday Morning",
                "Saturday Evening"
        };

        return shifts[shift];
    }

    public boolean addRoleToEmployee(String employeeUserName, String roleInput) {
        if (!employeeController.employeeExists(employeeUserName)) {
            System.out.println("employee not found");
            return false;
        }

        Role role = employeeController.getRoleByIdOrName(roleInput);
        if (role == null) {
            System.out.println("role not found");
            return false;
        }

        if (!employeeController.addRoleToEmployee(employeeUserName, role.getRoleID())) {
            System.out.println("employee already has this role");
            return false;
        }

        System.out.println("role added to employee successfully");
        return true;
    }

    public boolean hireEmployee(String employeeUserName, String employeeId, String password,
                                String branchIdStr, String hourlySalaryStr, String employmentTypeStr,
                                String bankNumberStr, String bankBranchNumberStr, String bankAccountNumberStr) {
        int branchId;
        int hourlySalary;
        int bankNumber;
        int bankBranchNumber;
        int bankAccountNumber;
        Employee.EmploymentType employmentType;

        try {
            branchId = Integer.parseInt(branchIdStr.trim());
            hourlySalary = Integer.parseInt(hourlySalaryStr.trim());
            bankNumber = Integer.parseInt(bankNumberStr.trim());
            bankBranchNumber = Integer.parseInt(bankBranchNumberStr.trim());
            bankAccountNumber = Integer.parseInt(bankAccountNumberStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("branch, salary and bank details must be numbers");
            return false;
        }

        try {
            employmentType = Employee.EmploymentType.valueOf(employmentTypeStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("employment type must be FULL_TIME or PART_TIME");
            return false;
        }

        if (!employeeController.branchExists(branchId)) {
            System.out.println("branch not found");
            return false;
        }

        if (hourlySalary <= 0) {
            System.out.println("hourly salary must be positive");
            return false;
        }

        if (bankNumber <= 0 || bankBranchNumber <= 0 || bankAccountNumber <= 0) {
            System.out.println("bank details must be positive numbers");
            return false;
        }

        try {
            Employee.BankAccount bankAccount = new Employee.BankAccount(bankNumber, bankBranchNumber,
                    bankAccountNumber);

            Employee employee = new Employee(
                    branchId,
                    employeeUserName,
                    bankAccount,
                    hourlySalary,
                    password,
                    employmentType,
                    employeeId);

            if (!employeeController.addEmployee(employee)) {
                System.out.println("employee already exists");
                return false;
            }

            System.out.println("employee hired successfully");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean fireEmployee(String employeeUserName) {
        if (!employeeController.removeEmployee(employeeUserName)) {
            System.out.println("employee not found");
            return false;
        }

        System.out.println("employee fired successfully");
        return true;
    }

    public boolean changeEmployeeSalary(String employeeUserName, String newSalaryStr) {
        int newSalary;

        try {
            newSalary = Integer.parseInt(newSalaryStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("salary must be a number");
            return false;
        }

        if (newSalary <= 0) {
            System.out.println("salary must be positive");
            return false;
        }

        if (!employeeController.changeEmployeeSalary(employeeUserName, newSalary)) {
            System.out.println("employee not found");
            return false;
        }

        System.out.println("employee salary changed successfully");
        return true;
    }

    public void showAllEmployeesAndRoles() {
        System.out.println(employeeController.employeesAndRolesToString());
    }

    // ADDED: shows all roles and how many employees have each role
    public void showAllRoles() {
        System.out.println(employeeController.rolesAndEmployeeCountToString());
    }
}
