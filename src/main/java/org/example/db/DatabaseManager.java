package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final DatabaseManager INSTANCE = new DatabaseManager();
    private final String url = "jdbc:postgresql://localhost:5432/food_delivery";
    private final String user = "food_user";
    private final String password = "food_pass";

    private DatabaseManager() {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            // create tables
            s.execute("CREATE TABLE IF NOT EXISTS users(" +
                    "id INT PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "address VARCHAR(255), " +
                    "premium BOOLEAN, " +
                    "loyalty INT, " +
                    "email VARCHAR(255), " +
                    "phone_number VARCHAR(100), " +
                    "user_type VARCHAR(50), " +
                    "university VARCHAR(255), " +
                    "student_discount INT)");
            s.execute("CREATE TABLE IF NOT EXISTS restaurants(id INT PRIMARY KEY, name VARCHAR(255), address VARCHAR(255))");
            s.execute("CREATE TABLE IF NOT EXISTS drivers(" +
                    "id INT PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "available BOOLEAN, " +
                    "driver_type VARCHAR(50), " +
                    "vehicle_type VARCHAR(100), " +
                    "speed_multiplier DOUBLE PRECISION)");
            s.execute("CREATE TABLE IF NOT EXISTS orders(id INT PRIMARY KEY, user_id INT, restaurant_id INT, items VARCHAR(1024), driver_id INT, status VARCHAR(50))");
            s.execute("CREATE TABLE IF NOT EXISTS menus(id INT PRIMARY KEY, restaurant_id INT)");
            s.execute("CREATE TABLE IF NOT EXISTS menu_items(id INT PRIMARY KEY, menu_id INT, name VARCHAR(255), price DOUBLE PRECISION)");
            s.execute("CREATE TABLE IF NOT EXISTS reviews(id INT PRIMARY KEY, user_id INT, restaurant_id INT, rating INT, comment VARCHAR(1024))");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static DatabaseManager getInstance() { return INSTANCE; }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            // driver not on classpath
            throw new SQLException("PostGres Driver not found on classpath", e);
        }
        return DriverManager.getConnection(url, user, password);
    }
}
