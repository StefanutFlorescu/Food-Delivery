package org.example.dao;

import org.example.db.AuditService;
import org.example.db.DatabaseManager;
import org.example.model.PremiumUser;
import org.example.model.StudentUser;
import org.example.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserJdbcService {
    private static final UserJdbcService INSTANCE = new UserJdbcService();

    private UserJdbcService() {}

    public static UserJdbcService getInstance() { return INSTANCE; }

    public void create(User u) {
        String sql = "INSERT INTO users(id,name,address,premium,loyalty,email,phone_number,user_type,university,student_discount) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            boolean isPremium = u instanceof PremiumUser;
            int loyaltyPoints = isPremium ? ((PremiumUser) u).getLoyaltyPoints() : 0;
            boolean isStudent = u instanceof StudentUser;
            String userType = isStudent ? "STUDENT" : (isPremium ? "PREMIUM" : "STANDARD");
            ps.setInt(1, u.getId());
            ps.setString(2, u.getName());
            ps.setString(3, u.getAddress());
            ps.setBoolean(4, isPremium);
            ps.setInt(5, loyaltyPoints);
            ps.setString(6, u.getEmail());
            ps.setString(7, u.getPhoneNumber());
            ps.setString(8, userType);
            if (isStudent) {
                StudentUser studentUser = (StudentUser) u;
                ps.setString(9, studentUser.getUniversity());
                ps.setDouble(10, studentUser.getDiscountPercent());
            } else {
                ps.setNull(9, Types.VARCHAR);
                ps.setNull(10, Types.DOUBLE);
            }
            ps.executeUpdate();
            AuditService.getInstance().record("create_user:" + u.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findById(int id) {
        String sql = "SELECT id,name,address,premium,loyalty,email,phone_number,user_type,university,student_discount FROM users WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String userType = rs.getString(8);
                    if ("STUDENT".equals(userType)) {
                        return new StudentUser(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(6), rs.getString(7), rs.getString(9), rs.getDouble(10));
                    }
                    if (rs.getBoolean(4) || "PREMIUM".equals(userType)) {
                        return new PremiumUser(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(6), rs.getString(7), rs.getInt(5));
                    }
                    return new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(6), rs.getString(7));
                }
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<User> listAll() {
        String sql = "SELECT id,name,address,premium,loyalty,email,phone_number,user_type,university,student_discount FROM users";
        List<User> res = new ArrayList<>();
        try (Connection c = DatabaseManager.getInstance().getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                String userType = rs.getString(8);
                if ("STUDENT".equals(userType)) {
                    res.add(new StudentUser(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(6), rs.getString(7), rs.getString(9), rs.getDouble(10)));
                } else if (rs.getBoolean(4) || "PREMIUM".equals(userType)) {
                    res.add(new PremiumUser(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(6), rs.getString(7), rs.getInt(5)));
                } else {
                    res.add(new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(6), rs.getString(7)));
                }
            }
            return res;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void update(User u) {
        String sql = "UPDATE users SET name=?,address=?,premium=?,loyalty=?,email=?,phone_number=?,user_type=?,university=?,student_discount=? WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            boolean isPremium = u instanceof PremiumUser;
            int loyaltyPoints = isPremium ? ((PremiumUser) u).getLoyaltyPoints() : 0;
            boolean isStudent = u instanceof StudentUser;
            String userType = isStudent ? "STUDENT" : (isPremium ? "PREMIUM" : "STANDARD");
            ps.setString(1, u.getName());
            ps.setString(2, u.getAddress());
            ps.setBoolean(3, isPremium);
            ps.setInt(4, loyaltyPoints);
            ps.setString(5, u.getEmail());
            ps.setString(6, u.getPhoneNumber());
            ps.setString(7, userType);
            if (isStudent) {
                StudentUser studentUser = (StudentUser) u;
                ps.setString(8, studentUser.getUniversity());
                ps.setDouble(9, studentUser.getDiscountPercent());
            } else {
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.DOUBLE);
            }
            ps.setInt(10, u.getId());
            ps.executeUpdate();
            AuditService.getInstance().record("update_user:" + u.getId());
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            AuditService.getInstance().record("delete_user:" + id);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
