package org.example.dao;

import org.example.db.AuditService;
import org.example.db.DatabaseManager;
import org.example.model.Driver;
import org.example.model.Order;
import org.example.model.OrderStatus;
import org.example.model.Restaurant;
import org.example.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OrderJdbcService {
    private static final OrderJdbcService INSTANCE = new OrderJdbcService();

    private OrderJdbcService() {}

    public static OrderJdbcService getInstance() { return INSTANCE; }

    public void create(Order o) {
        String sql = "INSERT INTO orders(id,user_id,restaurant_id,items,driver_id,status) VALUES(?,?,?,?,?,?)";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, o.getId());
            ps.setInt(2, o.getUser().getId());
            ps.setInt(3, o.getRestaurant().getId());
            ps.setString(4, o.getItems());
            ps.setObject(5, o.getDriver()==null?null:o.getDriver().getId());
            ps.setString(6, o.getStatus().name());
            ps.executeUpdate();
            AuditService.getInstance().record("create_order:" + o.getId());
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Order findById(int id) {
        return findById(id, List.of(), List.of(), List.of());
    }

    public Order findById(int id, Collection<User> users, Collection<Restaurant> restaurants, Collection<Driver> drivers) {
        String sql = "SELECT id,user_id,restaurant_id,items,driver_id,status FROM orders WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOrder(rs, users, restaurants, drivers);
                }
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Order> listAll() {
        return listAll(List.of(), List.of(), List.of());
    }

    public List<Order> listAll(Collection<User> users, Collection<Restaurant> restaurants, Collection<Driver> drivers) {
        String sql = "SELECT id,user_id,restaurant_id,items,driver_id,status FROM orders";
        List<Order> res = new ArrayList<>();
        try (Connection c = DatabaseManager.getInstance().getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                Order order = mapOrder(rs, users, restaurants, drivers);
                if (order != null) {
                    res.add(order);
                }
            }
            return res;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Order mapOrder(ResultSet rs, Collection<User> users, Collection<Restaurant> restaurants, Collection<Driver> drivers) throws SQLException {
        Map<Integer, User> usersById = users.stream().collect(Collectors.toMap(User::getId, Function.identity(), (left, right) -> left));
        Map<Integer, Restaurant> restaurantsById = restaurants.stream().collect(Collectors.toMap(Restaurant::getId, Function.identity(), (left, right) -> left));
        Map<Integer, Driver> driversById = drivers.stream().collect(Collectors.toMap(Driver::getId, Function.identity(), (left, right) -> left));

        User user = usersById.get(rs.getInt("user_id"));
        Restaurant restaurant = restaurantsById.get(rs.getInt("restaurant_id"));
        if (user == null || restaurant == null) {
            return null;
        }

        Order order = new Order(rs.getInt("id"), user, restaurant, rs.getString("items"));
        int driverId = rs.getInt("driver_id");
        if (!rs.wasNull()) {
            order.setDriver(driversById.get(driverId));
        }
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        return order;
    }

    public void update(Order o) {
        String sql = "UPDATE orders SET items=?,driver_id=?,status=? WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, o.getItems());
            ps.setObject(2, o.getDriver()==null?null:o.getDriver().getId());
            ps.setString(3, o.getStatus().name());
            ps.setInt(4, o.getId());
            ps.executeUpdate();
            AuditService.getInstance().record("update_order:" + o.getId());
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void delete(int id) {
        String sql = "DELETE FROM orders WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            AuditService.getInstance().record("delete_order:" + id);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
