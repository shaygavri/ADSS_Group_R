import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcOrderDAO implements OrderDAO {
    private DatabaseManager databaseManager;
    public JdbcOrderDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public boolean add(OrderDTO orderDTO) {
        String sql = "INSERT INTO orders (order_id, supplier_id, supplier_name, status, order_type, creation_date, expected_delivery_date, total_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, orderDTO.getOrderId());
            ps.setString(2, orderDTO.getSupplierId());
            ps.setString(3, orderDTO.getSupplierName());
            ps.setString(4, orderDTO.getStatus());
            ps.setString(5, orderDTO.getOrderType());
            ps.setString(6, orderDTO.getCreationDate());
            ps.setString(7, orderDTO.getExpectedDeliveryDate());
            ps.setDouble(8, orderDTO.getTotalPrice());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add order: " + e.getMessage(), e);
        }
    }

    @Override
    public OrderDTO findById(String orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToOrderDTO(rs);}
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find order by id: " + e.getMessage(), e);
        }
    }

    @Override
    public List<OrderDTO> findAll() {
        String sql = "SELECT * FROM orders";
        List<OrderDTO> orders = new ArrayList<>();
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToOrderDTO(rs));}
            return orders;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all orders: " + e.getMessage(), e);
        }
    }

    @Override
    public List<OrderDTO> findByStatus(String status) {
        String sql = "SELECT * FROM orders WHERE status = ?";
        List<OrderDTO> orders = new ArrayList<>();
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToOrderDTO(rs));
            }
            return orders;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find orders by status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(String orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update order status: " + e.getMessage(), e);
        }
    }

    private OrderDTO mapResultSetToOrderDTO(ResultSet rs) throws SQLException {
        return new OrderDTO(rs.getString("order_id"), rs.getString("supplier_id"), rs.getString("supplier_name"), rs.getString("status"), rs.getString("order_type"), rs.getString("creation_date"), rs.getString("expected_delivery_date"), rs.getDouble("total_price"));
    }
}