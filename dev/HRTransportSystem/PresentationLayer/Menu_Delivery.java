package PresentationLayer;

import DomainLayer.Shift;
import TransportationIntegrationMock.MockDriverLicenseType;
import TransportationIntegrationMock.MockTransportController;
import TransportationIntegrationMock.MockTransportDelivery;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Menu_Delivery {

    public void printMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean menu_on = true;

        while (menu_on) {
            System.out.print("\nDelivery Management:\n");
            System.out.print("1.Add delivery\n");
            System.out.print("2.Remove delivery\n");
            System.out.print("3.Show all deliveries\n");
            System.out.print("4.Back\n");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> addDelivery(scanner);
                    case 2 -> removeDelivery(scanner);
                    case 3 -> showAllDeliveries();
                    case 4 -> menu_on = false;
                    default -> System.out.println("Please enter a valid option (1-4).");
                }
            } else {
                System.out.println("Please enter a valid option (1-4).");
                scanner.nextLine();
            }
        }
    }

    private void addDelivery(Scanner scanner) {
        System.out.print("Branch ID: ");
        int branchId;
        try {
            branchId = Integer.parseInt(scanner.nextLine().trim());
            if (branchId <= 0) {
                System.out.println("Branch ID must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid branch ID.");
            return;
        }

        System.out.print("Date (YYYY-MM-DD): ");
        LocalDate date;
        try {
            date = LocalDate.parse(scanner.nextLine().trim());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }

        System.out.print("Shift type (1=MORNING, 2=EVENING): ");
        Shift.ShiftType shiftType;
        if (scanner.hasNextInt()) {
            int typeChoice = scanner.nextInt();
            scanner.nextLine();
            if (typeChoice == 1) {
                shiftType = Shift.ShiftType.MORNING;
            } else if (typeChoice == 2) {
                shiftType = Shift.ShiftType.EVENING;
            } else {
                System.out.println("Invalid shift type.");
                return;
            }
        } else {
            System.out.println("Invalid shift type.");
            scanner.nextLine();
            return;
        }

        System.out.print("Required license type (1=B, 2=C1, 3=C, 4=CE): ");
        MockDriverLicenseType licenseType;
        if (scanner.hasNextInt()) {
            int licenseChoice = scanner.nextInt();
            scanner.nextLine();
            switch (licenseChoice) {
                case 1 -> licenseType = MockDriverLicenseType.B;
                case 2 -> licenseType = MockDriverLicenseType.C1;
                case 3 -> licenseType = MockDriverLicenseType.C;
                case 4 -> licenseType = MockDriverLicenseType.CE;
                default -> {
                    System.out.println("Invalid license type.");
                    return;
                }
            }
        } else {
            System.out.println("Invalid license type.");
            scanner.nextLine();
            return;
        }

        System.out.print("Destination: ");
        String destination = scanner.nextLine().trim();
        if (destination.isBlank()) {
            System.out.println("Destination cannot be empty.");
            return;
        }

        MockTransportController transport = MockTransportController.getInstance();
        int newId = 1;
        for (MockTransportDelivery d : transport.getAllDeliveries()) {
            if (d.getDeliveryId() >= newId) {
                newId = d.getDeliveryId() + 1;
            }
        }

        boolean added = transport.createDelivery(newId, branchId, date, shiftType, licenseType, destination);
        if (added) {
            System.out.println("Delivery #" + newId + " added successfully.");
        } else {
            System.out.println("Failed to add delivery.");
        }
    }

    private void removeDelivery(Scanner scanner) {
        System.out.print("Delivery ID to remove: ");
        int deliveryId;
        try {
            deliveryId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid delivery ID.");
            return;
        }

        boolean removed = MockTransportController.getInstance().removeDelivery(deliveryId);
        if (removed) {
            System.out.println("Delivery #" + deliveryId + " removed.");
        } else {
            System.out.println("Delivery #" + deliveryId + " not found.");
        }
    }

    private void showAllDeliveries() {
        java.util.ArrayList<MockTransportDelivery> deliveries = MockTransportController.getInstance().getAllDeliveries();
        if (deliveries.isEmpty()) {
            System.out.println("No deliveries.");
            return;
        }

        System.out.println("\n===== All Deliveries =====");
        for (MockTransportDelivery d : deliveries) {
            String driver = d.needsDriver() ? "unassigned" : "driver: " + d.getAssignedDriverId();
            System.out.println("Delivery #" + d.getDeliveryId()
                    + " | Branch: " + d.getBranchId()
                    + " | " + d.getDate() + " " + d.getShiftType()
                    + " | License: " + d.getRequiredLicenseType()
                    + " | Dest: " + d.getDestination()
                    + " | " + driver);
        }
        System.out.println("==========================");
    }
}
