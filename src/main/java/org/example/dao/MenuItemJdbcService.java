package org.example.dao;

import org.example.db.DatabaseManager;
import org.example.model.MenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemJdbcService extends GenericJdbcService<MenuItem> {
    private static final MenuItemJdbcService INSTANCE = new MenuItemJdbcService();

    private MenuItemJdbcService() {}

    public static MenuItemJdbcService getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO menu_items(id,menu_id,name,price) VALUES(?,?,?,?)";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT id,name,price FROM menu_items WHERE id=?";
    }

    @Override
    protected String getListAllSql() {
        return "SELECT id,name,price FROM menu_items";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE menu_items SET name=?,price=? WHERE id=?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM menu_items WHERE id=?";
    }

    @Override
    protected void setCreateParams(PreparedStatement ps, MenuItem item) throws SQLException {
        ps.setInt(1, item.getId());
        ps.setInt(2, 0); // placeholder for menuId, handled separately
        ps.setString(3, item.getName());
        ps.setDouble(4, item.getPrice());
    }

    @Override
    protected void setFindByIdParams(PreparedStatement ps, int id) throws SQLException {
        ps.setInt(1, id);
    }

    @Override
    protected MenuItem mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new MenuItem(rs.getInt(1), rs.getString(2), rs.getDouble(3));
    }

    @Override
    protected void setUpdateParams(PreparedStatement ps, MenuItem item) throws SQLException {
        ps.setString(1, item.getName());
        ps.setDouble(2, item.getPrice());
        ps.setInt(3, item.getId());
    }

    @Override
    protected void setDeleteParams(PreparedStatement ps, int id) throws SQLException {
        ps.setInt(1, id);
    }

    @Override
    protected String getCreateAuditAction(MenuItem item) {
        return "create_menu_item:" + item.getId();
    }

    @Override
    protected String getUpdateAuditAction(MenuItem item) {
        return "update_menu_item:" + item.getId();
    }

    @Override
    protected String getDeleteAuditAction(int id) {
        return "delete_menu_item:" + id;
    }

    /**
     * Custom create for MenuItem with menuId parameter (bypasses generic).
     */
    public void create(int menuId, MenuItem item) {
        String sql = "INSERT INTO menu_items(id,menu_id,name,price) VALUES(?,?,?,?)";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, item.getId());
            ps.setInt(2, menuId);
            ps.setString(3, item.getName());
            ps.setDouble(4, item.getPrice());
            ps.executeUpdate();
            org.example.db.AuditService.getInstance().record("create_menu_item:" + item.getId());
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<MenuItem> listByMenuId(int menuId) {
        String sql = "SELECT id,name,price FROM menu_items WHERE menu_id=?";
        List<MenuItem> res = new ArrayList<>();
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, menuId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(mapResultSetToEntity(rs));
                }
                return res;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}

