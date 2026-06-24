package PresentationLayer;
import DataAccessLayer.DatabaseConfig;
import ServiceLayer.Service;
import java.util.Scanner;
import DomainLayer.EmployeeController;
import DomainLayer.ShiftController;

public class MainEmployees {
    private static final Service service = new Service();

    public static void main(String[] args) {
        EmployeeController.getInstance().connectToDatabase(); //load saved data and turn on persistence
        ShiftController.getInstance().connectToDatabase();   //load shift state and turn on persistence
        DatabaseConfig.seed();                               //seed initial data if the DB is empty

        boolean system_on = true;
        Scanner scanner = new Scanner(System.in);
        int user_input;

        while (system_on) {
            System.out.print("Hello, please enter your role:\n");
            System.out.print("1.Employee\n");
            System.out.print("2.HR\n");
            System.out.print("3.Delivery\n");
            System.out.print("4.Exit system\n");

            if (scanner.hasNextInt()) {
                user_input = scanner.nextInt();
                scanner.nextLine();

                switch (user_input) {
                    case 1 -> handleEmployeeLogin(scanner);
                    case 2 -> handleHRLogin(scanner);
                    case 3 -> new Menu_Delivery().printMenu();
                    case 4 -> system_on = false;
                    default -> System.out.println("Please enter a valid option (1, 2, 3 or 4).");
                }
            } else {
                System.out.println("Please enter a valid option (1, 2, 3 or 4).");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    public static void handleEmployeeLogin(Scanner scanner) {
        String user_name, password;
        Menu_User system_user = new Menu_User();
        system_user.service = service;

        System.out.print("Enter username: ");
        user_name = scanner.nextLine();

        System.out.print("Enter password: ");
        password = scanner.nextLine();

        if (service.login(user_name, password)) {
            system_user.userName = user_name;
            system_user.printMenu();
        }
    }

    public static void handleHRLogin(Scanner scanner) {
        String user_name, password;
        Menu_HR system_hr = new Menu_HR();
        system_hr.service = service;

        System.out.print("Enter username: ");
        user_name = scanner.nextLine();

        System.out.print("Enter password: ");
        password = scanner.nextLine();

        if (service.login(user_name, password)) {
            if (!service.isHRManager(user_name)) {
                System.out.println("this employee is not an HR manager");
                service.logout(user_name);
                return;
            }

            system_hr.userName = user_name;
            system_hr.printMenu();
        }
    }
}
