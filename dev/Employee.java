import java.time.LocalDate;
import java.util.ArrayList;

public class Employee {
    private String userName;
    private String password;
    private boolean isLoggedIn;
    private BankAccount bankAccount;
    private int branchID;
    private ArrayList<Role> roles;
    private int hourlySalary;
    private LocalDate startContract;
    private LocalDate endContract;

    public Employee(int BID, String userName, BankAccount bankAccount, int hourlySalary, String enterPassword) {
        this.branchID = BID;
        this.userName = userName;
        this.password = enterPassword;
        this.bankAccount = bankAccount;
        this.roles = new ArrayList<>();
        this.hourlySalary = hourlySalary;
        this.startContract = LocalDate.now();
        this.endContract = this.startContract.plusYears(1);
        this.isLoggedIn = false;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
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

    @Override
    public String toString() {
        return "Employee: " + userName +
                ", Branch: " + branchID +
                ", Roles: " + roles +
                ", Salary: " + hourlySalary +
                ", End Contract: " + endContract;
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
            return "Bank: " + bankNumber + ", Branch: " + bankBranchNumber + ", Account: " + bankAccountNumber;
        }
    }
}