import java.util.List;

public interface CategoryDAO {
    boolean add(CategoryDTO categoryDTO);
    CategoryDTO findById(int categoryId);
    CategoryDTO find(String categoryName, String subCategoryName, String subSubCategoryName);
    List<CategoryDTO> findAll();
}