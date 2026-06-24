import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaleRepository {
    private SaleDAO saleDAO;

    public SaleRepository(SaleDAO saleDAO) {
        this.saleDAO = saleDAO;}

    public boolean add(Sale sale) {
        if (sale == null) {
            return false;}
        return saleDAO.add(toDTO(sale));
    }

    public Sale findById(String saleId) {
        SaleDTO dto = saleDAO.findById(saleId);
        if (dto == null) {
            return null;}
        return toDomain(dto);
    }

    public List<Sale> findAll() {
        List<SaleDTO> dtoList = saleDAO.findAll();
        List<Sale> sales = new ArrayList<>();
        for (SaleDTO dto : dtoList) {
            sales.add(toDomain(dto));}
        return sales;
    }

    public List<Sale> findActiveSales() {
        List<SaleDTO> dtoList = saleDAO.findActiveSales(LocalDate.now().toString());
        List<Sale> sales = new ArrayList<>();
        for (SaleDTO dto : dtoList) {
            sales.add(toDomain(dto));}
        return sales;
    }

    private SaleDTO toDTO(Sale sale) {
        return new SaleDTO(sale.getSaleID(), sale.getTarget_category(), sale.getTarget_subcat(), sale.getDiscountPrecent(), sale.getStartDate().toString(), sale.getEndDate().toString());}

    private Sale toDomain(SaleDTO dto) {
        return new Sale(dto.getSaleId(), dto.getTargetSubCategory(), dto.getTargetCategory(), dto.getDiscountPercent(), LocalDate.parse(dto.getStartDate()), LocalDate.parse(dto.getEndDate()));}
}