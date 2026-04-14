package PresentationLayer;
import java.util.Scanner;

public class Main {
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
                scanner.nextLine();  // Clear the buffer

                switch (user_input) {
                    case 1 -> handleEmployeeLogin(scanner);
                    case 2 -> handleHRLogin(scanner);
                    case 3 -> system_on = false;
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
        System_User system_user = new System_User();
        system_user.service = service;

        System.out.print("Enter username: ");
        user_name = scanner.next();
        scanner.nextLine();
        System.out.print("Enter password: ");
        password = scanner.next();
        scanner.nextLine();
    }

    public static void handleHRLogin(Scanner scanner) {
        String user_name, password;
        System_HR system_hr = new System_HR();
        system_hr.service = service;

        System.out.print("Enter username: ");
        user_name = scanner.next();
        scanner.nextLine();
        System.out.print("Enter password: ");
        password = scanner.next();
        scanner.nextLine();
    }
}
