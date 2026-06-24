public class CategoryDTO {
    private int categoryId;
    private String categoryName;
    private String subCategoryName;
    private String subSubCategoryName;
    public CategoryDTO(int categoryId, String categoryName, String subCategoryName, String subSubCategoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.subCategoryName = subCategoryName;
        this.subSubCategoryName = subSubCategoryName;}
    public int getCategoryId() {
        return categoryId;}
    public String getCategoryName() {
        return categoryName;}
    public String getSubCategoryName() {
        return subCategoryName;}
    public String getSubSubCategoryName() {
        return subSubCategoryName;}
}