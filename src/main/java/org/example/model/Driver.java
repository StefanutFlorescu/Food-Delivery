package org.example.model;

public class Driver {
    private final int id;
    private String name;
    private boolean available;

    public Driver(int id, String name) {
        this.id = id;
        this.name = name;
        this.available = true;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    protected String describeDriverFields() {
        return "id=" + id + ", name='" + name + '\'' + ", available=" + available;
    }

    @Override
    public String toString() {
        return "Driver{" + describeDriverFields() + '}';
    }
}
