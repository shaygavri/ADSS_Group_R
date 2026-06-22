package ServiceLayer;

public class Service {
    private final UserService userService;
    private final ShiftService shiftService;
    private final TransportationIntegrationService transportationIntegrationService;

    public Service() {
        this.userService = new UserService();
        this.shiftService = new ShiftService();
        this.transportationIntegrationService = new TransportationIntegrationService();
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

    public void showBranches() {
        userService.showBranches();
    }

    public boolean addBranch(String branchId) {
        return userService.addBranch(branchId);
    }

    public boolean removeBranch(String branchId) {
        return userService.removeBranch(branchId);
    }

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
                bankAccountNumber);
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

    public boolean publishNextWeekRequirements(String deadlineOption, String customDate) {
        return shiftService.publishNextWeekRequirements(deadlineOption, customDate);
    }

    public boolean setAsCurrentWeek() {
        return shiftService.setAsCurrentWeek();
    }

    public boolean changeShiftRequirement(String branchId, String date, String shiftType,
                                          String roleId, String amount) {
        return shiftService.changeShiftRequirement(branchId, date, shiftType, roleId, amount);
    }

    public boolean markHolidayDay(String branchId, String shiftNumber, String week) {
        return shiftService.markHolidayDay(branchId, shiftNumber, week);
    }

    public boolean changeShiftRequirement(String branchId, String date, String shiftType,
                                          String roleId, String amount, String week) {
        return shiftService.changeShiftRequirement(branchId, date, shiftType, roleId, amount, week);
    }

    public boolean changeShiftRequirementByShiftNumber(String branchId, String shiftNumber,
                                                       String roleId, String amount, String week) {
        return shiftService.changeShiftRequirementByShiftNumber(branchId, shiftNumber, roleId, amount, week);
    }

    public void showAvailableEmployeesForShiftByRole(String branchId, String date,
                                                     String shiftType, String roleId) {
        shiftService.showAvailableEmployeesForShiftByRole(branchId, date, shiftType, roleId);
    }

    public void showAvailableEmployeesForShiftByRole(String branchId, String shiftNumber, String roleId) {
        shiftService.showAvailableEmployeesForShiftByRole(branchId, shiftNumber, roleId);
    }

    public boolean assignEmployee(String employeeUserName, String branchId, String date,
                                  String shiftType, String roleId) {
        return shiftService.assignEmployee(employeeUserName, branchId, date, shiftType, roleId);
    }

    public boolean assignEmployeeByShiftNumber(String employeeUserName, String branchId,
                                               String shiftNumber, String roleId) {
        return shiftService.assignEmployeeByShiftNumber(employeeUserName, branchId, shiftNumber, roleId);
    }

    public boolean removeEmployeeFromShiftByShiftNumber(String employeeUserName, String branchId,
                                                        String shiftNumber) {
        return shiftService.removeEmployeeFromShiftByShiftNumber(employeeUserName, branchId, shiftNumber);
    }

    public void showNextWeekStatus() {
        shiftService.showNextWeekStatus();
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

    // ADDED: shows requirements for a specific shift in next week by default
    public void showShiftRequirements(String branchId, String date, String shiftType) {
        shiftService.showShiftRequirements(branchId, date, shiftType);
    }

    // ADDED: shows requirements for a specific shift in current or next week
    public void showShiftRequirements(String branchId, String date, String shiftType, String week) {
        shiftService.showShiftRequirements(branchId, date, shiftType, week);
    }

    public void showShiftRequirementsByShiftNumber(String branchId, String shiftNumber, String week) {
        shiftService.showShiftRequirementsByShiftNumber(branchId, shiftNumber, week);
    }

    // ADDED: shows all roles and number of employees per role
    public void showAllRoles() {
        userService.showAllRoles();
    }

    public void showRoleOptions() {
        userService.showRoleOptions();
    }

    public boolean registerEmployeeAsDriver(String userName, String licenseType) {
        return transportationIntegrationService.registerEmployeeAsDriver(userName, licenseType);
    }

    public boolean addTransportDelivery(String deliveryId, String branchId, String date,
                                        String shiftType, String licenseType, String destination) {
        return transportationIntegrationService.addTransportDelivery(
                deliveryId, branchId, date, shiftType, licenseType, destination
        );
    }

    public boolean assignDriverToDelivery(String userName, String deliveryId) {
        return transportationIntegrationService.assignDriverToDelivery(userName, deliveryId);
    }

    public void showDeliveryIntegrationStatus(String deliveryId) {
        transportationIntegrationService.showDeliveryIntegrationStatus(deliveryId);
    }
}
