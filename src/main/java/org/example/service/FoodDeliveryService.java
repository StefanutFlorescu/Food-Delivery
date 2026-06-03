package org.example.service;


import org.example.dao.DriverJdbcService;
import org.example.dao.OrderJdbcService;
import org.example.dao.RestaurantJdbcService;
import org.example.dao.OrderJdbcService;
import org.example.dao.UserJdbcService;
import org.example.model.*;
import java.util.*;

public class FoodDeliveryService {
    private final InMemoryRepository repo;

    public FoodDeliveryService(InMemoryRepository repo) {
        this.repo = repo;
    }

    public User createUser(String name, String address, String email, String phoneNumber) {
        User aux = repo.addUser(name, address, email, phoneNumber);
        persistUserToDb(aux);
        return aux;
    }
    public Restaurant createRestaurant(String name, String address) {
        Restaurant aux = repo.addRestaurant(name, address);
        persistRestaurantToDb(aux);
        return aux;
    }
    public Driver createDriver(String name) {
        Driver aux = repo.addDriver(name);
        persistDriverToDb(aux);
        return aux;
    }
    public ExpressDriver createExpressDriver(String name, String vehicleType, double speedMultiplier) {
        ExpressDriver aux = repo.addExpressDriver(name, vehicleType, speedMultiplier);
        persistDriverToDb(aux);
        return aux;
    }

    public PremiumUser createPremiumUser(String name, String address, String email, String phoneNumber, int points) {
        PremiumUser aux = repo.addPremiumUser(name, address, email, phoneNumber, points);
        persistUserToDb(aux);
        return aux;
    }
    public StudentUser createStudentUser(String name, String address, String email, String phoneNumber, String university, int discountPercent) {
        StudentUser aux = repo.addStudentUser(name, address, email, phoneNumber, university, discountPercent);
        persistUserToDb(aux);
        return aux;
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
        Order o = repo.addOrder(u, r, items);
        persistOrderToDb(o);
        return o;
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
    public void persistUserToDb(User u) { UserJdbcService.getInstance().create(u); }
    public void persistRestaurantToDb(Restaurant r) { RestaurantJdbcService.getInstance().create(r); }
    public void persistDriverToDb(Driver d) { DriverJdbcService.getInstance().create(d); }
    public void persistOrderToDb(Order o) { OrderJdbcService.getInstance().create(o); }
}
