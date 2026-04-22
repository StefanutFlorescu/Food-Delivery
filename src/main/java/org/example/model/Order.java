package org.example.model;

public class Order {
    private final int id;
    private final User user;
    private final Restaurant restaurant;
    private String items; // simple description
    private Driver driver; // nullable
    private OrderStatus status;

    public Order(int id, User user, Restaurant restaurant, String items) {
        this.id = id;
        this.user = user;
        this.restaurant = restaurant;
        this.items = items;
        this.status = OrderStatus.NEW;
    }

    public int getId() { return id; }
    public User getUser() { return user; }
    public Restaurant getRestaurant() { return restaurant; }
    public String getItems() { return items; }
    public void setItems(String items) { this.items = items; }
    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Order{" + "id=" + id + ", user=" + user.getName() + ", restaurant=" + restaurant.getName() + ", items='" + items + '\'' + ", driver=" + (driver==null?"none":driver.getName()) + ", status=" + status + '}';
    }
}
