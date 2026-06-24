import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcOrderItemDAO implements OrderItemDAO {
    private DatabaseManager databaseManager;
    public JdbcOrderItemDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public boolean add(OrderItemDTO orderItemDTO) {
        String sql = "INSERT INTO order_items (order_item_id, order_id, product_id, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, orderItemDTO.getOrderItemId());
            ps.setString(2, orderItemDTO.getOrderId());
            ps.setString(3, orderItemDTO.getProductId());
            ps.setInt(4, orderItemDTO.getQuantity());
            ps.setDouble(5, orderItemDTO.getUnitPrice());
            ps.setDouble(6, orderItemDTO.getTotalPrice());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add order item: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean addAll(List<OrderItemDTO> orderItemDTOs) {
        if (orderItemDTOs == null || orderItemDTOs.isEmpty()) {
            return false;
        }
        for (OrderItemDTO itemDTO : orderItemDTOs) {
            boolean added = add(itemDTO);
            if (!added) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<OrderItemDTO> findByOrderId(String orderId) {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        List<OrderItemDTO> items = new ArrayList<>();
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToOrderItemDTO(rs));
            }
            return items;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find order items by order id: " + e.getMessage(), e);
        }
    }

    private OrderItemDTO mapResultSetToOrderItemDTO(ResultSet rs) throws SQLException {
        return new OrderItemDTO(rs.getString("order_item_id"), rs.getString("order_id"), rs.getString("product_id"), rs.getInt("quantity"), rs.getDouble("unit_price"), rs.getDouble("total_price"));
    }
}