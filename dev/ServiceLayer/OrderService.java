import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private DomainController domainController;
    private MockSupplierSystem supplierSystem;

    public OrderService(DomainController domainController, MockSupplierSystem supplierSystem) {
        this.domainController = domainController;
        this.supplierSystem = supplierSystem;
    }

    public List<Product> requestShortageOrder() {
        return domainController.getProductsForSystemAlerts();}

    public Order prepareShortageOrder(String productId) {
        Product product = domainController.getProductByID(productId);
        if (product == null) {
            return null;}
        if (product.getTotalQuantity() >= product.getMin_limit()) {
            return null;}
        int requiredQuantity = calculateRequiredQuantity(product);
        SupplierOffer bestOffer = supplierSystem.getBestOffer(productId, requiredQuantity);
        if (bestOffer == null) {
            return null;}
        LocalDate expectedDeliveryDate = LocalDate.now().plusDays(Math.max(product.getDeliveryTime(), 1));
        Order order = createOrder(product, requiredQuantity, bestOffer, OrderType.SHORTAGE, expectedDeliveryDate);
        boolean saved = domainController.getOrderRepository().save(order);
        if (!saved) {
            return null;}
        return order;
    }

    public boolean confirmShortageOrder(String orderId) {
        Order order = domainController.getOrderRepository().findById(orderId);
        if (order == null) {
            return false;}
        if (order.getStatus() != OrderStatus.DRAFT) {
            return false;}
        boolean supplierConfirmed = supplierSystem.sendOrder(order);
        if (!supplierConfirmed) {
            return false;}
        return domainController.getOrderRepository().updateStatus(orderId, OrderStatus.SENT);
    }

    public boolean markOrderAsReceived(String orderId) {
        Order order = domainController.getOrderRepository().findById(orderId);
        if (order == null) {
            return false;}
        if (order.getStatus() != OrderStatus.SENT) {
            return false;}
        for (OrderItem item : order.getItems()) {
            Product product = domainController.getProductByID(item.getProductId());
            if (product != null) {
                int newWarehouseQuantity = product.getWarehouseQuantity() + item.getQuantity();
                domainController.getProductRepository().updateQuantities(product.getProductID(), product.getShopQuantity(), newWarehouseQuantity, product.getDamagedAmount());}
        }
        return domainController.getOrderRepository().updateStatus(orderId, OrderStatus.RECEIVED);
    }

    public PeriodicOrderRuleDTO createPeriodicOrderRule(String productId, int quantity, int dayOfMonth) {
        Product product = domainController.getProductByID(productId);
        if (product == null) {
            return null;}
        if (quantity <= 0) {
            return null;}
        if (dayOfMonth < 1 || dayOfMonth > 31) {
            return null;}
        SupplierOffer offer = supplierSystem.getBestOffer(productId, quantity);
        if (offer == null) {
            return null;}
        return domainController.getPeriodicOrderRuleRepository().createRule(productId, quantity, dayOfMonth);
    }

    public List<Order> runAutomaticPeriodicOrders() {
        List<Order> createdOrders = new ArrayList<>();
        List<PeriodicOrderRuleDTO> rulesToRun = domainController.getPeriodicOrderRuleRepository().findRulesThatShouldRunToday();
        for (PeriodicOrderRuleDTO rule : rulesToRun) {
            Product product = domainController.getProductByID(rule.getProductId());
            if (product == null) {
                continue;}
            LocalDate expectedDeliveryDate = LocalDate.parse(rule.getNextDeliveryDate());
            if (periodicOrderAlreadyCreated(rule.getProductId(), expectedDeliveryDate)) {
                domainController.getPeriodicOrderRuleRepository().advanceNextDeliveryDate(rule);
                continue;}
            SupplierOffer bestOffer = supplierSystem.getBestOffer(rule.getProductId(), rule.getQuantity());
            if (bestOffer == null) {
                continue;}
            Order order = createOrder(product, rule.getQuantity(), bestOffer, OrderType.PERIODIC, expectedDeliveryDate);
            boolean saved = domainController.getOrderRepository().save(order);
            if (!saved) {
                continue;}
            boolean sent = supplierSystem.sendOrder(order);
            if (sent) {
                domainController.getOrderRepository().updateStatus(order.getOrderId(), OrderStatus.SENT);
                order.changeStatus(OrderStatus.SENT);
                domainController.getPeriodicOrderRuleRepository().advanceNextDeliveryDate(rule);
                createdOrders.add(order);
            }
        }
        return createdOrders;
    }

    public List<PeriodicOrderRuleDTO> getAllPeriodicOrderRules() {
        return domainController.getPeriodicOrderRuleRepository().findAll();}

    public boolean deactivatePeriodicOrderRule(String ruleId) {
        return domainController.getPeriodicOrderRuleRepository().deactivate(ruleId);}

    public List<Order> getAllOrders() {
        return domainController.getOrderRepository().findAll();}

    private int calculateRequiredQuantity(Product product) {
        int requiredQuantity = product.getMin_limit() - product.getTotalQuantity() + 1;
        if (requiredQuantity < 1) {
            return 1;}
        return requiredQuantity;
    }

    private Order createOrder(Product product, int quantity, SupplierOffer bestOffer, OrderType orderType, LocalDate expectedDeliveryDate) {
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(product, quantity, bestOffer.getUnitPrice()));
        String orderId = "O" + System.currentTimeMillis() + "_" + Math.abs(product.getProductID().hashCode());
        return new Order(orderId, items, bestOffer.getSupplierId(), bestOffer.getSupplierName(), orderType, expectedDeliveryDate);
    }

    private boolean periodicOrderAlreadyCreated(String productId, LocalDate expectedDeliveryDate) {
        for (Order order : domainController.getOrderRepository().findAll()) {
            if (order.getOrderType() == OrderType.PERIODIC && order.getStatus() != OrderStatus.CANCELLED && expectedDeliveryDate.equals(order.getExpectedDeliveryDate())) {
                for (OrderItem item : order.getItems()) {
                    if (item.getProductId().equals(productId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}