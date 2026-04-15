package PresentationLayer;
import ServiceLayer.Service;
import java.util.Scanner;

// User menu - for HR manager
public class Menu_HR {

        public String in;
        public String userName;
        public Service service;

    public void printMenu() {
        System.out.println("\n========== HR MENU ==========");

        System.out.println("\n--- Branch Management ---");
        System.out.println("1. show branches IDs");
        System.out.println("2. add branch");
        System.out.println("3. remove branch");

        System.out.println("\n--- Employee and Role Management ---");
        System.out.println("4. create new role");
        System.out.println("5. add role to employee");
        System.out.println("6. hire employee");
        System.out.println("7. fire employee");
        System.out.println("8. change employee's hourly salary");
        System.out.println("9. show all employees and their roles");

        System.out.println("\n--- Shift and Scheduling Management ---");
        System.out.println("10. publish next week");
        System.out.println("11. set as current week");
        System.out.println("12. change shift requirement");
        System.out.println("13. show available employees for shift by role");
        System.out.println("14. assign employee");
        System.out.println("15. show current week shift");
        System.out.println("16. show next week shift");
        System.out.println("17. show all shifts history");
        System.out.println("18. show shifts history by branch");

        System.out.println("19. logout");

        getAnswer();
    }

        public void getAnswer(){
            Scanner scanner = new Scanner(System.in);
            int input = scanner.nextInt();
            scanner.nextLine();

            switch (input) {
                case 1 -> {
                    printMenu();
                }
                case 2 -> {
                    printMenu();
                }

                case 3 -> {
                    printMenu();
                }

                case 4 -> {
                    printMenu();
                }

                case 5 -> {
                    printMenu();
                }

                case 6 -> {
                    printMenu();
                }

                case 7 -> {
                    printMenu();
                }

                case 8 -> {
                    printMenu();
                }

                case 9 -> {
                    printMenu();
                }

                case 10 -> {
                    printMenu();
                }

                case 11 -> {
                    printMenu();
                }

                case 12 -> {
                    printMenu();
                }

                case 13 -> {
                    printMenu();
                }

                case 14 -> {
                    printMenu();
                }

                case 15 -> {
                    printMenu();
                }

                case 16 -> {
                    printMenu();
                }

                case 17 -> {
                    service.logout(userName);
                    printMenu();
                }

                default -> {
                    System.out.print("invalid input, please try again\n");
                    printMenu();
                }
            }
        }
}
