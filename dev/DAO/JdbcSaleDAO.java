import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcSaleDAO implements SaleDAO {
    private DatabaseManager databaseManager;
    public JdbcSaleDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
    @Override
    public boolean add(SaleDTO saleDTO) {
        String sql = "INSERT INTO sales (sale_id, target_category, target_sub_category, discount_percent, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, saleDTO.getSaleId());
            ps.setString(2, saleDTO.getTargetCategory());
            ps.setString(3, saleDTO.getTargetSubCategory());
            ps.setDouble(4, saleDTO.getDiscountPercent());
            ps.setString(5, saleDTO.getStartDate());
            ps.setString(6, saleDTO.getEndDate());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add sale: " + e.getMessage(), e);
        }
    }

    @Override
    public SaleDTO findById(String saleId) {
        String sql = "SELECT * FROM sales WHERE sale_id = ?";
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, saleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToSaleDTO(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find sale by id: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SaleDTO> findAll() {
        String sql = "SELECT * FROM sales";
        List<SaleDTO> sales = new ArrayList<>();
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sales.add(mapResultSetToSaleDTO(rs));
            }
            return sales;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all sales: " + e.getMessage(), e);
        }
    }
    @Override
    public List<SaleDTO> findActiveSales(String currentDate) {
        String sql = "SELECT * FROM sales WHERE start_date <= ? AND end_date >= ?";
        List<SaleDTO> sales = new ArrayList<>();
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, currentDate);
            ps.setString(2, currentDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sales.add(mapResultSetToSaleDTO(rs));
            }
            return sales;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find active sales: " + e.getMessage(), e);
        }
    }

    private SaleDTO mapResultSetToSaleDTO(ResultSet rs) throws SQLException {
        return new SaleDTO(rs.getString("sale_id"), rs.getString("target_category"), rs.getString("target_sub_category"), rs.getDouble("discount_percent"), rs.getString("start_date"), rs.getString("end_date"));
    }
}