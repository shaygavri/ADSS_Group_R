import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 * Service Layer for generating various inventory reports.
 * This class handles validation logic for dates and categories before
 * requesting data from the domain layer.
 */
public class ReportService {
    private DomainController domain;
    public ReportService(DomainController domain) {
        this.domain = domain;
    }

    /**
     * Generates a report of damaged products within a specific date range.
     * Validates that the end date is not before the start date and that
     * the start date is not in the future.
     * @param start The beginning of the period to report.
     * @param end The end of the period to report.
     * @return A list of products with recorded damage in the given period,
     * or null if date validation fails.
     */
    public List<Product> getDamagedProductsReport(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            System.out.println("End date cannot be earlier than start date.");
            return null;
        }
        if (start.isAfter(LocalDate.now())) {
            System.out.println("Start date cannot be in the future.");
            return null;
        }
        return domain.displayPeriodicReportByDate(start, end);

    }
    /**
     * Fetches a specific product's details for individual reporting.
     * @param id The unique identifier of the product.
     * @return The Product object if found, or null if it does not exist.
     */
    public Product getProductForReport(String id) {
        Product p = domain.getProductByID(id);
        if (p == null) {
            System.out.println("Product with ID " + id + " does not exist.");
        }
        return p;
    }
    /**
     * Retrieves all products belonging to a specific sub-category for reporting.
     * @param subCatName The name of the sub-category.
     * @return A list of matching products from the domain layer.
     */
    public List<Product> getSubCategoryReport(String subCatName) {
        if (subCatName == null || subCatName.isEmpty()) {
            System.out.println("SubCategory name cannot be empty.");
        }
        return domain.getProductBySubCat(subCatName);

    }
    /**
     * Retrieves all products belonging to a specific sub-sub-category (size).
     * @param size The product size representing the sub-sub-category.
     * @return A list of matching products, or null if the size is invalid.
     */
    public List<Product> getSubSubCategoryReport(String size) {
        if (size == null || size.trim().isEmpty()) {
            System.out.println("Sub-Sub-Category (size) cannot be empty.");
            return null;}
        return domain.getProductBySubSubCat(size.trim());
    }
    /**
     * Retrieves all products belonging to a specific category for reporting.
     * @param category The name of the category.
     * @return A list of matching products from the domain layer.
     */
    public List<Product> getCategoryReport(String category) {
        if (category == null || category.isEmpty()) {
            System.out.println("Category name cannot be empty.");
        }
        return domain.getProductByCat(category);
    }

    /**
     * Generates a list of products that have reached low-stock levels.
     * This method is used for automatic system notifications regarding restocking.
     * @return A list of products whose total quantity is below their minimum limit.
     */
    public List<Product> getSystemAlerts() {
        return domain.getProductsForSystemAlerts();
    }

    }
