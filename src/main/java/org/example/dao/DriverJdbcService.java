package org.example.dao;

import org.example.db.AuditService;
import org.example.db.DatabaseManager;
import org.example.model.Driver;
import org.example.model.ExpressDriver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverJdbcService {
    private static final DriverJdbcService INSTANCE = new DriverJdbcService();

    private DriverJdbcService() {}

    public static DriverJdbcService getInstance() { return INSTANCE; }

    public void create(Driver d) {
        String sql = "INSERT INTO drivers(id,name,available,driver_type,vehicle_type,price_multiplier) VALUES(?,?,?,?,?,?)";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            boolean isExpress = d instanceof ExpressDriver;
            ps.setInt(1, d.getId());
            ps.setString(2, d.getName());
            ps.setBoolean(3, d.isAvailable());
            ps.setString(4, isExpress ? "EXPRESS" : "STANDARD");
            if (isExpress) {
                ExpressDriver expressDriver = (ExpressDriver) d;
                ps.setString(5, expressDriver.getVehicleType());
                ps.setDouble(6, expressDriver.getPriceMultiplier());
            } else {
                ps.setNull(5, Types.VARCHAR);
                ps.setNull(6, Types.DOUBLE);
            }
            ps.executeUpdate();
            AuditService.getInstance().record("create_driver:" + d.getId());
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Driver findById(int id) {
        String sql = "SELECT id,name,available,driver_type,vehicle_type,price_multiplier FROM drivers WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Driver driver;
                    if ("EXPRESS".equals(rs.getString(4))) {
                        driver = new ExpressDriver(rs.getInt(1), rs.getString(2), rs.getString(5), rs.getDouble(6));
                    } else {
                        driver = new Driver(rs.getInt(1), rs.getString(2));
                    }
                    driver.setAvailable(rs.getBoolean(3));
                    return driver;
                }
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Driver> listAll() {
        String sql = "SELECT id,name,available,driver_type,vehicle_type,price_multiplier FROM drivers";
        List<Driver> res = new ArrayList<>();
        try (Connection c = DatabaseManager.getInstance().getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                Driver d;
                if ("EXPRESS".equals(rs.getString(4))) {
                    d = new ExpressDriver(rs.getInt(1), rs.getString(2), rs.getString(5), rs.getDouble(6));
                } else {
                    d = new Driver(rs.getInt(1), rs.getString(2));
                }
                d.setAvailable(rs.getBoolean(3));
                res.add(d);
            }
            return res;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void update(Driver d) {
        String sql = "UPDATE drivers SET name=?,available=?,driver_type=?,vehicle_type=?,price_multiplier=? WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            boolean isExpress = d instanceof ExpressDriver;
            ps.setString(1, d.getName());
            ps.setBoolean(2, d.isAvailable());
            ps.setString(3, isExpress ? "EXPRESS" : "STANDARD");
            if (isExpress) {
                ExpressDriver expressDriver = (ExpressDriver) d;
                ps.setString(4, expressDriver.getVehicleType());
                ps.setDouble(5, expressDriver.getPriceMultiplier());
            } else {
                ps.setNull(4, Types.VARCHAR);
                ps.setNull(5, Types.DOUBLE);
            }
            ps.setInt(6, d.getId());
            ps.executeUpdate();
            AuditService.getInstance().record("update_driver:" + d.getId());
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void delete(int id) {
        String sql = "DELETE FROM drivers WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            AuditService.getInstance().record("delete_driver:" + id);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
