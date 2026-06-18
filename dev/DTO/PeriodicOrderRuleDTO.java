public class PeriodicOrderRuleDTO {
    private String ruleId;
    private String productId;
    private int quantity;
    private int dayOfMonth;
    private String nextDeliveryDate;
    private boolean active;

    public PeriodicOrderRuleDTO(String ruleId, String productId, int quantity, int dayOfMonth, String nextDeliveryDate, boolean active) {
        this.ruleId = ruleId;
        this.productId = productId;
        this.quantity = quantity;
        this.dayOfMonth = dayOfMonth;
        this.nextDeliveryDate = nextDeliveryDate;
        this.active = active;
    }
    public String getRuleId() {
        return ruleId;}
    public String getProductId() {
        return productId;}
    public int getQuantity() {
        return quantity;}
    public int getDayOfMonth() {
        return dayOfMonth;}
    public String getNextDeliveryDate() {
        return nextDeliveryDate;}
    public boolean isActive() {
        return active;
    }
}