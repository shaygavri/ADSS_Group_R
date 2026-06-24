package DTO;

import java.time.LocalDate;

public class EmployeeDTO {
    private int branchId;
    private String userName;
    private String password;
    private String employeeId;
    private int bankNumber;
    private int bankBranchNumber;
    private int bankAccountNumber;
    private int hourlySalary;
    private String employmentType;
    private int vacationDays;
    private LocalDate startContract;
    private LocalDate endContract;
    private String driverLicenseType;
    private boolean loggedIn;
    private String status;

    public EmployeeDTO(int branchId, String userName, String password, String employeeId,
                       int bankNumber, int bankBranchNumber, int bankAccountNumber,
                       int hourlySalary, String employmentType, int vacationDays,
                       LocalDate startContract, LocalDate endContract,
                       String driverLicenseType, boolean loggedIn, String status) {
        this.branchId = branchId;
        this.userName = userName;
        this.password = password;
        this.employeeId = employeeId;
        this.bankNumber = bankNumber;
        this.bankBranchNumber = bankBranchNumber;
        this.bankAccountNumber = bankAccountNumber;
        this.hourlySalary = hourlySalary;
        this.employmentType = employmentType;
        this.vacationDays = vacationDays;
        this.startContract = startContract;
        this.endContract = endContract;
        this.driverLicenseType = driverLicenseType;
        this.loggedIn = loggedIn;
        this.status = status;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public int getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(int bankNumber) {
        this.bankNumber = bankNumber;
    }

    public int getBankBranchNumber() {
        return bankBranchNumber;
    }

    public void setBankBranchNumber(int bankBranchNumber) {
        this.bankBranchNumber = bankBranchNumber;
    }

    public int getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(int bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public int getHourlySalary() {
        return hourlySalary;
    }

    public void setHourlySalary(int hourlySalary) {
        this.hourlySalary = hourlySalary;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public int getVacationDays() {
        return vacationDays;
    }

    public void setVacationDays(int vacationDays) {
        this.vacationDays = vacationDays;
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

    public String getDriverLicenseType() {
        return driverLicenseType;
    }

    public void setDriverLicenseType(String driverLicenseType) {
        this.driverLicenseType = driverLicenseType;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
