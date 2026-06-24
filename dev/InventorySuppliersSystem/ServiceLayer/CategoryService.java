import java.util.ArrayList;
import java.util.List;

/**
 * Service Layer for category-related operations.
 * Validates category information before passing it
 * to the Domain Layer.
 */
public class CategoryService {
    private DomainController domain;
    public CategoryService(DomainController domain) {
        this.domain = domain;
    }
    /**
     * Prints an error message for an invalid field.
     */
    private boolean errorType(String fieldName) {
        System.out.println(fieldName + " is invalid or missing.");
        return false;
    }
    /**
     * Adds a complete category classification:
     * Category -> Sub-Category -> Sub-Sub-Category.
     */
    public boolean addCategory(String categoryName, String subCategoryName, String subSubCategoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return errorType("Category");
        }
        if (subCategoryName == null || subCategoryName.trim().isEmpty()) {
            return errorType("Sub-Category");}
        if (subSubCategoryName == null
                || subSubCategoryName.trim().isEmpty()) {
            return errorType("Sub-Sub-Category");
        }
        boolean added = domain.addCategory(
                categoryName.trim(),
                subCategoryName.trim(),
                subSubCategoryName.trim()
        );
        if (!added) {System.out.println("This category classification already exists.");}
        return added;}

    /**
     * Finds an exact category classification.
     */
    public Category findCategory(String categoryName,
                                 String subCategoryName,
                                 String subSubCategoryName) {
        if (categoryName == null
                || subCategoryName == null
                || subSubCategoryName == null) {
            return null;
        }
        return domain.findCategory(categoryName.trim(), subCategoryName.trim(), subSubCategoryName.trim());
    }
    /**
     * Returns all categories stored in the system.
     */
    public List<Category> getAllCategories() {
        List<Category> categories = domain.getAllCategories();
        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }
        return categories;
    }
}