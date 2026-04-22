package org.example.service;

import org.example.dao.DriverJdbcService;
import org.example.dao.OrderJdbcService;
import org.example.model.*;
import java.util.*;

public class FoodDeliveryService {
    private final InMemoryRepository repo;

    public FoodDeliveryService(InMemoryRepository repo) {
        this.repo = repo;
    }

    public User createUser(String name, String address) { return repo.addUser(name, address); }
    public User createUser(String name, String address, String email, String phoneNumber) { return repo.addUser(name, address, email, phoneNumber); }
    public Restaurant createRestaurant(String name, String address) { return repo.addRestaurant(name, address); }
    public Driver createDriver(String name) { return repo.addDriver(name); }
    public ExpressDriver createExpressDriver(String name, String vehicleType, double speedMultiplier) { return repo.addExpressDriver(name, vehicleType, speedMultiplier); }
    public PremiumUser createPremiumUser(String name, String address, int points) { return repo.addPremiumUser(name, address, points); }
    public PremiumUser createPremiumUser(String name, String address, String email, String phoneNumber, int points) {
        return repo.addPremiumUser(name, address, email, phoneNumber, points);
    }
    public StudentUser createStudentUser(String name, String address, String email, String phoneNumber, String university, int discountPercent) {
        return repo.addStudentUser(name, address, email, phoneNumber, university, discountPercent);
    }

    public Menu createMenuForRestaurant(int restaurantId) { return repo.createMenuForRestaurant(restaurantId); }
    public MenuItem addMenuItem(int menuId, String name, double price) { return repo.addMenuItem(menuId, name, price); }

    public Review addReview(int userId, int restaurantId, int rating, String comment) { return repo.addReview(userId, restaurantId, rating, comment); }

    public Collection<Restaurant> listRestaurantsSorted() { return repo.getRestaurantsSortedByName(); }
    public Collection<Menu> listMenus() { return repo.getAllMenus(); }
    public Collection<Review> listReviews() { return repo.getAllReviews(); }

    public Order placeOrder(int userId, int restaurantId, String items) {
        User u = repo.findUser(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Restaurant r = repo.findRestaurant(restaurantId).orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        return repo.addOrder(u, r, items);
    }

    public boolean assignDriver(int orderId, int driverId) {
        Optional<Order> oOpt = repo.findOrder(orderId);
        Optional<Driver> dOpt = repo.findDriver(driverId);
        if (oOpt.isEmpty() || dOpt.isEmpty()) return false;
        Order o = oOpt.get();
        Driver d = dOpt.get();
        if (!d.isAvailable()) return false;
        o.setDriver(d);
        o.setStatus(OrderStatus.ASSIGNED);
        d.setAvailable(false);
        DriverJdbcService.getInstance().update(d);
        OrderJdbcService.getInstance().update(o);
        return true;
    }

    public boolean completeOrder(int orderId) {
        Optional<Order> oOpt = repo.findOrder(orderId);
        if (oOpt.isEmpty()) return false;
        Order o = oOpt.get();
        if (o.getDriver() != null) {
            o.getDriver().setAvailable(true);
            DriverJdbcService.getInstance().update(o.getDriver());
        }
        o.setStatus(OrderStatus.DELIVERED);
        OrderJdbcService.getInstance().update(o);
        return true;
    }

    public Collection<User> listUsers() { return repo.getAllUsers(); }
    public Collection<Restaurant> listRestaurants() { return repo.getAllRestaurants(); }
    public Collection<Driver> listDrivers() { return repo.getAllDrivers(); }
    public Collection<Order> listOrders() { return repo.getAllOrders(); }

    // JDBC-backed CRUD helpers
    public void persistUserToDb(User u) { org.example.dao.UserJdbcService.getInstance().create(u); }
    public void persistRestaurantToDb(Restaurant r) { org.example.dao.RestaurantJdbcService.getInstance().create(r); }
    public void persistDriverToDb(Driver d) { org.example.dao.DriverJdbcService.getInstance().create(d); }
    public void persistOrderToDb(Order o) { org.example.dao.OrderJdbcService.getInstance().create(o); }
}
