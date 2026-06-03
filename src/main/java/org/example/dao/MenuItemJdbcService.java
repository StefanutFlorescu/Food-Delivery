package org.example.dao;

import org.example.db.AuditService;
import org.example.db.DatabaseManager;
import org.example.model.MenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemJdbcService {
    private static final MenuItemJdbcService INSTANCE = new MenuItemJdbcService();

    private MenuItemJdbcService() {}

    public static MenuItemJdbcService getInstance() {
        return INSTANCE;
    }

    public void create(int menuId, MenuItem item) {
        String sql = "INSERT INTO menu_items(id,menu_id,name,price) VALUES(?,?,?,?)";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, item.getId());
            ps.setInt(2, menuId);
            ps.setString(3, item.getName());
            ps.setDouble(4, item.getPrice());
            ps.executeUpdate();
            AuditService.getInstance().record("create_menu_item:" + item.getId());
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public MenuItem findById(int id) {
        String sql = "SELECT id,name,price FROM menu_items WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MenuItem(rs.getInt(1), rs.getString(2), rs.getDouble(3));
                }
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<MenuItem> listAll() {
        String sql = "SELECT id,name,price FROM menu_items";
        List<MenuItem> res = new ArrayList<>();
        try (Connection c = DatabaseManager.getInstance().getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                res.add(new MenuItem(rs.getInt(1), rs.getString(2), rs.getDouble(3)));
            }
            return res;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<MenuItem> listByMenuId(int menuId) {
        String sql = "SELECT id,name,price FROM menu_items WHERE menu_id=?";
        List<MenuItem> res = new ArrayList<>();
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, menuId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(new MenuItem(rs.getInt(1), rs.getString(2), rs.getDouble(3)));
                }
                return res;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void update(MenuItem item) {
        String sql = "UPDATE menu_items SET name=?,price=? WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setDouble(2, item.getPrice());
            ps.setInt(3, item.getId());
            ps.executeUpdate();
            AuditService.getInstance().record("update_menu_item:" + item.getId());
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void delete(int id) {
        String sql = "DELETE FROM menu_items WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            AuditService.getInstance().record("delete_menu_item:" + id);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}

