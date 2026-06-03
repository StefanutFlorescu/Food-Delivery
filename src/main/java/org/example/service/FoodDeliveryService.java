package org.example.service;


import org.example.db.AuditService;
import org.example.dao.DriverJdbcService;
import org.example.dao.MenuItemJdbcService;
import org.example.dao.MenuJdbcService;
import org.example.dao.OrderJdbcService;
import org.example.dao.RestaurantJdbcService;
import org.example.dao.ReviewJdbcService;
import org.example.dao.UserJdbcService;
import org.example.model.*;
import java.util.*;

public class FoodDeliveryService {
    private final InMemoryRepository repo;

    public FoodDeliveryService(InMemoryRepository repo) {
        this.repo = repo;
    }

    private void audit(String action) {
        AuditService.getInstance().record(action);
    }

    public User createUser(String name, String address, String email, String phoneNumber) {
        User aux = repo.addUser(name, address, email, phoneNumber);
        persistUserToDb(aux);
        audit("create_user");
        return aux;
    }
    public Restaurant createRestaurant(String name, String address) {
        Restaurant aux = repo.addRestaurant(name, address);
        persistRestaurantToDb(aux);
        audit("create_restaurant");
        return aux;
    }
    public Driver createDriver(String name) {
        Driver aux = repo.addDriver(name);
        persistDriverToDb(aux);
        audit("create_driver");
        return aux;
    }
    public ExpressDriver createExpressDriver(String name, String vehicleType, double priceMultiplier) {
        ExpressDriver aux = repo.addExpressDriver(name, vehicleType, priceMultiplier);
        persistDriverToDb(aux);
        audit("create_express_driver");
        return aux;
    }

    public PremiumUser createPremiumUser(String name, String address, String email, String phoneNumber, int points) {
        PremiumUser aux = repo.addPremiumUser(name, address, email, phoneNumber, points);
        persistUserToDb(aux);
        audit("create_premium_user");
        return aux;
    }
    public StudentUser createStudentUser(String name, String address, String email, String phoneNumber, String university, double discountPercent) {
        StudentUser aux = repo.addStudentUser(name, address, email, phoneNumber, university, discountPercent);
        persistUserToDb(aux);
        audit("create_student_user");
        return aux;
    }

    public Menu createMenuForRestaurant(int restaurantId) {
        Menu menu = repo.createMenuForRestaurant(restaurantId);
        persistMenuToDb(menu);
        audit("create_menu");
        return menu;
    }

    public MenuItem addMenuItem(int menuId, String name, double price) {
        MenuItem item = repo.addMenuItem(menuId, name, price);
        if (item != null) {
            persistMenuItemToDb(menuId, item);
            audit("add_menu_item");
        }
        return item;
    }

    public Review addReview(int userId, int restaurantId, int rating, String comment) {
        Review review = repo.addReview(userId, restaurantId, rating, comment);
        persistReviewToDb(review);
        audit("add_review");
        return review;
    }

    public Collection<Restaurant> listRestaurantsSorted() {
        audit("list_restaurants_sorted");
        return repo.getRestaurantsSortedByName();
    }
    public Collection<Menu> listMenus() {
        audit("list_menus");
        return repo.getAllMenus();
    }
    public Collection<Review> listReviews() {
        audit("list_reviews");
        return repo.getAllReviews();
    }

    public Order placeOrder(int userId, int restaurantId, String items, double deliveryFee) {
        User u = repo.findUser(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Restaurant r = repo.findRestaurant(restaurantId).orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        Order o = repo.addOrder(u, r, items, deliveryFee);
        if (u instanceof PremiumUser premiumUser) {
            premiumUser.setLoyaltyPoints(premiumUser.getLoyaltyPoints() + 1);
            UserJdbcService.getInstance().update(premiumUser);
        }
        persistOrderToDb(o);
        audit("place_order");
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
        audit("assign_driver");
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
        audit("complete_order");
        return true;
    }

    public Collection<User> listUsers() {
        audit("list_users");
        return repo.getAllUsers();
    }
    public Collection<Restaurant> listRestaurants() {
        audit("list_restaurants");
        return repo.getAllRestaurants();
    }
    public Collection<Driver> listDrivers() {
        audit("list_drivers");
        return repo.getAllDrivers();
    }
    public Collection<Order> listOrders() {
        audit("list_orders");
        return repo.getAllOrders();
    }

    // JDBC-backed CRUD helpers
    public void persistUserToDb(User u) { UserJdbcService.getInstance().create(u); }
    public void persistRestaurantToDb(Restaurant r) { RestaurantJdbcService.getInstance().create(r); }
    public void persistDriverToDb(Driver d) { DriverJdbcService.getInstance().create(d); }
    public void persistMenuToDb(Menu m) { MenuJdbcService.getInstance().create(m); }
    public void persistMenuItemToDb(int menuId, MenuItem item) { MenuItemJdbcService.getInstance().create(menuId, item); }
    public void persistReviewToDb(Review review) { ReviewJdbcService.getInstance().create(review); }
    public void persistOrderToDb(Order o) { OrderJdbcService.getInstance().create(o); }
}
