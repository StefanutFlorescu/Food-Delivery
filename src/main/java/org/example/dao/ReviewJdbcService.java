package org.example.dao;

import org.example.db.DatabaseManager;
import org.example.model.Review;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewJdbcService extends GenericJdbcService<Review> {
    private static final ReviewJdbcService INSTANCE = new ReviewJdbcService();

    private ReviewJdbcService() {}

    public static ReviewJdbcService getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO reviews(id,user_id,restaurant_id,rating,comment) VALUES(?,?,?,?,?)";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT id,user_id,restaurant_id,rating,comment FROM reviews WHERE id=?";
    }

    @Override
    protected String getListAllSql() {
        return "SELECT id,user_id,restaurant_id,rating,comment FROM reviews";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE reviews SET rating=?,comment=? WHERE id=?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM reviews WHERE id=?";
    }

    @Override
    protected void setCreateParams(PreparedStatement ps, Review review) throws SQLException {
        ps.setInt(1, review.getId());
        ps.setInt(2, review.getUserId());
        ps.setInt(3, review.getRestaurantId());
        ps.setInt(4, review.getRating());
        ps.setString(5, review.getComment());
    }

    @Override
    protected void setFindByIdParams(PreparedStatement ps, int id) throws SQLException {
        ps.setInt(1, id);
    }

    @Override
    protected Review mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Review(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getString(5));
    }

    @Override
    protected void setUpdateParams(PreparedStatement ps, Review review) throws SQLException {
        ps.setInt(1, review.getRating());
        ps.setString(2, review.getComment());
        ps.setInt(3, review.getId());
    }

    @Override
    protected void setDeleteParams(PreparedStatement ps, int id) throws SQLException {
        ps.setInt(1, id);
    }

    @Override
    protected String getCreateAuditAction(Review review) {
        return "create_review:" + review.getId();
    }

    @Override
    protected String getUpdateAuditAction(Review review) {
        return "update_review:" + review.getId();
    }

    @Override
    protected String getDeleteAuditAction(int id) {
        return "delete_review:" + id;
    }

    public List<Review> listByRestaurantId(int restaurantId) {
        String sql = "SELECT id,user_id,restaurant_id,rating,comment FROM reviews WHERE restaurant_id=?";
        List<Review> res = new ArrayList<>();
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(mapResultSetToEntity(rs));
                }
                return res;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}

