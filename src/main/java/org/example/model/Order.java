package org.example.model;

public class Order {
    private final int id;
    private final User user;
    private final Restaurant restaurant;
    private String items;
    private double deliveryFee;
    private Driver driver;
    private OrderStatus status;

    public Order(int id, User user, Restaurant restaurant, String items) {
        this(id, user, restaurant, items, 0.0);
    }

    public Order(int id, User user, Restaurant restaurant, String items, double deliveryFee) {
        this.id = id;
        this.user = user;
        this.restaurant = restaurant;
        this.items = items;
        this.deliveryFee = deliveryFee;
        this.status = OrderStatus.NEW;
    }

    public int getId() { return id; }
    public User getUser() { return user; }
    public Restaurant getRestaurant() { return restaurant; }
    public String getItems() { return items; }
    public void setItems(String items) { this.items = items; }
    public double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }
    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public double getFinalBill() {
        double total = Math.max(0.0, deliveryFee);
        if (driver instanceof ExpressDriver expressDriver) {
            total *= Math.max(0.0, expressDriver.getPriceMultiplier());
        }
        double discountPercent = user.getDiscountPercentOverall();
        total *= (100.0 - discountPercent) / 100.0;
        return total;
    }

    @Override
    public String toString() {
        return "Order{" + "id=" + id + ", user=" + user.getName() + ", restaurant=" + restaurant.getName() + ", items='" + items + '\''
                + ", deliveryFee=" + deliveryFee + ", finalBill=" + getFinalBill()
                + ", driver=" + (driver==null?"none":driver.getName()) + ", status=" + status + '}';
    }
}
