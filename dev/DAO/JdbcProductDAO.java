import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcProductDAO implements ProductDAO {
    private DatabaseManager databaseManager;

    public JdbcProductDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public boolean add(ProductDTO productDTO) {
        String sql = "INSERT INTO products (product_id, product_name, brand, category_id, shop_quantity, warehouse_quantity, total_quantity, aisle, shelf, min_limit, rank, delivery_time, cost_price, sell_price, best_price, entry_date, expiration_date, damaged_amount, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            fillProductStatement(ps, productDTO);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add product: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(ProductDTO productDTO) {
        String sql = "UPDATE products SET product_name = ?, brand = ?, category_id = ?, shop_quantity = ?, warehouse_quantity = ?, total_quantity = ?, aisle = ?, shelf = ?, min_limit = ?, rank = ?, delivery_time = ?, cost_price = ?, sell_price = ?, best_price = ?, entry_date = ?, expiration_date = ?, damaged_amount = ?, is_active = ? WHERE product_id = ?";

        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, productDTO.getProductName());
            ps.setString(2, productDTO.getBrand());
            ps.setInt(3, productDTO.getCategoryId());
            ps.setInt(4, productDTO.getShopQuantity());
            ps.setInt(5, productDTO.getWarehouseQuantity());
            ps.setInt(6, productDTO.getTotalQuantity());
            ps.setString(7, productDTO.getAisle());
            ps.setString(8, productDTO.getShelf());
            ps.setInt(9, productDTO.getMinLimit());
            ps.setInt(10, productDTO.getRank());
            ps.setInt(11, productDTO.getDeliveryTime());
            ps.setDouble(12, productDTO.getCostPrice());
            ps.setDouble(13, productDTO.getSellPrice());
            ps.setDouble(14, productDTO.getBestPrice());
            ps.setString(15, productDTO.getEntryDate());
            ps.setString(16, productDTO.getExpirationDate());
            ps.setInt(17, productDTO.getDamagedAmount());
            ps.setInt(18, productDTO.isActive() ? 1 : 0);
            ps.setString(19, productDTO.getProductId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product: " + e.getMessage(), e);
        }
    }

    @Override
    public ProductDTO findById(String productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, productId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToProductDTO(rs);
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find product by id: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ProductDTO> findAll() {
        String sql = "SELECT * FROM products";
        List<ProductDTO> products = new ArrayList<>();

        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProductDTO(rs));
            }

            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all products: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ProductDTO> findShortageProducts() {
        String sql = "SELECT * FROM products WHERE total_quantity < min_limit AND is_active = 1";
        List<ProductDTO> products = new ArrayList<>();
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProductDTO(rs));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find shortage products: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateQuantities(String productId, int shopQuantity, int warehouseQuantity, int damagedAmount) {
        String sql = "UPDATE products SET shop_quantity = ?, warehouse_quantity = ?, total_quantity = ?, damaged_amount = ? WHERE product_id = ?";
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, shopQuantity);
            ps.setInt(2, warehouseQuantity);
            ps.setInt(3, shopQuantity + warehouseQuantity);
            ps.setInt(4, damagedAmount);
            ps.setString(5, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product quantities: " + e.getMessage(), e);
        }
    }
    private void fillProductStatement(PreparedStatement ps, ProductDTO productDTO) throws SQLException {
        ps.setString(1, productDTO.getProductId());
        ps.setString(2, productDTO.getProductName());
        ps.setString(3, productDTO.getBrand());
        ps.setInt(4, productDTO.getCategoryId());
        ps.setInt(5, productDTO.getShopQuantity());
        ps.setInt(6, productDTO.getWarehouseQuantity());
        ps.setInt(7, productDTO.getTotalQuantity());
        ps.setString(8, productDTO.getAisle());
        ps.setString(9, productDTO.getShelf());
        ps.setInt(10, productDTO.getMinLimit());
        ps.setInt(11, productDTO.getRank());
        ps.setInt(12, productDTO.getDeliveryTime());
        ps.setDouble(13, productDTO.getCostPrice());
        ps.setDouble(14, productDTO.getSellPrice());
        ps.setDouble(15, productDTO.getBestPrice());
        ps.setString(16, productDTO.getEntryDate());
        ps.setString(17, productDTO.getExpirationDate());
        ps.setInt(18, productDTO.getDamagedAmount());
        ps.setInt(19, productDTO.isActive() ? 1 : 0);
    }

    private ProductDTO mapResultSetToProductDTO(ResultSet rs) throws SQLException {
        return new ProductDTO(rs.getString("product_id"), rs.getString("product_name"), rs.getString("brand"), rs.getInt("category_id"), rs.getInt("shop_quantity"), rs.getInt("warehouse_quantity"), rs.getInt("total_quantity"), rs.getString("aisle"), rs.getString("shelf"), rs.getInt("min_limit"), rs.getInt("rank"), rs.getInt("delivery_time"), rs.getDouble("cost_price"), rs.getDouble("sell_price"), rs.getDouble("best_price"), rs.getString("entry_date"), rs.getString("expiration_date"), rs.getInt("damaged_amount"), rs.getInt("is_active") == 1);
    }
}