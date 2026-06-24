import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("========================================");
        System.out.println("        Super-Lee Management System      ");
        System.out.println("========================================");
        System.out.println("1. Inventory / Suppliers System");
        System.out.println("2. HR / Transportation System");
        System.out.println("3. Exit");
        System.out.println("========================================");
        System.out.print("Please Enter Your Choice: ");

        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            MainInventory.main(new String[]{});
            return;
        }

        if (choice.equals("2")) {
            PresentationLayer.MainEmployees.main(new String[]{});
            return;
        }

        if (choice.equals("3")) {
            System.out.println("Goodbye.");
            return;
        }

        System.out.println("Wrong input, please run the system again.");
    }
}