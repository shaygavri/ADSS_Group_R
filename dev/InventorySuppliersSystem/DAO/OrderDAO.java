import java.util.List;

public interface OrderDAO {
    boolean add(OrderDTO orderDTO);
    OrderDTO findById(String orderId);
    List<OrderDTO> findAll();
    List<OrderDTO> findByStatus(String status);
    boolean updateStatus(String orderId, String status);
}