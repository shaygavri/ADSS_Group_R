import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private CategoryDAO categoryDAO;

    public CategoryRepository(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;}
    public boolean add(Category category) {
        if (category == null) {
            return false;}
        CategoryDTO dto = new CategoryDTO(0, category.getCategoryName(), category.getSubCategoryName(), category.getSubSubCategoryName());
        return categoryDAO.add(dto);}

    public Category find(String categoryName, String subCategoryName, String subSubCategoryName) {
        CategoryDTO dto = categoryDAO.find(categoryName, subCategoryName, subSubCategoryName);
        if (dto == null) {
            return null;}
        return toDomain(dto);
    }
    public Category findById(int categoryId) {
        CategoryDTO dto = categoryDAO.findById(categoryId);
        if (dto == null) {return null;}
        return toDomain(dto);
    }
    public int findCategoryId(String categoryName, String subCategoryName, String subSubCategoryName) {
        CategoryDTO dto = categoryDAO.find(categoryName, subCategoryName, subSubCategoryName);
        if (dto == null) {
            return -1;}
        return dto.getCategoryId();
    }
    public List<Category> findAll() {
        List<CategoryDTO> dtoList = categoryDAO.findAll();
        List<Category> categories = new ArrayList<>();
        for (CategoryDTO dto : dtoList) {categories.add(toDomain(dto));}
        return categories;
    }
    private Category toDomain(CategoryDTO dto) {
        return new Category(dto.getCategoryName(), dto.getSubCategoryName(), dto.getSubSubCategoryName());}
}