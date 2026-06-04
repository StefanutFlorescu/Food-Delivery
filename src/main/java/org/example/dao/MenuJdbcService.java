package org.example.dao;

import org.example.db.DatabaseManager;
import org.example.model.Menu;
import org.example.model.MenuItem;

import java.sql.*;
import java.util.List;

public class MenuJdbcService extends GenericJdbcService<Menu> {
    private static final MenuJdbcService INSTANCE = new MenuJdbcService();

    private MenuJdbcService() {}

    public static MenuJdbcService getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO menus(id,restaurant_id) VALUES(?,?)";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT id,restaurant_id FROM menus WHERE id=?";
    }

    @Override
    protected String getListAllSql() {
        return "SELECT id,restaurant_id FROM menus";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE menus SET restaurant_id=? WHERE id=?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM menus WHERE id=?";
    }

    @Override
    protected void setCreateParams(PreparedStatement ps, Menu menu) throws SQLException {
        ps.setInt(1, menu.getId());
        ps.setInt(2, menu.getRestaurantId());
    }

    @Override
    protected void setFindByIdParams(PreparedStatement ps, int id) throws SQLException {
        ps.setInt(1, id);
    }

    @Override
    protected Menu mapResultSetToEntity(ResultSet rs) throws SQLException {
        Menu menu = new Menu(rs.getInt(1), rs.getInt(2));
        // Hydrate menu items separately
        return menu;
    }

    @Override
    protected void setUpdateParams(PreparedStatement ps, Menu menu) throws SQLException {
        ps.setInt(1, menu.getRestaurantId());
        ps.setInt(2, menu.getId());
    }

    @Override
    protected void setDeleteParams(PreparedStatement ps, int id) throws SQLException {
        ps.setInt(1, id);
    }

    @Override
    protected String getCreateAuditAction(Menu menu) {
        return "create_menu:" + menu.getId();
    }

    @Override
    protected String getUpdateAuditAction(Menu menu) {
        return "update_menu:" + menu.getId();
    }

    @Override
    protected String getDeleteAuditAction(int id) {
        return "delete_menu:" + id;
    }

    /**
     * Override findById to hydrate menu items.
     */
    @Override
    public Menu findById(int id) {
        Menu menu = super.findById(id);
        if (menu != null) {
            loadItems(menu);
        }
        return menu;
    }

    /**
     * Override listAll to hydrate menu items.
     */
    @Override
    public List<Menu> listAll() {
        List<Menu> menus = super.listAll();
        for (Menu menu : menus) {
            loadItems(menu);
        }
        return menus;
    }

    /**
     * Override delete to cascade delete menu items.
     */
    @Override
    public void delete(int id) {
        String deleteItemsSql = "DELETE FROM menu_items WHERE menu_id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement deleteItems = c.prepareStatement(deleteItemsSql)) {
            deleteItems.setInt(1, id);
            deleteItems.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        super.delete(id);
    }

    private void loadItems(Menu menu) {
        String sql = "SELECT id,name,price FROM menu_items WHERE menu_id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, menu.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MenuItem item = new MenuItem(rs.getInt(1), rs.getString(2), rs.getDouble(3));
                    menu.addItem(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}