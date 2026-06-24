import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DEFAULT_DATABASE_URL = "jdbc:sqlite:superli_inventory.db";

    private String databaseUrl;
    private Connection connection;

    public DatabaseManager() {
        this.databaseUrl = DEFAULT_DATABASE_URL;}

    public DatabaseManager(String databaseUrl) {
        this.databaseUrl = databaseUrl;}

    public Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(databaseUrl);
                enableForeignKeys();
            }
            return connection;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver was not found. Add sqlite-jdbc.jar to the project dependencies.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to SQLite database: " + e.getMessage(), e);
        }
    }

    public Connection getConnection() {
        return connect();
    }

    public void createTables() {
        createCategoriesTable();
        createProductsTable();
        createSalesTable();
        createOrdersTable();
        createOrderItemsTable();
        createPeriodicOrderRulesTable();
    }

    private void enableForeignKeys() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to enable SQLite foreign keys: " + e.getMessage(), e);
        }
    }

    private void createCategoriesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS categories (" + "category_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "category_name TEXT NOT NULL, " +
                "sub_category_name TEXT NOT NULL, " + "sub_sub_category_name TEXT NOT NULL, " + "UNIQUE(category_name, sub_category_name, sub_sub_category_name)" +
                ");";

        executeUpdate(sql);
    }
    private void createPeriodicOrderRulesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS periodic_order_rules (" +
                "rule_id TEXT PRIMARY KEY, " +
                "product_id TEXT NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "day_of_month INTEGER NOT NULL, " +
                "next_delivery_date TEXT NOT NULL, " +
                "active INTEGER NOT NULL DEFAULT 1, " +
                "FOREIGN KEY(product_id) REFERENCES products(product_id)" +
                ");";
        executeUpdate(sql);
    }
    private void createProductsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS products (" +
                "product_id TEXT PRIMARY KEY, " + "product_name TEXT NOT NULL, " +
                "brand TEXT NOT NULL, " + "category_id INTEGER NOT NULL, " + "shop_quantity INTEGER NOT NULL DEFAULT 0, " + "warehouse_quantity INTEGER NOT NULL DEFAULT 0, " + "total_quantity INTEGER NOT NULL DEFAULT 0, " +
                "aisle TEXT, " + "shelf TEXT, " +
                "min_limit INTEGER NOT NULL DEFAULT 0, " + "rank INTEGER NOT NULL DEFAULT 0, " + "delivery_time INTEGER NOT NULL DEFAULT 0, " + "cost_price REAL NOT NULL DEFAULT 0, " + "sell_price REAL NOT NULL DEFAULT 0, " +
                "best_price REAL NOT NULL DEFAULT 0, " + "entry_date TEXT, " + "expiration_date TEXT, " + "damaged_amount INTEGER NOT NULL DEFAULT 0, " + "is_active INTEGER NOT NULL DEFAULT 1, " + "FOREIGN KEY(category_id) REFERENCES categories(category_id)" + ");";

        executeUpdate(sql);
    }

    private void createSalesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS sales (" + "sale_id TEXT PRIMARY KEY, " + "target_category TEXT NOT NULL, " +
                "target_sub_category TEXT NOT NULL, " + "discount_percent REAL NOT NULL, " + "start_date TEXT NOT NULL, " + "end_date TEXT NOT NULL" + ");";

        executeUpdate(sql);
    }

    private void createOrdersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS orders (" + "order_id TEXT PRIMARY KEY, " + "supplier_id TEXT NOT NULL, " + "supplier_name TEXT NOT NULL, " +
                "status TEXT NOT NULL, " + "order_type TEXT NOT NULL, " + "creation_date TEXT NOT NULL, " + "expected_delivery_date TEXT, " + "total_price REAL NOT NULL DEFAULT 0" + ");";

        executeUpdate(sql);
    }

    private void createOrderItemsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS order_items (" + "order_item_id TEXT PRIMARY KEY, " + "order_id TEXT NOT NULL, " + "product_id TEXT NOT NULL, " + "quantity INTEGER NOT NULL, " +
                "unit_price REAL NOT NULL, " + "total_price REAL NOT NULL, " + "FOREIGN KEY(order_id) REFERENCES orders(order_id), " + "FOREIGN KEY(product_id) REFERENCES products(product_id)" + ");";
        executeUpdate(sql);
    }

    private void executeUpdate(String sql) {
        try (Statement statement = connect().createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute SQL statement: " + e.getMessage(), e);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close SQLite connection: " + e.getMessage(), e);
        }
    }
}