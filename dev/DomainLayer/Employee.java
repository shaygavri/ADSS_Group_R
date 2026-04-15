package DomainLayer;

import java.time.LocalDate;
import java.util.ArrayList;

public class Employee {
    private String userName;
    private String password;
    private String id;
    private boolean isLoggedIn;
    private BankAccount bankAccount;
    private int branchID;
    private int vacationDays;
    private ArrayList<Role> roles;
    private int hourlySalary;
    private LocalDate startContract;
    private LocalDate endContract;
    private ArrayList<Integer> availabilities;
    private EmploymentType employmentType;

    public enum EmploymentType {
        FULL_TIME,
        PART_TIME
    }

    public Employee(int BID, String userName, BankAccount bankAccount, int hourlySalary,
                    String enterPassword, EmploymentType employmentType, String id) {

        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("username cannot be empty");
        }
        if (enterPassword == null || enterPassword.isBlank()) {
            throw new IllegalArgumentException("password cannot be empty");
        }
        if (employmentType == null) {
            throw new IllegalArgumentException("employment type cannot be null");
        }
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }

        this.branchID = BID;
        this.userName = userName;
        this.password = enterPassword;
        this.bankAccount = bankAccount;
        this.roles = new ArrayList<>();
        this.hourlySalary = hourlySalary;
        this.startContract = LocalDate.now();
        this.endContract = this.startContract.plusYears(1);
        this.isLoggedIn = false;
        this.availabilities = new ArrayList<>();
        this.employmentType = employmentType;

        this.id = id;
        this.vacationDays = 20;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("username cannot be empty");
        }
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password cannot be empty");
        }
        this.password = password;
    }

    public int getBranchID() {
        return branchID;
    }

    public void setBranchID(int branchID) {
        this.branchID = branchID;
    }

    public ArrayList<Role> getRoles() {
        return new ArrayList<>(roles);
    }

    public void setRoles(ArrayList<Role> roles) {
        this.roles = new ArrayList<>(roles);
    }

    public String getId() {
        return id;
    }

    public int getVacationDays() {
        return vacationDays;
    }

    public void setVacationDays(int vacationDays) {
        if (vacationDays < 0) {
            throw new IllegalArgumentException("vacation days cannot be negative");
        }
        this.vacationDays = vacationDays;
    }

    public boolean useVacationDays(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("days must be positive");
        }
        if (days > vacationDays) {
            return false;
        }

        vacationDays -= days;
        return true;
    }

    public void addVacationDays(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("days must be positive");
        }
        vacationDays += days;
    }

    public int getHourlySalary() {
        return hourlySalary;
    }

    public void setHourlySalary(int hourlySalary) {
        this.hourlySalary = hourlySalary;
    }

    public LocalDate getStartContract() {
        return startContract;
    }

    public void setStartContract(LocalDate startContract) {
        this.startContract = startContract;
    }

    public LocalDate getEndContract() {
        return endContract;
    }

    public void setEndContract(LocalDate endContract) {
        this.endContract = endContract;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        if (employmentType == null) {
            throw new IllegalArgumentException("employment type cannot be null");
        }
        this.employmentType = employmentType;
    }

    public boolean checkPassword(String input) {
        return this.password.equals(input);
    }

    public void addRole(Role role) {
        if (role != null && !roles.contains(role)) {
            roles.add(role);
        }
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public ArrayList<Integer> getAvailabilities() {
        return new ArrayList<>(availabilities);
    }

    public void setAvailabilities(ArrayList<Integer> availabilities) {
        this.availabilities = new ArrayList<>(availabilities);
    }

    public void addAvailability(int shift) {
        if (!availabilities.contains(shift)) {
            availabilities.add(shift);
        }
    }

    public boolean hasAvailability(int shift) {
        return availabilities.contains(shift);
    }

    public void replaceAvailability(int oldShift, int newShift) {
        int index = availabilities.indexOf(oldShift);
        if (index == -1) {
            throw new IllegalArgumentException("old shift does not exist");
        }
        if (availabilities.contains(newShift)) {
            throw new IllegalArgumentException("new shift already exists");
        }
        availabilities.set(index, newShift);
    }

    private String rolesToString() {
        if (roles.isEmpty()) return "None\n";

        StringBuilder sb = new StringBuilder();
        for (Role role : roles) {
            sb.append(role).append("\n");
        }
        return sb.toString();
    }

    private String availabilitiesToString() {
        if (availabilities.isEmpty()) return "None";
        return availabilities.toString();
    }

    @Override
    public String toString() {
        return "========== Employee ==========\n" +
                "Name              : " + userName + "\n" +
                "ID                : " + id + "\n" +
                "Branch            : " + branchID + "\n" +
                "Employment Type   : " + employmentType + "\n" +
                bankAccount + "\n" +
                "Roles:\n" + rolesToString() +
                "Shift Availabilities: " + availabilitiesToString() + "\n" +
                "Salary            : " + hourlySalary + "\n" +
                "Vacation Days     : " + vacationDays + "\n" +
                "Start Contract    : " + startContract + "\n" +
                "End Contract      : " + endContract + "\n" +
                "================================";
    }

    public static class BankAccount {
        private int bankNumber;
        private int bankBranchNumber;
        private int bankAccountNumber;

        public BankAccount(int bankNumber, int bankBranchNumber, int bankAccountNumber) {
            this.bankNumber = bankNumber;
            this.bankBranchNumber = bankBranchNumber;
            this.bankAccountNumber = bankAccountNumber;
        }

        public int getBankNumber() {
            return this.bankNumber;
        }

        public void setBankNumber(int bankNumber) {
            this.bankNumber = bankNumber;
        }

        public int getBankBranchNumber() {
            return this.bankBranchNumber;
        }

        public void setBankBranchNumber(int bankBranchNumber) {
            this.bankBranchNumber = bankBranchNumber;
        }

        public int getBankAccountNumber() {
            return this.bankAccountNumber;
        }

        public void setBankAccountNumber(int bankAccountNumber) {
            this.bankAccountNumber = bankAccountNumber;
        }

        @Override
        public String toString() {
            return "====== Bank Account ======\n" +
                    "Bank Number   : " + bankNumber + "\n" +
                    "Branch Number : " + bankBranchNumber + "\n" +
                    "Account Number: " + bankAccountNumber + "\n" +
                    "==========================";
        }
    }
}