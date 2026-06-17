import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DomainControllerTest {

    private DomainController domain;

    @BeforeEach
    void setUp() {
        domain = new DomainController();
    }

    @Test
    void testAddNewSup() {
        boolean added = domain.addNewSup("Osem", "S123", 10.0);
        assertTrue(added);
        assertEquals(1, domain.displayAllSup().size());
        assertEquals("Osem", domain.findSupplierById("S123").getSupplierName());
    }

    @Test
    void testAddDuplicateSupplier() {
        domain.addNewSup("Osem", "S123", 10.0);
        boolean addedAgain = domain.addNewSup("Tnuva", "S123", 5.0);
        assertFalse(addedAgain, "Should not allow duplicate supplier IDs");
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
        domain.addNewSup("Tnuva", "S1", 10.0);
        domain.addCategory("Dairy", "Milk", "1L");

        boolean added = domain.addProductBatch("Milk", "Tnuva", "Dairy", "Milk", "1L", 10, 10, 5, 1, 5.0, LocalDate.now().plusDays(10), 0, "S1", "A1", "S1");

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
    void testAddProductWithMissingSupplier() {
        domain.addCategory("Dairy", "Milk", "1L");

        boolean added = domain.addProductBatch("Milk", "Tnuva", "Dairy", "Milk", "1L", 10, 10, 5, 1, 5.0, LocalDate.now(), 0, "NON_EXISTENT", "A1", "S1");

        assertFalse(added, "Should return false if supplier is not found");
    }

    @Test
    void testAddProductWithMissingCategory() {
        domain.addNewSup("Tnuva", "S1", 10.0);

        boolean added = domain.addProductBatch("Milk", "Tnuva", "Dairy", "Milk", "1L", 10, 10, 5, 1, 5.0, LocalDate.now(), 0, "S1", "A1", "S1");

        assertFalse(added, "Should return false if category is not found");
    }

    @Test
    void testUpdateQuantities() {
        domain.addNewSup("S", "S1", 0);
        domain.addCategory("D", "M", "1L");

        boolean added = domain.addProductBatch("Milk", "T", "D", "M", "1L", 10, 10, 5, 1, 5.0, LocalDate.now(), 0, "S1", "A1", "S1");

        assertTrue(added);

        Product product = domain.getProductByCat("D").get(0);
        String realId = product.getProductID();

        boolean updated = domain.updateQuantities(realId, 20, 30);

        assertTrue(updated);
        assertEquals(20, domain.getProductByID(realId).getShopQuantity());
        assertEquals(30, domain.getProductByID(realId).getWarehouseQuantity());
    }

    @Test
    void testUpdateStatus() {
        domain.addNewSup("S", "S1", 0);
        domain.addCategory("D", "M", "1L");

        boolean added = domain.addProductBatch("Milk", "T", "D", "M", "1L", 10, 10, 5, 1, 5.0, LocalDate.now(), 0, "S1", "A1", "S1");

        assertTrue(added);

        Product product = domain.getProductByCat("D").get(0);
        String realId = product.getProductID();

        domain.updateNewStatus(realId, false);

        assertFalse(domain.getProductByID(realId).getIsActive());
    }

    @Test
    void testGetProductByCategoryCaseInsensitive() {
        domain.addNewSup("S", "S1", 0);
        domain.addCategory("Dairy", "M", "1L");

        boolean added = domain.addProductBatch("Milk", "T", "Dairy", "M", "1L", 10, 10, 5, 1, 5.0, LocalDate.now(), 0, "S1", "A1", "S1");

        assertTrue(added);

        List<Product> results = domain.getProductByCat("dairy");

        assertEquals(1, results.size());
    }

    @Test
    void testSystemAlerts() {
        domain.addNewSup("S", "S1", 0);
        domain.addCategory("D", "M", "1");

        boolean added = domain.addProductBatch("LowStock", "T", "D", "M", "1", 2, 2, 5, 1, 5.0, LocalDate.now(), 0, "S1", "A1", "S1");

        assertTrue(added);

        List<Product> alerts = domain.getProductsForSystemAlerts();

        assertFalse(alerts.isEmpty());
        assertEquals("LowStock", alerts.get(0).getProductName());
    }

    @Test
    void testAddSaleAppliesToProducts() {
        domain.addNewSup("S", "S1", 0);
        domain.addCategory("Dairy", "Milk", "1L");

        boolean productAdded = domain.addProductBatch("Milk", "T", "Dairy", "Milk", "1L", 10, 10, 5, 1, 100.0, LocalDate.now(), 0, "S1", "A1", "S1");
        assertTrue(productAdded);
        domain.addSale("S100", "Dairy", "Milk", 20.0, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        assertEquals(1, domain.displayAllStoreSales().size());
    }

    @Test
    void testUpdateSupplierDiscount() {
        domain.addNewSup("Osem", "S123", 10.0);

        boolean updated = domain.updateSupDicountRate("S123", 15.5);

        assertTrue(updated);
        assertEquals(15.5, domain.findSupplierById("S123").getDiscountRate());
    }
}