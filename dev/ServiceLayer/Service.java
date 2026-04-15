package ServiceLayer;

public class Service {
    private final UserService userService;
    private final ShiftService shiftService;

    public Service() {
        this.userService = new UserService();
        this.shiftService = new ShiftService();
    }

    // ======================== REGULAR USER ========================

    public boolean login(String userName, String password) {
        return userService.login(userName, password);
    }

    public boolean changePassword(String userName, String newPassword) {
        return userService.changePassword(userName, newPassword);
    }

    public boolean pickAvailability(String userName, String input) {
        return userService.pickAvailability(userName, input);
    }

    public void showMyAvailability(String userName) {
        userService.showMyAvailability(userName);
    }

    public boolean changeAvailability(String userName, String oldShift, String newShift) {
        return userService.changeAvailability(userName, oldShift, newShift);
    }

    public boolean updateBankAccount(String userName, String bank, String branch, String account) {
        return userService.updateBankAccount(userName, bank, branch, account);
    }

    public void showMyPersonalDetails(String userName) {
        userService.showMyPersonalDetails(userName);
    }

    public boolean logout(String userName) {
        return userService.logout(userName);
    }

    public boolean isHRManager(String userName) {
        return userService.isHRManager(userName);
    }


    // ======================== HR ========================


    public boolean createRole(String roleId, String roleName, String description) {
        return userService.createRole(roleId, roleName, description);
    }

    public boolean addRoleToEmployee(String employeeUserName, String roleId) {
        return userService.addRoleToEmployee(employeeUserName, roleId);
    }

    public boolean hireEmployee(String employeeUserName, String employeeId, String password,
                                String branchId, String hourlySalary, String employmentType,
                                String bankNumber, String bankBranchNumber, String bankAccountNumber) {
        return userService.hireEmployee(
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
    }

    public boolean fireEmployee(String employeeUserName) {
        return userService.fireEmployee(employeeUserName);
    }

    public boolean changeEmployeeSalary(String employeeUserName, String newSalary) {
        return userService.changeEmployeeSalary(employeeUserName, newSalary);
    }

    public void showAllEmployeesAndRoles() {
        userService.showAllEmployeesAndRoles();
    }

    public boolean publishNextWeek() {
        return shiftService.publishNextWeek();
    }

    public boolean setAsCurrentWeek() {
        return shiftService.setAsCurrentWeek();
    }

    public boolean changeShiftRequirement(String branchId, String date, String shiftType,
                                          String roleId, String amount) {
        return shiftService.changeShiftRequirement(branchId, date, shiftType, roleId, amount);
    }

    public void showAvailableEmployeesForShiftByRole(String branchId, String date,
                                                     String shiftType, String roleId) {
        shiftService.showAvailableEmployeesForShiftByRole(branchId, date, shiftType, roleId);
    }

    public boolean assignEmployee(String employeeUserName, String branchId, String date,
                                  String shiftType, String roleId) {
        return shiftService.assignEmployee(employeeUserName, branchId, date, shiftType, roleId);
    }

    public void showCurrentWeekShift() {
        shiftService.showCurrentWeekShift();
    }

    public void showNextWeekShift() {
        shiftService.showNextWeekShift();
    }

    public void showAllShiftsHistory() {
        shiftService.showAllShiftsHistory();
    }

    public void showShiftsHistoryByBranch(String branchId) {
        shiftService.showShiftsHistoryByBranch(branchId);
    }
}
