import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcCategoryDAO implements CategoryDAO {
    private DatabaseManager databaseManager;

    public JdbcCategoryDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public boolean add(CategoryDTO categoryDTO) {
        String sql = "INSERT OR IGNORE INTO categories (category_name, sub_category_name, sub_sub_category_name) VALUES (?, ?, ?)";
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, categoryDTO.getCategoryName());
            ps.setString(2, categoryDTO.getSubCategoryName());
            ps.setString(3, categoryDTO.getSubSubCategoryName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add category: " + e.getMessage(), e);
        }
    }

    @Override
    public CategoryDTO findById(int categoryId) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToCategoryDTO(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find category by id: " + e.getMessage(), e);
        }
    }
    @Override
    public CategoryDTO find(String categoryName, String subCategoryName, String subSubCategoryName) {
        String sql = "SELECT * FROM categories WHERE LOWER(category_name) = LOWER(?) AND LOWER(sub_category_name) = LOWER(?) AND LOWER(sub_sub_category_name) = LOWER(?)";
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, categoryName);
            ps.setString(2, subCategoryName);
            ps.setString(3, subSubCategoryName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToCategoryDTO(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find category: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CategoryDTO> findAll() {
        String sql = "SELECT * FROM categories";
        List<CategoryDTO> categories = new ArrayList<>();
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categories.add(mapResultSetToCategoryDTO(rs));
            }
            return categories;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all categories: " + e.getMessage(), e);
        }
    }
    private CategoryDTO mapResultSetToCategoryDTO(ResultSet rs) throws SQLException {
        return new CategoryDTO(rs.getInt("category_id"), rs.getString("category_name"), rs.getString("sub_category_name"), rs.getString("sub_sub_category_name"));
    }
}