import part1_singleton.Order;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Single-file SQLite layer for QuickBite.
 *
 * Contains:
 *   - Database        : singleton connection + schema setup
 *   - OrderRepository  : save/read orders
 *
 * Usage (called once from RealOrderService, after an order is processed):
 *   new OrderRepository().save(order, region, channel, "PROCESSED");
 *
 * Requires the sqlite-jdbc driver jar on the classpath, e.g.
 *   sqlite-jdbc-3.53.2.1-natives-all.jar
 */
public class Database {

    private static final String DB_URL = "jdbc:sqlite:orders.db";

    private static Connection connection;

    private Database() {
        // no instances - static helper
    }

    public static synchronized Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("org.sqlite.JDBC"); // force-load the SQLite driver
                connection = DriverManager.getConnection(DB_URL);
                createTableIfNotExists();
                System.out.println("[DB] Connected to SQLite at orders.db");
            } catch (ClassNotFoundException e) {
                System.out.println("[DB] SQLite driver not found on classpath: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("[DB] Failed to connect: " + e.getMessage());
            }
        }
        return connection;
    }

    private static void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "customer_name TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "tax REAL NOT NULL," +
                "total REAL NOT NULL," +
                "coupon_code TEXT," +
                "region TEXT," +
                "channel TEXT," +
                "status TEXT," +
                "created_at TEXT DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sql);
        }
    }

    /** DAO (Data Access Object) - saves Order objects into SQLite and reads them back. */
    public static class OrderRepository {

        /** Save one order after it has been successfully processed. */
        public void save(Order order, String region, String channel, String status) {
            String sql = "INSERT INTO orders " +
                    "(customer_name, amount, tax, total, coupon_code, region, channel, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            Connection conn = Database.getConnection();
            if (conn == null) return;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, order.getCustomerName());
                ps.setDouble(2, order.getAmount());
                ps.setDouble(3, order.getTax());
                ps.setDouble(4, order.getTotalPayable());
                ps.setString(5, order.getCouponCode());
                ps.setString(6, region);
                ps.setString(7, channel);
                ps.setString(8, status);
                ps.executeUpdate();
                System.out.println("[DB] Order saved for " + order.getCustomerName());
            } catch (SQLException e) {
                System.out.println("[DB] Failed to save order: " + e.getMessage());
            }
        }

        /** Fetch all saved orders, most recent first. Handy for a history view. */
        public List<String> findAllAsText() {
            List<String> rows = new ArrayList<>();
            String sql = "SELECT id, customer_name, amount, tax, total, region, channel, status, created_at " +
                    "FROM orders ORDER BY id DESC";

            Connection conn = Database.getConnection();
            if (conn == null) return rows;

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String row = "#" + rs.getInt("id")
                            + " | " + rs.getString("customer_name")
                            + " | amount=" + rs.getDouble("amount")
                            + " | tax=" + rs.getDouble("tax")
                            + " | total=" + rs.getDouble("total")
                            + " | region=" + rs.getString("region")
                            + " | channel=" + rs.getString("channel")
                            + " | status=" + rs.getString("status")
                            + " | at=" + rs.getString("created_at");
                    rows.add(row);
                }
            } catch (SQLException e) {
                System.out.println("[DB] Failed to fetch orders: " + e.getMessage());
            }
            return rows;
        }
    }
}
