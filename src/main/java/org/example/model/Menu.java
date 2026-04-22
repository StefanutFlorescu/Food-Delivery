package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private final int id;
    private final int restaurantId;
    private final List<MenuItem> items = new ArrayList<>();

    public Menu(int id, int restaurantId) {
        this.id = id;
        this.restaurantId = restaurantId;
    }

    public int getId() { return id; }
    public int getRestaurantId() { return restaurantId; }
    public List<MenuItem> getItems() { return items; }

    public void addItem(MenuItem item) { items.add(item); }

    @Override
    public String toString() {
        return "Menu{" + "id=" + id + ", restaurantId=" + restaurantId + ", items=" + items + '}';
    }
}
