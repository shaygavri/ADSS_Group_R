import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcPeriodicOrderRuleDAO implements PeriodicOrderRuleDAO {
    private DatabaseManager databaseManager;

    public JdbcPeriodicOrderRuleDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;}

    @Override
    public boolean add(PeriodicOrderRuleDTO ruleDTO) {
        String sql = "INSERT INTO periodic_order_rules (rule_id, product_id, quantity, day_of_month, next_delivery_date, active) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, ruleDTO.getRuleId());
            ps.setString(2, ruleDTO.getProductId());
            ps.setInt(3, ruleDTO.getQuantity());
            ps.setInt(4, ruleDTO.getDayOfMonth());
            ps.setString(5, ruleDTO.getNextDeliveryDate());
            ps.setInt(6, ruleDTO.isActive() ? 1 : 0);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add periodic order rule: " + e.getMessage(), e);
        }
    }

    @Override
    public PeriodicOrderRuleDTO findById(String ruleId) {
        String sql = "SELECT * FROM periodic_order_rules WHERE rule_id = ?";
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, ruleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToDTO(rs);}
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find periodic order rule by id: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PeriodicOrderRuleDTO> findAll() {
        String sql = "SELECT * FROM periodic_order_rules";
        List<PeriodicOrderRuleDTO> rules = new ArrayList<>();
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rules.add(mapResultSetToDTO(rs));}
            return rules;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all periodic order rules: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PeriodicOrderRuleDTO> findActiveRules() {
        String sql = "SELECT * FROM periodic_order_rules WHERE active = 1";
        List<PeriodicOrderRuleDTO> rules = new ArrayList<>();
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rules.add(mapResultSetToDTO(rs));
            }
            return rules;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find active periodic order rules: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateNextDeliveryDate(String ruleId, String nextDeliveryDate) {
        String sql = "UPDATE periodic_order_rules SET next_delivery_date = ? WHERE rule_id = ?";
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, nextDeliveryDate);
            ps.setString(2, ruleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update next delivery date: " + e.getMessage(), e);
        }
    }
    @Override
    public boolean deactivate(String ruleId) {
        String sql = "UPDATE periodic_order_rules SET active = 0 WHERE rule_id = ?";

        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, ruleId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to deactivate periodic order rule: " + e.getMessage(), e);
        }
    }
    private PeriodicOrderRuleDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new PeriodicOrderRuleDTO(rs.getString("rule_id"), rs.getString("product_id"), rs.getInt("quantity"), rs.getInt("day_of_month"), rs.getString("next_delivery_date"), rs.getInt("active") == 1);
    }
}