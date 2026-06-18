import java.time.LocalDate;

public class DataInitializer {
    public static void initialize(DomainController domainController) {
        addCategories(domainController);
        addProducts(domainController);
        addSales(domainController);
        addPeriodicOrderRules(domainController);

        domainController.applyAllSalesToAllProducts();
    }

    private static void addCategories(DomainController domainController) {
        domainController.addCategory("Dairy", "Milk", "1L");
        domainController.addCategory("Dairy", "Cheese", "250g");
        domainController.addCategory("Dairy", "Yogurt", "150g");
        domainController.addCategory("Snacks", "Salty", "80g");
        domainController.addCategory("Snacks", "Salty", "70g");
        domainController.addCategory("Dry Goods", "Pasta", "500g");
        domainController.addCategory("Dry Goods", "Sauces", "500g");
    }

    private static void addProducts(DomainController domainController) {
        domainController.addProductBatch("Milk 3% 1L", "Tnuva", "Dairy", "Milk", "1L", 2, 1, 5, 2, 4.2, LocalDate.now().plusDays(10), 0, "", "A", "1");
        domainController.addProductBatch("Cottage Cheese 5%", "Tnuva", "Dairy", "Cheese", "250g", 20, 50, 4, 2, 3.5, LocalDate.now().plusDays(7), 1, "", "A", "2");
        domainController.addProductBatch("Chocolate Milk", "Strauss", "Dairy", "Milk", "1L", 15, 60, 3, 3, 5.0, LocalDate.now().plusDays(12), 0, "", "A", "11");
        domainController.addProductBatch("Greek Yogurt", "Strauss", "Dairy", "Yogurt", "150g", 40, 80, 4, 3, 2.8, LocalDate.now().plusDays(14), 0, "", "A", "3");
        domainController.addProductBatch("Bamba Classic", "Osem", "Snacks", "Salty", "80g", 100, 500, 10, 5, 1.2, LocalDate.now().plusMonths(6), 5, "", "B", "44");
        domainController.addProductBatch("Bisli Grill", "Osem", "Snacks", "Salty", "70g", 80, 400, 8, 5, 1.2, LocalDate.now().plusMonths(6), 0, "", "B", "49");
        domainController.addProductBatch("Spaghetti No. 8", "Osem", "Dry Goods", "Pasta", "500g", 50, 250, 6, 7, 2.5, LocalDate.now().plusYears(1), 0, "", "C", "2");
        domainController.addProductBatch("Hellmanns Mayo", "Unilever", "Dry Goods", "Sauces", "500g", 15, 45, 5, 7, 11.0, LocalDate.now().plusMonths(12), 0, "", "C", "3");
    }

    private static void addSales(DomainController domainController) {
        domainController.addSale("SALE_DAIRY", "Dairy", "Milk", 10.0, LocalDate.now().minusDays(1), LocalDate.now().plusDays(7));
        domainController.addSale("SALE_SNACKS", "Snacks", "Salty", 20.0, LocalDate.now().minusDays(1), LocalDate.now().plusWeeks(2));
        domainController.addSale("SALE_PASTA", "Dry Goods", "Pasta", 15.0, LocalDate.now(), LocalDate.now().plusMonths(1));
        domainController.addSale("SALE_CHEESE", "Dairy", "Cheese", 5.0, LocalDate.now().minusDays(2), LocalDate.now().plusDays(5));
    }

    private static void addPeriodicOrderRules(DomainController domainController) {
        int tomorrowDayOfMonth = LocalDate.now().plusDays(1).getDayOfMonth();
        Product milk = domainController.findProductByDetails("Milk 3% 1L", "Tnuva", "Dairy", "Milk", "1L");
        if (milk != null) {
            domainController.addPeriodicOrderRule(milk.getProductID(), 30, tomorrowDayOfMonth);}
        Product cottage = domainController.findProductByDetails("Cottage Cheese 5%", "Tnuva", "Dairy", "Cheese", "250g");
        if (cottage != null) {
            domainController.addPeriodicOrderRule(cottage.getProductID(), 25, 10);}
        Product bamba = domainController.findProductByDetails("Bamba Classic", "Osem", "Snacks", "Salty", "80g");
        if (bamba != null) {
            domainController.addPeriodicOrderRule(bamba.getProductID(), 50, 15);}
    }
}