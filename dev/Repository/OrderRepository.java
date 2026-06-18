import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private ProductRepository productRepository;

    public OrderRepository(OrderDAO orderDAO, OrderItemDAO orderItemDAO, ProductRepository productRepository) {
        this.orderDAO = orderDAO;
        this.orderItemDAO = orderItemDAO;
        this.productRepository = productRepository;}
    public boolean save(Order order) {
        if (order == null) {
            return false;}
        boolean orderSaved = orderDAO.add(toOrderDTO(order));
        if (!orderSaved) {
            return false;}
        List<OrderItemDTO> itemDTOs = toOrderItemDTOs(order);
        return orderItemDAO.addAll(itemDTOs);
    }

    public Order findById(String orderId) {
        OrderDTO orderDTO = orderDAO.findById(orderId);
        if (orderDTO == null) {
            return null;}
        List<OrderItemDTO> itemDTOs = orderItemDAO.findByOrderId(orderId);
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemDTO itemDTO : itemDTOs) {
            Product product = productRepository.findById(itemDTO.getProductId());
            OrderItem item = new OrderItem(product, itemDTO.getQuantity(), itemDTO.getUnitPrice());
            items.add(item);}
        return toDomain(orderDTO, items);
    }

    public List<Order> findAll() {
        List<OrderDTO> orderDTOs = orderDAO.findAll();
        List<Order> orders = new ArrayList<>();
        for (OrderDTO dto : orderDTOs) {
            Order order = findById(dto.getOrderId());
            if (order != null) {
                orders.add(order);}
        }
        return orders;
    }

    public boolean updateStatus(String orderId, OrderStatus status) {
        if (orderId == null || status == null) {
            return false;}
        return orderDAO.updateStatus(orderId, status.name());
    }

    public List<OrderItem> findItemsByOrderId(String orderId) {
        List<OrderItemDTO> itemDTOs = orderItemDAO.findByOrderId(orderId);
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemDTO dto : itemDTOs) {
            Product product = productRepository.findById(dto.getProductId());
            items.add(new OrderItem(product, dto.getQuantity(), dto.getUnitPrice()));
        }
        return items;
    }

    private OrderDTO toOrderDTO(Order order) {
        return new OrderDTO(order.getOrderId(), order.getSupplierId(), order.getSupplierName(), order.getStatus().name(), order.getOrderType().name(), order.getCreationDate().toString(), order.getExpectedDeliveryDate().toString(), order.getTotalPrice());
    }
    private List<OrderItemDTO> toOrderItemDTOs(Order order) {
        List<OrderItemDTO> itemDTOs = new ArrayList<>();
        int counter = 1;
        for (OrderItem item : order.getItems()) {
            String itemId = order.getOrderId() + "_ITEM_" + counter;
            OrderItemDTO dto = new OrderItemDTO(itemId, order.getOrderId(), item.getProductId(), item.getQuantity(), item.getUnitPrice(), item.calculateTotalPrice());
            itemDTOs.add(dto);
            counter++;
        }
        return itemDTOs;
    }

    private Order toDomain(OrderDTO dto, List<OrderItem> items) {
        OrderType orderType = OrderType.valueOf(dto.getOrderType());
        OrderStatus status = OrderStatus.valueOf(dto.getStatus());
        LocalDate expectedDate = dto.getExpectedDeliveryDate() != null ? LocalDate.parse(dto.getExpectedDeliveryDate()) : null;

        Order order = new Order(dto.getOrderId(), items, dto.getSupplierId(), dto.getSupplierName(), orderType, expectedDate);
        order.changeStatus(status);

        return order;
    }
}