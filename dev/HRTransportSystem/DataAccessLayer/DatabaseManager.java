package DataAccessLayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    public static final String DEFAULT_DB_FILE = "super_lee.db";
    public static final String IN_MEMORY_DB_FILE = ":memory:";

    private static DatabaseManager instance;

    private final String url;
    private Connection connection;
    private boolean initialized;

    private DatabaseManager(String dbFile) {
        this.url = "jdbc:sqlite:" + dbFile;
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager(DEFAULT_DB_FILE);
        }
        return instance;
    }

    // used by tests to switch to a throwaway database (e.g. IN_MEMORY_DB_FILE)
    public static synchronized DatabaseManager useDatabaseFile(String dbFile) {
        if (instance != null) {
            instance.close();
        }
        instance = new DatabaseManager(dbFile);
        return instance;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try { Class.forName("org.sqlite.JDBC"); } catch (ClassNotFoundException e) { throw new SQLException("SQLite driver not found", e); }
            connection = DriverManager.getConnection(url);
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON;");
            }
        }
        return connection;
    }

    public synchronized void initialize() throws SQLException {
        if (initialized) {
            return;
        }

        Connection conn = getConnection();
        conn.setAutoCommit(false);
        try (Statement statement = conn.createStatement()) {
            for (String ddl : SCHEMA) {
                statement.execute(ddl);
            }
            conn.commit();
            initialized = true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public synchronized void resetSchema() throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);
        try (Statement statement = conn.createStatement()) {
            statement.execute("PRAGMA foreign_keys = OFF;");
            for (int i = TABLES.length - 1; i >= 0; i--) {
                statement.execute("DROP TABLE IF EXISTS " + TABLES[i] + ";");
            }
            for (String ddl : SCHEMA) {
                statement.execute(ddl);
            }
            statement.execute("PRAGMA foreign_keys = ON;");
            conn.commit();
            initialized = true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public synchronized void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
            connection = null;
        }
        initialized = false;
    }

    // table names in creation order, used to drop them in reverse
    private static final String[] TABLES = {
            "branch",
            "role",
            "employee",
            "employee_role",
            "employee_availability",
            "shift_organizer",
            "shift",
            "shift_requirement",
            "shift_assignment"
    };

    private static final String[] SCHEMA = {

            "CREATE TABLE IF NOT EXISTS branch (" +
                    "  branch_id INTEGER PRIMARY KEY" +
                    ");",

            "CREATE TABLE IF NOT EXISTS role (" +
                    "  role_id     INTEGER PRIMARY KEY," +
                    "  role_name   TEXT NOT NULL UNIQUE COLLATE NOCASE," +
                    "  description TEXT NOT NULL" +
                    ");",

            "CREATE TABLE IF NOT EXISTS employee (" +
                    "  user_name           TEXT PRIMARY KEY COLLATE NOCASE," +
                    "  password            TEXT NOT NULL," +
                    "  national_id         TEXT NOT NULL," +
                    "  branch_id           INTEGER NOT NULL," +
                    "  hourly_salary       INTEGER NOT NULL," +
                    "  vacation_days       INTEGER NOT NULL DEFAULT 20," +
                    "  employment_type     TEXT NOT NULL CHECK (employment_type IN ('FULL_TIME','PART_TIME'))," +
                    "  driver_license_type TEXT CHECK (driver_license_type IN ('B','C1','C','CE'))," +
                    "  start_contract      TEXT NOT NULL," +
                    "  end_contract        TEXT NOT NULL," +
                    "  is_logged_in        INTEGER NOT NULL DEFAULT 0," +
                    "  bank_number         INTEGER," +
                    "  bank_branch_number  INTEGER," +
                    "  bank_account_number INTEGER," +
                    "  status              TEXT NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','FIRED'))," +
                    "  FOREIGN KEY (branch_id) REFERENCES branch(branch_id)" +
                    ");",

            "CREATE TABLE IF NOT EXISTS employee_role (" +
                    "  user_name TEXT NOT NULL COLLATE NOCASE," +
                    "  role_id   INTEGER NOT NULL," +
                    "  PRIMARY KEY (user_name, role_id)," +
                    "  FOREIGN KEY (user_name) REFERENCES employee(user_name) ON DELETE CASCADE," +
                    "  FOREIGN KEY (role_id)   REFERENCES role(role_id)" +
                    ");",

            "CREATE TABLE IF NOT EXISTS employee_availability (" +
                    "  user_name   TEXT NOT NULL COLLATE NOCASE," +
                    "  shift_index INTEGER NOT NULL CHECK (shift_index BETWEEN 0 AND 13)," +
                    "  PRIMARY KEY (user_name, shift_index)," +
                    "  FOREIGN KEY (user_name) REFERENCES employee(user_name) ON DELETE CASCADE" +
                    ");",

            "CREATE TABLE IF NOT EXISTS shift_organizer (" +
                    "  branch_id                    INTEGER PRIMARY KEY," +
                    "  week_start_date              TEXT NOT NULL," +
                    "  availability_changes_allowed INTEGER NOT NULL DEFAULT 1," +
                    "  is_published                 INTEGER NOT NULL DEFAULT 0," +
                    "  requirements_published_at    TEXT," +
                    "  availability_deadline        TEXT," +
                    "  FOREIGN KEY (branch_id) REFERENCES branch(branch_id) ON DELETE CASCADE" +
                    ");",

            // week_category is CURRENT, NEXT or HISTORY; history_week_index orders the history weeks
            "CREATE TABLE IF NOT EXISTS shift (" +
                    "  shift_pk           INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "  branch_id          INTEGER NOT NULL," +
                    "  week_category      TEXT NOT NULL CHECK (week_category IN ('CURRENT','NEXT','HISTORY'))," +
                    "  history_week_index INTEGER," +
                    "  shift_index        INTEGER NOT NULL CHECK (shift_index BETWEEN 0 AND 13)," +
                    "  shift_date         TEXT NOT NULL," +
                    "  shift_type         TEXT NOT NULL CHECK (shift_type IN ('MORNING','EVENING'))," +
                    "  closed_day         INTEGER NOT NULL DEFAULT 0," +
                    "  UNIQUE (branch_id, week_category, history_week_index, shift_index)," +
                    "  FOREIGN KEY (branch_id) REFERENCES branch(branch_id) ON DELETE CASCADE" +
                    ");",

            "CREATE TABLE IF NOT EXISTS shift_requirement (" +
                    "  shift_pk       INTEGER NOT NULL," +
                    "  role_id        INTEGER NOT NULL," +
                    "  required_count INTEGER NOT NULL CHECK (required_count >= 0)," +
                    "  PRIMARY KEY (shift_pk, role_id)," +
                    "  FOREIGN KEY (shift_pk) REFERENCES shift(shift_pk) ON DELETE CASCADE," +
                    "  FOREIGN KEY (role_id)  REFERENCES role(role_id)" +
                    ");",

            "CREATE TABLE IF NOT EXISTS shift_assignment (" +
                    "  shift_pk  INTEGER NOT NULL," +
                    "  user_name TEXT NOT NULL COLLATE NOCASE," +
                    "  role_id   INTEGER NOT NULL," +
                    "  PRIMARY KEY (shift_pk, user_name)," +
                    "  FOREIGN KEY (shift_pk)  REFERENCES shift(shift_pk) ON DELETE CASCADE," +
                    "  FOREIGN KEY (user_name) REFERENCES employee(user_name)," +
                    "  FOREIGN KEY (role_id)   REFERENCES role(role_id)" +
                    ");"
    };
}
