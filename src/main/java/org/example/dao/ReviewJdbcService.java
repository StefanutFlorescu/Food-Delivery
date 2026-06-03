package org.example.dao;

import org.example.db.AuditService;
import org.example.db.DatabaseManager;
import org.example.model.Review;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewJdbcService {
    private static final ReviewJdbcService INSTANCE = new ReviewJdbcService();

    private ReviewJdbcService() {}

    public static ReviewJdbcService getInstance() {
        return INSTANCE;
    }

    public void create(Review review) {
        String sql = "INSERT INTO reviews(id,user_id,restaurant_id,rating,comment) VALUES(?,?,?,?,?)";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, review.getId());
            ps.setInt(2, review.getUserId());
            ps.setInt(3, review.getRestaurantId());
            ps.setInt(4, review.getRating());
            ps.setString(5, review.getComment());
            ps.executeUpdate();
            AuditService.getInstance().record("create_review:" + review.getId());
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Review findById(int id) {
        String sql = "SELECT id,user_id,restaurant_id,rating,comment FROM reviews WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Review(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getString(5));
                }
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Review> listAll() {
        String sql = "SELECT id,user_id,restaurant_id,rating,comment FROM reviews";
        List<Review> res = new ArrayList<>();
        try (Connection c = DatabaseManager.getInstance().getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                res.add(new Review(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getString(5)));
            }
            return res;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Review> listByRestaurantId(int restaurantId) {
        String sql = "SELECT id,user_id,restaurant_id,rating,comment FROM reviews WHERE restaurant_id=?";
        List<Review> res = new ArrayList<>();
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(new Review(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getString(5)));
                }
                return res;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void update(Review review) {
        String sql = "UPDATE reviews SET rating=?,comment=? WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, review.getRating());
            ps.setString(2, review.getComment());
            ps.setInt(3, review.getId());
            ps.executeUpdate();
            AuditService.getInstance().record("update_review:" + review.getId());
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void delete(int id) {
        String sql = "DELETE FROM reviews WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            AuditService.getInstance().record("delete_review:" + id);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}

