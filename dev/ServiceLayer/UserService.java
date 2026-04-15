package ServiceLayer;

import DomainLayer.Employee;
import DomainLayer.EmployeeController;

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
}