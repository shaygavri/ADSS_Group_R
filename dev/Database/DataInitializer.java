import java.time.LocalDate;

public class DataInitializer {
    public static void initialize(DomainController domainController) {
        domainController.addNewSup("Tnuva", "S101", 5.0);
        domainController.addNewSup("Strauss", "S102", 10.0);
        domainController.addNewSup("Osem", "S103", 8.5);
        domainController.addNewSup("Unilever", "S104", 12.0);
        domainController.addProductBatch("Milk 3% 1L", "Tnuva", "Dairy", "Milk", "1L",
                30, 100, 5, 2, 4.2, LocalDate.now().plusDays(10), 0, "S101", "A", "1");
        // Categories
        domainController.addCategory("Dairy", "Milk", "1L");
        domainController.addCategory("Dairy", "Cheese", "250g");
        domainController.addCategory("Dairy", "Yogurt", "150g");
        domainController.addCategory("Snacks", "Salty", "80g");
        domainController.addCategory("Snacks", "Salty", "70g");
        domainController.addCategory("Dry Goods", "Pasta", "500g");
        domainController.addCategory("Dry Goods", "Sauces", "500g");
        domainController.addProductBatch("Cottage Cheese 5%", "Tnuva", "Dairy", "Cheese", "250g",
                20, 50, 4, 2, 3.5, LocalDate.now().plusDays(7), 1, "S101", "A", "2");
        domainController.addProductBatch("Chocolate Milk", "Strauss", "Dairy", "Milk", "1L",
                15, 60, 3, 3, 5.0, LocalDate.now().plusDays(12), 0, "S102", "A", "11");
        domainController.addProductBatch("Greek Yogurt", "Strauss", "Dairy", "Yogurt", "150g",
                40, 80, 4, 3, 2.8, LocalDate.now().plusDays(14), 0, "S102", "A", "3");
        domainController.addProductBatch("Bamba Classic", "Osem", "Snacks", "Salty", "80g",
                100, 500, 10, 5, 1.2, LocalDate.now().plusMonths(6), 5, "S103", "B", "44");
        domainController.addProductBatch("Bisli Grill", "Osem", "Snacks", "Salty", "70g",
                80, 400, 8, 5, 1.2, LocalDate.now().plusMonths(6), 0, "S103", "B", "49");
        domainController.addProductBatch("Spaghetti No. 8", "Osem", "Dry Goods", "Pasta", "500g",
                50, 250, 6, 7, 2.5, LocalDate.now().plusYears(1), 0, "S103", "C", "2");
        domainController.addProductBatch("Hellmanns Mayo", "Unilever", "Dry Goods", "Sauces", "500g",
                15, 45, 5, 7, 11.0, LocalDate.now().plusMonths(12), 0, "S104", "C", "3");
        domainController.addSale("SALE_DAIRY", "Milk", "Dairy", 10.0, LocalDate.now().minusDays(1), LocalDate.now().plusDays(7));
        domainController.addSale("SALE_SNACKS", "Salty", "Snacks", 20.0, LocalDate.now().minusDays(1), LocalDate.now().plusWeeks(2));
        domainController.addSale("SALE_PASTA", "Pasta", "Dry Goods", 15.0, LocalDate.now(), LocalDate.now().plusMonths(1));
        domainController.addSale("SALE_CHEESE", "Cheese", "Dairy", 5.0, LocalDate.now().minusDays(2), LocalDate.now().plusDays(5));
    }
}
