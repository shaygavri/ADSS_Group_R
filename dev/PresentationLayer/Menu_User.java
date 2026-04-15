package PresentationLayer;
import ServiceLayer.Service;
import java.util.Scanner;

public class Menu_User {
    public String in;
    public String userName;
    public Service service;

    public void printMenu(){
        System.out.println();
        System.out.println("choose an option:");
        System.out.println("1. change password");
        System.out.println("2. pick weekly availability");
        System.out.println("3. show my availabilities");
        System.out.println("4. change availability");
        System.out.println("5. logout");
        getAnswer();
    }

    public void getAnswer(){
        Scanner scanner = new Scanner(System.in);
        int input = scanner.nextInt();
        scanner.nextLine();
        switch (input) {

            case 1 -> {
                System.out.println("please enter new password");
                in = scanner.nextLine();
                service.changePassword(userName, in);
                printMenu();
            }

            case 2 -> {
                System.out.println("please enter your availability by choosing numbers between 0-13, make sure" +
                        " to have a space between each number");
                System.out.println("Note that each number represents a shift, for example 0 is sunday morning shift" +
                        " ,1 is sunday evening shift, 2 is monday morning and so on");
                in = scanner.nextLine();
                service.pickAvailability(userName, in);
                printMenu();
            }

            case 3 -> {
                service.showMyAvailability(userName);
                printMenu();
            }

            case 4 -> {
                System.out.println("please enter the current shift you want to replace (0-13)");
                String oldShift = scanner.nextLine();

                System.out.println("please enter the new shift (0-13)");
                String newShift = scanner.nextLine();

                service.changeAvailability(userName, oldShift, newShift);
                printMenu();
            }

            case 5 -> {
                service.logout(userName);
            }

            default -> {
                System.out.print("invalid input, please try again\n");
                printMenu();
            }
        }
    }
}
