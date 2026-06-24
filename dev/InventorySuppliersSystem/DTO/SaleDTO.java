public class SaleDTO {
    private String saleId;
    private String targetCategory;
    private String targetSubCategory;
    private double discountPercent;
    private String startDate;
    private String endDate;

    public SaleDTO(String saleId, String targetCategory, String targetSubCategory, double discountPercent, String startDate, String endDate) {
        this.saleId = saleId;
        this.targetCategory = targetCategory;
        this.targetSubCategory = targetSubCategory;
        this.discountPercent = discountPercent;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getSaleId() {
        return saleId;}

    public String getTargetCategory() {
        return targetCategory;}

    public String getTargetSubCategory() {
        return targetSubCategory;}

    public double getDiscountPercent() {
        return discountPercent;}

    public String getStartDate() {
        return startDate;}

    public String getEndDate() {
        return endDate;}
}