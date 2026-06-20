import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DomainControllerTest {

    private DomainController domain;

    private DomainController createDomainControllerForTests() {
        DatabaseManager databaseManager = new DatabaseManager("jdbc:sqlite::memory:");
        databaseManager.connect();
        databaseManager.createTables();

        CategoryDAO categoryDAO = new JdbcCategoryDAO(databaseManager);
        ProductDAO productDAO = new JdbcProductDAO(databaseManager);
        SaleDAO saleDAO = new JdbcSaleDAO(databaseManager);
        OrderDAO orderDAO = new JdbcOrderDAO(databaseManager);
        OrderItemDAO orderItemDAO = new JdbcOrderItemDAO(databaseManager);
        PeriodicOrderRuleDAO periodicOrderRuleDAO =
                new JdbcPeriodicOrderRuleDAO(databaseManager);

        CategoryRepository categoryRepository =
                new CategoryRepository(categoryDAO);

        ProductRepository productRepository =
                new ProductRepository(productDAO, categoryRepository);

        SaleRepository saleRepository =
                new SaleRepository(saleDAO);

        OrderRepository orderRepository =
                new OrderRepository(orderDAO, orderItemDAO, productRepository);

        PeriodicOrderRuleRepository periodicOrderRuleRepository =
                new PeriodicOrderRuleRepository(periodicOrderRuleDAO);

        return new DomainController(
                categoryRepository,
                productRepository,
                saleRepository,
                orderRepository,
                periodicOrderRuleRepository
        );
    }
    @BeforeEach
    void setUp() {
        new java.io.File("inventory.db").delete();
        domain = createDomainControllerForTests();
    }

    @Test
    void testAddCategory() {
        boolean added = domain.addCategory("Dairy", "Milk", "1L");

        assertTrue(added);

        Category category = domain.findCategory("Dairy", "Milk", "1L");

        assertNotNull(category);
        assertEquals("Dairy", category.getCategoryName());
        assertEquals("Milk", category.getSubCategoryName());
        assertEquals("1L", category.getSubSubCategoryName());
    }

    @Test
    void testAddDuplicateCategory() {
        domain.addCategory("Dairy", "Milk", "1L");

        boolean addedAgain = domain.addCategory("Dairy", "Milk", "1L");

        assertFalse(addedAgain, "Should not allow an identical category classification");
    }

    @Test
    void testFindCategoryCaseInsensitive() {
        domain.addCategory("Dairy", "Milk", "1L");

        Category category = domain.findCategory("dairy", "milk", "1l");

        assertNotNull(category);
    }

    @Test
    void testAddProductBatchAndIdGeneration() {
        domain.addCategory("Dairy", "Milk", "1L");

        boolean added = domain.addProductBatch(
                "Milk",
                "Tnuva",
                "Dairy",
                "Milk",
                "1L",
                10,
                10,
                5,
                1,
                5.0,
                LocalDate.now().plusDays(10),
                0,
                "S1",
                "A1",
                "S1"
        );

        assertTrue(added);

        List<Product> products = domain.getProductByCat("Dairy");

        assertFalse(products.isEmpty(), "Product list should not be empty");

        Product product = products.get(0);

        assertNotNull(product, "Product should be found");
        assertTrue(product.getProductID().startsWith("P"), "ID should start with P");
        assertEquals("Dairy", product.getCategory());
        assertEquals("Milk", product.getSub_cat_name());
        assertEquals("1L", product.getSize());
    }

    @Test
    void testAddProductDoesNotDependOnSupplier() {
        domain.addCategory("Dairy", "Milk", "1L");

        boolean added = domain.addProductBatch(
                "Milk",
                "Tnuva",
                "Dairy",
                "Milk",
                "1L",
                10,
                10,
                5,
                1,
                5.0,
                LocalDate.now().plusDays(10),
                0,
                "NON_EXISTENT_SUPPLIER",
                "A1",
                "S1"
        );

        assertTrue(added, "Product should not depend on supplier anymore");
    }

    @Test
    void testAddProductWithMissingCategory() {
        boolean added = domain.addProductBatch(
                "Milk",
                "Tnuva",
                "Dairy",
                "Milk",
                "1L",
                10,
                10,
                5,
                1,
                5.0,
                LocalDate.now().plusDays(10),
                0,
                "S1",
                "A1",
                "S1"
        );
        assertTrue(added, "Product should be added because missing category is created automatically");
        Category category = domain.findCategory("Dairy", "Milk", "1L");
        assertNotNull(category, "Missing category should be created automatically");
    }
    @Test
    void testUpdateQuantities() {
        domain.addCategory("D", "M", "1L");

        boolean added = domain.addProductBatch(
                "Milk",
                "T",
                "D",
                "M",
                "1L",
                10,
                10,
                5,
                1,
                5.0,
                LocalDate.now().plusDays(10),
                0,
                "S1",
                "A1",
                "S1"
        );

        assertTrue(added);

        Product product = domain.getProductByCat("D").get(0);
        String realId = product.getProductID();

        boolean updated = domain.updateQuantities(realId, 20, 30);

        assertTrue(updated);
        assertEquals(20, domain.getProductByID(realId).getShopQuantity());
        assertEquals(30, domain.getProductByID(realId).getWarehouseQuantity());
        assertEquals(50, domain.getProductByID(realId).getTotalQuantity());
    }

    @Test
    void testUpdateStatus() {
        domain.addCategory("D", "M", "1L");

        boolean added = domain.addProductBatch("Milk", "T", "D",
                "M", "1L", 10,
                10, 5, 1,
                5.0, LocalDate.now().plusDays(10), 0, "S1", "A1", "S1");
        assertTrue(added);
        Product product = domain.getProductByCat("D").get(0);
        String realId = product.getProductID();
        boolean updated = domain.updateNewStatus(realId, false);
        assertTrue(updated);
        assertFalse(domain.getProductByID(realId).getIsActive());}

    @Test
    void testGetProductByCategoryCaseInsensitive() {
        domain.addCategory("Dairy", "M", "1L");

        boolean added = domain.addProductBatch(
                "Milk",
                "T",
                "Dairy",
                "M",
                "1L",
                10,
                10,
                5,
                1,
                5.0,
                LocalDate.now().plusDays(10),
                0,
                "S1",
                "A1",
                "S1"
        );

        assertTrue(added);

        List<Product> results = domain.getProductByCat("dairy");

        assertEquals(1, results.size());
    }

    @Test
    void testSystemAlerts() {
        domain.addCategory("D", "M", "1");

        boolean added = domain.addProductBatch(
                "LowStock",
                "T",
                "D",
                "M",
                "1",
                2,
                2,
                5,
                1,
                5.0,
                LocalDate.now().plusDays(10),
                0,
                "S1",
                "A1",
                "S1"
        );

        assertTrue(added);

        List<Product> alerts = domain.getProductsForSystemAlerts();

        assertFalse(alerts.isEmpty());
        assertEquals("LowStock", alerts.get(0).getProductName());
    }

    @Test
    void testAddSaleAppliesToProducts() {
        domain.addCategory("Dairy", "Milk", "1L");

        boolean productAdded = domain.addProductBatch(
                "Milk",
                "T",
                "Dairy",
                "Milk",
                "1L",
                10,
                10,
                5,
                1,
                100.0,
                LocalDate.now().plusDays(10),
                0,
                "S1",
                "A1",
                "S1"
        );

        assertTrue(productAdded);

        boolean saleAdded = domain.addSale(
                "SALE100",
                "Dairy",
                "Milk",
                20.0,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(1)
        );

        assertTrue(saleAdded);
        assertEquals(1, domain.displayAllStoreSales().size());
    }

    @Test
    void testMockSupplierSystemInitializesSuppliers() {
        MockSupplierSystem mockSupplierSystem = new MockSupplierSystem();

        List<Supplier> suppliers = mockSupplierSystem.getSuppliers();

        assertNotNull(suppliers);
        assertFalse(suppliers.isEmpty());
    }

    @Test
    void testMockSupplierSystemAddSupplier() {
        MockSupplierSystem mockSupplierSystem = new MockSupplierSystem();
        Supplier supplier = new Supplier("Osem", "S999");
        boolean added = mockSupplierSystem.addSupplier(supplier);
        assertTrue(added);
        assertNotNull(mockSupplierSystem.findSupplierById("S999"));
        assertEquals("Osem", mockSupplierSystem.findSupplierById("S999").getSupplierName());
    }

    @Test
    void testMockSupplierSystemDoesNotAllowDuplicateSupplierId() {
        MockSupplierSystem mockSupplierSystem = new MockSupplierSystem();
        Supplier supplier1 = new Supplier("Osem", "S999");
        Supplier supplier2 = new Supplier("Tnuva", "S999");
        boolean firstAdded = mockSupplierSystem.addSupplier(supplier1);
        boolean secondAdded = mockSupplierSystem.addSupplier(supplier2);
        assertTrue(firstAdded);
        assertFalse(secondAdded);
    }
    @Test
    void testMockSupplierSystemReturnsDummyBestOffer() {
        MockSupplierSystem mockSupplierSystem = new MockSupplierSystem();
        SupplierOffer offer = mockSupplierSystem.getBestOffer("P1", 50);
        assertNotNull(offer);
        assertEquals("P1", offer.getProductId());
        assertEquals(50, offer.getQuantity());
        assertTrue(offer.getTotalPrice() > 0);
    }
}