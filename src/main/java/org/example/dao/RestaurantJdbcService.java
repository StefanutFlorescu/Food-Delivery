package org.example.dao;

import org.example.db.AuditService;
import org.example.db.DatabaseManager;
import org.example.model.Restaurant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantJdbcService {
    private static final RestaurantJdbcService INSTANCE = new RestaurantJdbcService();

    private RestaurantJdbcService() {}

    public static RestaurantJdbcService getInstance() { return INSTANCE; }

    public void create(Restaurant r) {
        String sql = "INSERT INTO restaurants(id,name,address) VALUES(?,?,?)";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, r.getId());
            ps.setString(2, r.getName());
            ps.setString(3, r.getAddress());
            ps.executeUpdate();
            AuditService.getInstance().record("create_restaurant:" + r.getId());
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Restaurant findById(int id) {
        String sql = "SELECT id,name,address FROM restaurants WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Restaurant(rs.getInt(1), rs.getString(2), rs.getString(3));
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Restaurant> listAll() {
        String sql = "SELECT id,name,address FROM restaurants";
        List<Restaurant> res = new ArrayList<>();
        try (Connection c = DatabaseManager.getInstance().getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) res.add(new Restaurant(rs.getInt(1), rs.getString(2), rs.getString(3)));
            return res;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void update(Restaurant r) {
        String sql = "UPDATE restaurants SET name=?,address=? WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.getName());
            ps.setString(2, r.getAddress());
            ps.setInt(3, r.getId());
            ps.executeUpdate();
            AuditService.getInstance().record("update_restaurant:" + r.getId());
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void delete(int id) {
        String sql = "DELETE FROM restaurants WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            AuditService.getInstance().record("delete_restaurant:" + id);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
