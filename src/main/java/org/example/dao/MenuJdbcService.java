package org.example.dao;

import org.example.db.AuditService;
import org.example.db.DatabaseManager;
import org.example.model.Menu;
import org.example.model.MenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuJdbcService {
    private static final MenuJdbcService INSTANCE = new MenuJdbcService();

    private MenuJdbcService() {}

    public static MenuJdbcService getInstance() {
        return INSTANCE;
    }

    public void create(Menu menu) {
        String sql = "INSERT INTO menus (id, restaurant_id) VALUES (?, ?)";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setInt(1, menu.getId());
            ps.setInt(2, menu.getRestaurantId());
            ps.executeUpdate();
            AuditService.getInstance().record("create_menu:" + menu.getId());
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Menu findById(int id) {
        String sql = "SELECT id, restaurant_id FROM menus WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Menu menu = new Menu(rs.getInt(1), rs.getInt(2));
                loadItems(c, menu);
                return menu;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Menu> listAll() {
        String sql = "SELECT id, restaurant_id FROM menus";
        List<Menu> res = new ArrayList<>();
        try (Connection c = DatabaseManager.getInstance().getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                Menu menu = new Menu(rs.getInt(1), rs.getInt(2));
                loadItems(c, menu);
                res.add(menu);
            }
            return res;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void update(Menu menu) {
        String sql = "UPDATE menus SET restaurant_id=? WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, menu.getRestaurantId());
            ps.setInt(2, menu.getId());
            ps.executeUpdate();
            AuditService.getInstance().record("update_menu:" + menu.getId());
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void delete(int id) {
        String deleteItemsSql = "DELETE FROM menu_items WHERE menu_id=?";
        String deleteMenuSql = "DELETE FROM menus WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement deleteItems = c.prepareStatement(deleteItemsSql);
             PreparedStatement deleteMenu = c.prepareStatement(deleteMenuSql)) {
            deleteItems.setInt(1, id);
            deleteItems.executeUpdate();
            deleteMenu.setInt(1, id);
            deleteMenu.executeUpdate();
            AuditService.getInstance().record("delete_menu:" + id);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private void loadItems(Connection c, Menu menu) throws SQLException {
        String sql = "SELECT id, name, price FROM menu_items WHERE menu_id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, menu.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MenuItem item = new MenuItem(rs.getInt(1), rs.getString(2), rs.getDouble(3));
                    menu.addItem(item);
                }
            }
        }
    }

}