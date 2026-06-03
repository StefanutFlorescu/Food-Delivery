package org.example.service;

import org.example.model.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.example.dao.OrderJdbcService;
import org.example.dao.UserJdbcService;
import org.example.dao.RestaurantJdbcService;
import org.example.dao.DriverJdbcService;

public class InMemoryRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Restaurant> restaurants = new HashMap<>();
    // sorted collection of restaurants by name
    private final NavigableSet<Restaurant> restaurantsByName = new TreeSet<>(Comparator.comparing(Restaurant::getName));
    private final Map<Integer, Menu> menus = new HashMap<>();
    private final Map<Integer, Review> reviews = new LinkedHashMap<>();
    private final Map<Integer, Driver> drivers = new HashMap<>();
    private final Map<Integer, Order> orders = new LinkedHashMap<>();

    private final AtomicInteger userIdSeq = new AtomicInteger(1);
    private final AtomicInteger restaurantIdSeq = new AtomicInteger(1);
    private final AtomicInteger menuIdSeq = new AtomicInteger(1);
    private final AtomicInteger menuItemIdSeq = new AtomicInteger(1);
    private final AtomicInteger reviewIdSeq = new AtomicInteger(1);
    private final AtomicInteger driverIdSeq = new AtomicInteger(1);
    private final AtomicInteger orderIdSeq = new AtomicInteger(1);

    public User addUser(String name, String address, String email, String phoneNumber) {
        int id = userIdSeq.getAndIncrement();
        User u = new User(id, name, address, email, phoneNumber);
        users.put(id, u);
        return u;
    }

    public Restaurant addRestaurant(String name, String address) {
        int id = restaurantIdSeq.getAndIncrement();
        Restaurant r = new Restaurant(id, name, address);
        restaurants.put(id, r);
        restaurantsByName.add(r);
        return r;
    }

    public Driver addDriver(String name) {
        int id = driverIdSeq.getAndIncrement();
        Driver d = new Driver(id, name);
        drivers.put(id, d);
        return d;
    }

    public ExpressDriver addExpressDriver(String name, String vehicleType, double speedMultiplier) {
        int id = driverIdSeq.getAndIncrement();
        ExpressDriver d = new ExpressDriver(id, name, vehicleType, speedMultiplier);
        drivers.put(id, d);
        return d;
    }

    public PremiumUser addPremiumUser(String name, String address, String email, String phoneNumber, int points) {
        int id = userIdSeq.getAndIncrement();
        PremiumUser p = new PremiumUser(id, name, address, email, phoneNumber, points);
        users.put(id, p);
        return p;
    }

    public StudentUser addStudentUser(String name, String address, String email, String phoneNumber, String university, int discountPercent) {
        int id = userIdSeq.getAndIncrement();
        StudentUser student = new StudentUser(id, name, address, email, phoneNumber, university, discountPercent);
        users.put(id, student);
        return student;
    }

    public Menu createMenuForRestaurant(int restaurantId) {
        int id = menuIdSeq.getAndIncrement();
        Menu m = new Menu(id, restaurantId);
        menus.put(id, m);
        return m;
    }

    public MenuItem addMenuItem(int menuId, String name, double price) {
        Menu m = menus.get(menuId);
        if (m == null) return null;
        int id = menuItemIdSeq.getAndIncrement();
        MenuItem mi = new MenuItem(id, name, price);
        m.addItem(mi);
        return mi;
    }

    public Review addReview(int userId, int restaurantId, int rating, String comment) {
        int id = reviewIdSeq.getAndIncrement();
        Review r = new Review(id, userId, restaurantId, rating, comment);
        reviews.put(id, r);
        return r;
    }

    public Order addOrder(User user, Restaurant restaurant, String items) {
        int id = orderIdSeq.getAndIncrement();
        Order o = new Order(id, user, restaurant, items);
        orders.put(id, o);
        return o;
    }

    public Collection<User> getAllUsers() { return users.values(); }
    public Collection<Restaurant> getAllRestaurants() { return restaurants.values(); }
    public Collection<Restaurant> getRestaurantsSortedByName() { return Collections.unmodifiableCollection(restaurantsByName); }
    public Collection<Menu> getAllMenus() { return menus.values(); }
    public Collection<Review> getAllReviews() { return reviews.values(); }
    public Collection<Driver> getAllDrivers() { return drivers.values(); }
    public Collection<Order> getAllOrders() { return orders.values(); }

    public Optional<User> findUser(int id) { return Optional.ofNullable(users.get(id)); }
    public Optional<Restaurant> findRestaurant(int id) { return Optional.ofNullable(restaurants.get(id)); }
    public Optional<Driver> findDriver(int id) { return Optional.ofNullable(drivers.get(id)); }
    public Optional<Order> findOrder(int id) { return Optional.ofNullable(orders.get(id)); }

    // load persisted entities from DB into in-memory collections
    public void loadFromDb() {
        // users
        List<User> dbUsers = UserJdbcService.getInstance().listAll();
        int maxUid = 0;
        for (User u : dbUsers) {
            users.put(u.getId(), u);
            if (u.getId() > maxUid) maxUid = u.getId();
        }
        userIdSeq.set(Math.max(userIdSeq.get(), maxUid + 1));

        // restaurants
        List<Restaurant> dbR = RestaurantJdbcService.getInstance().listAll();
        int maxRid = 0;
        for (Restaurant r : dbR) {
            restaurants.put(r.getId(), r);
            restaurantsByName.add(r);
            if (r.getId() > maxRid) maxRid = r.getId();
        }
        restaurantIdSeq.set(Math.max(restaurantIdSeq.get(), maxRid + 1));

        // drivers
        List<Driver> dbD = DriverJdbcService.getInstance().listAll();
        int maxDid = 0;
        for (Driver d : dbD) {
            drivers.put(d.getId(), d);
            if (d.getId() > maxDid) maxDid = d.getId();
        }
        driverIdSeq.set(Math.max(driverIdSeq.get(), maxDid + 1));

        // orders
        List<Order> dbOrders = OrderJdbcService.getInstance().listAll(users.values(), restaurants.values(), drivers.values());
        int maxOid = 0;
        for (Order order : dbOrders) {
            orders.put(order.getId(), order);
            if (order.getId() > maxOid) maxOid = order.getId();
        }
        orderIdSeq.set(Math.max(orderIdSeq.get(), maxOid + 1));
    }
}
