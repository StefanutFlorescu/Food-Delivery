package org.example.dao;

import org.example.model.Restaurant;

import java.sql.*;
import java.util.List;

public class RestaurantJdbcService extends GenericJdbcService<Restaurant> {
    private static final RestaurantJdbcService INSTANCE = new RestaurantJdbcService();

    private RestaurantJdbcService() {}

    public static RestaurantJdbcService getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO restaurants(id,name,address) VALUES(?,?,?)";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT id,name,address FROM restaurants WHERE id=?";
    }

    @Override
    protected String getListAllSql() {
        return "SELECT id,name,address FROM restaurants";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE restaurants SET name=?,address=? WHERE id=?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM restaurants WHERE id=?";
    }

    @Override
    protected void setCreateParams(PreparedStatement ps, Restaurant r) throws SQLException {
        ps.setInt(1, r.getId());
        ps.setString(2, r.getName());
        ps.setString(3, r.getAddress());
    }

    @Override
    protected void setFindByIdParams(PreparedStatement ps, int id) throws SQLException {
        ps.setInt(1, id);
    }

    @Override
    protected Restaurant mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Restaurant(rs.getInt(1), rs.getString(2), rs.getString(3));
    }

    @Override
    protected void setUpdateParams(PreparedStatement ps, Restaurant r) throws SQLException {
        ps.setString(1, r.getName());
        ps.setString(2, r.getAddress());
        ps.setInt(3, r.getId());
    }

    @Override
    protected void setDeleteParams(PreparedStatement ps, int id) throws SQLException {
        ps.setInt(1, id);
    }

    @Override
    protected String getCreateAuditAction(Restaurant r) {
        return "create_restaurant:" + r.getId();
    }

    @Override
    protected String getUpdateAuditAction(Restaurant r) {
        return "update_restaurant:" + r.getId();
    }

    @Override
    protected String getDeleteAuditAction(int id) {
        return "delete_restaurant:" + id;
    }
}
