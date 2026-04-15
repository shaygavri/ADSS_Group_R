package PresentationLayer;
import ServiceLayer.Service;
import java.util.Scanner;

public class Main {
    private static final Service service = new Service();

    public static void main(String[] args) {
        boolean system_on = true;
        Scanner scanner = new Scanner(System.in);
        int user_input;

        while (system_on) {
            System.out.print("Hello, please enter your role:\n");
            System.out.print("1.Employee\n");
            System.out.print("2.HR\n");
            System.out.print("3.Exit system\n");

            if (scanner.hasNextInt()) {
                user_input = scanner.nextInt();
                scanner.nextLine();

                switch (user_input) {
                    case 1 -> handleEmployeeLogin(scanner);
                    case 2 -> handleHRLogin(scanner);
                    case 3 -> system_on = false;
                    default -> System.out.println("Please enter a valid option (1, 2 or 3).");
                }
            } else {
                System.out.println("Please enter a valid option (1, 2 or 3).");
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
        System.out.println("HR menu is not implemented yet");
    }
}