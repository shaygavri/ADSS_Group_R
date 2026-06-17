/**
 * Central controller for the Service Layer.
 * This class integrates the core business logic by grouping different services like Product, Supplier, Sale, and Report
 * management into one controller.
 * into a single entry point. It simplifies the interaction between the Presentation Layer
 * and the various business logic components.
 */

public class ServiceController {
    /** Service handling core product management and stock updates. */
    public ProductService productService;

    /** Service managing supplier credentials and discount rates. */
    public SupplierService supplierService;

    /** Service overseeing store-wide sales and promotional logic. */
    public SaleService saleService;

    /** Service responsible for generating inventory and damage reports. */
    public ReportService reportService;
    public CategoryService categoryService;

    /**
     * Constructs a new ServiceController and initializes all sub-services.
     * Each sub-service is provided with a shared instance of the DomainController
     * to ensure data consistency across the system.
     * * @param domainController The domain layer instance to be shared among all services.
     */
    public  ServiceController(DomainController domainController) {
        this.productService = new ProductService(domainController);
        this.supplierService = new SupplierService(domainController);
        this.saleService = new SaleService(domainController);
        this.reportService = new ReportService(domainController);
        this.categoryService = new CategoryService(domainController);
    }
}
