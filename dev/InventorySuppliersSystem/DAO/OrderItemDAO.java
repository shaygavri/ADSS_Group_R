import java.util.List;

public interface OrderItemDAO {
    boolean add(OrderItemDTO orderItemDTO);
    boolean addAll(List<OrderItemDTO> orderItemDTOs);
    List<OrderItemDTO> findByOrderId(String orderId);
}