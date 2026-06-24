import java.util.List;

public interface SaleDAO {
    boolean add(SaleDTO saleDTO);
    SaleDTO findById(String saleId);
    List<SaleDTO> findAll();
    List<SaleDTO> findActiveSales(String currentDate);
}