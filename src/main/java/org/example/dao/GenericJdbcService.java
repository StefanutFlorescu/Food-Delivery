package org.example.dao;

import org.example.db.AuditService;
import org.example.db.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public abstract class GenericJdbcService<T> {

    protected GenericJdbcService() {}

    /**
     * Get the SQL INSERT statement for creating an entity.
     */
    protected abstract String getCreateSql();

    /**
     * Get the SQL SELECT statement for reading an entity by ID.
     */
    protected abstract String getFindByIdSql();

    /**
     * Get the SQL SELECT statement for listing all entities.
     */
    protected abstract String getListAllSql();

    /**
     * Get the SQL UPDATE statement for updating an entity.
     */
    protected abstract String getUpdateSql();

    /**
     * Get the SQL DELETE statement for deleting an entity by ID.
     */
    protected abstract String getDeleteSql();

    /**
     * Set parameters in PreparedStatement for create operation.
     */
    protected abstract void setCreateParams(PreparedStatement ps, T entity) throws SQLException;

    /**
     * Set parameters in PreparedStatement for find by ID operation.
     */
    protected abstract void setFindByIdParams(PreparedStatement ps, int id) throws SQLException;

    /**
     * Map ResultSet row to entity object.
     */
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;

    /**
     * Set parameters in PreparedStatement for update operation.
     */
    protected abstract void setUpdateParams(PreparedStatement ps, T entity) throws SQLException;

    /**
     * Set parameters in PreparedStatement for delete operation.
     */
    protected abstract void setDeleteParams(PreparedStatement ps, int id) throws SQLException;

    /**
     * Get audit action name for create.
     */
    protected abstract String getCreateAuditAction(T entity);

    /**
     * Get audit action name for update.
     */
    protected abstract String getUpdateAuditAction(T entity);

    /**
     * Get audit action name for delete.
     */
    protected abstract String getDeleteAuditAction(int id);

    /**
     * Create (INSERT) an entity into the database.
     */
    public void create(T entity) {
        String sql = getCreateSql();
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            setCreateParams(ps, entity);
            ps.executeUpdate();
            AuditService.getInstance().record(getCreateAuditAction(entity));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read (SELECT) an entity by ID from the database.
     */
    public T findById(int id) {
        String sql = getFindByIdSql();
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            setFindByIdParams(ps, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * List (SELECT ALL) entities from the database.
     */
    public List<T> listAll() {
        String sql = getListAllSql();
        List<T> res = new ArrayList<>();
        try (Connection c = DatabaseManager.getInstance().getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                res.add(mapResultSetToEntity(rs));
            }
            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update an entity in the database.
     */
    public void update(T entity) {
        String sql = getUpdateSql();
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            setUpdateParams(ps, entity);
            ps.executeUpdate();
            AuditService.getInstance().record(getUpdateAuditAction(entity));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete an entity from the database by ID.
     */
    public void delete(int id) {
        String sql = getDeleteSql();
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            setDeleteParams(ps, id);
            ps.executeUpdate();
            AuditService.getInstance().record(getDeleteAuditAction(id));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

