package ServiceLayer;

public class Service {
    private final UserService userService;

    public Service() {
        this.userService = new UserService();
    }

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
}