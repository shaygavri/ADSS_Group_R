/**
 * Entry point for the Inventory Management System.
 * Initializes the Domain, Service, and Presentation layers to launch the application.
 */
public class Main {

    public static void main(String[] args) {

        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.connect();
        databaseManager.createTables();
        CategoryDAO categoryDAO = new JdbcCategoryDAO(databaseManager);
        ProductDAO productDAO = new JdbcProductDAO(databaseManager);
        SaleDAO saleDAO = new JdbcSaleDAO(databaseManager);
        OrderDAO orderDAO = new JdbcOrderDAO(databaseManager);
        OrderItemDAO orderItemDAO = new JdbcOrderItemDAO(databaseManager);
        PeriodicOrderRuleDAO periodicOrderRuleDAO = new JdbcPeriodicOrderRuleDAO(databaseManager);
        CategoryRepository categoryRepository = new CategoryRepository(categoryDAO);
        ProductRepository productRepository = new ProductRepository(productDAO, categoryRepository);
        SaleRepository saleRepository = new SaleRepository(saleDAO);
        OrderRepository orderRepository = new OrderRepository(orderDAO, orderItemDAO, productRepository);
        PeriodicOrderRuleRepository periodicOrderRuleRepository = new PeriodicOrderRuleRepository(periodicOrderRuleDAO);
        DomainController domainController = new DomainController(categoryRepository, productRepository, saleRepository, orderRepository, periodicOrderRuleRepository);
        ServiceController serviceController = new ServiceController(domainController);
        InventoryUI inventoryUI = new InventoryUI(serviceController);
        AutomaticOrderThread automaticOrderThread = new AutomaticOrderThread(serviceController, 10000);
        automaticOrderThread.start();
        inventoryUI.start();
        automaticOrderThread.stopRunning();
        databaseManager.closeConnection();
    }
}
