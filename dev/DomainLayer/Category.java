public class Category {
    private String categoryName;
    private String subCategoryName;
    private String subSubCategoryName;
    public Category(String categoryName,
                    String subCategoryName,
                    String subSubCategoryName) {
        this.categoryName = categoryName;
        this.subCategoryName = subCategoryName;
        this.subSubCategoryName = subSubCategoryName;}
    public String getCategoryName() {return categoryName;}
    public String getSubCategoryName() {return subCategoryName;}
    public String getSubSubCategoryName() {return subSubCategoryName;}
    @Override
    public String toString() {
        return categoryName
                + " -> " + subCategoryName
                + " -> " + subSubCategoryName;}
}
