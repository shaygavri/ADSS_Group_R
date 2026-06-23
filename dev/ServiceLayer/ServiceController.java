import java.util.List;
/**
 * Central controller for the Service Layer.
 *
 * This class integrates the core business logic by grouping different services
 * like Product, Supplier, Sale, Category and Report management into one controller.
 */
public class ServiceController {
    /** Service handling core product management and stock updates. */
    public ProductService productService;

    /** Service managing suppliers through the mocked supplier system. */
    public SupplierService supplierService;

    /** Service overseeing store-wide sales and promotional logic. */
    public SaleService saleService;

    /** Service responsible for generating inventory and damage reports. */
    public ReportService reportService;

    /** Service responsible for category operations. */
    public CategoryService categoryService;
    private OrderService orderService;
    /**
     * Mock supplier system.
     */
    public MockSupplierSystem supplierSystem;
    private DomainController domainController;
    private final Object automationLock = new Object();
    public ServiceController(DomainController domainController) {
        this.supplierSystem = new MockSupplierSystem();
        this.categoryService = new CategoryService(domainController);
        this.productService = new ProductService(domainController);
        this.domainController = domainController;
        this.reportService = new ReportService(domainController);
        this.saleService = new SaleService(domainController);
        this.supplierService = new SupplierService(supplierSystem);
        this.orderService = new OrderService(domainController, supplierSystem);
    }
    public List<Product> requestShortageOrder() {
        return orderService.requestShortageOrder();}
    public Order prepareShortageOrder(String productId) {
        return orderService.prepareShortageOrder(productId);}
    public boolean confirmShortageOrder(String orderId) {
        return orderService.confirmShortageOrder(orderId);}
    public boolean markOrderAsReceived(String orderId) {
        return orderService.markOrderAsReceived(orderId);}
    public PeriodicOrderRuleDTO createPeriodicOrderRule(String productId, int quantity, int dayOfMonth) {
        return orderService.createPeriodicOrderRule(productId, quantity, dayOfMonth);}
    public List<Order> runAutomaticPeriodicOrders() {synchronized (automationLock) {return orderService.runAutomaticPeriodicOrders();}}
    public List<PeriodicOrderRuleDTO> getAllPeriodicOrderRules() {
        return orderService.getAllPeriodicOrderRules();}
    public List<Order> runAutomaticShortageOrders() {
        synchronized (automationLock) {
            return orderService.runAutomaticShortageOrders();}}
    public boolean deactivatePeriodicOrderRule(String ruleId) {
        return orderService.deactivatePeriodicOrderRule(ruleId);}
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();}
    public DomainController getDomainController() {
        return domainController;
    }
}