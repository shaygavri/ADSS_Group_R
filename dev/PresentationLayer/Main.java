/**
 * Entry point for the Inventory Management System.
 * Initializes the Domain, Service, and Presentation layers to launch the application.
 */
public class Main {
        public static void main(String[] args) {
            DomainController domainController = new DomainController();
            ServiceController serviceController = new ServiceController(domainController);
            InventoryUI inventoryUI = new InventoryUI(serviceController);
            inventoryUI.start();
        }
    }
