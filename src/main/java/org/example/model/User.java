package org.example.model;

public class User {
    private final int id;
    private String name;
    private String address;
    private String email;
    private String phoneNumber;
    protected double discountPercent;

    public User(int id, String name, String address) {
        this(id, name, address, null, null);
    }

    public User(int id, String name, String address, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }

    protected String describeCoreFields() {
        return "id=" + id + ", name='" + name + '\'' + ", address='" + address + '\''
                + ", email='" + email + '\'' + ", phoneNumber='" + phoneNumber + '\'';
    }

    public double getDiscountPercentOverall() {
        return Math.max(0.0, Math.min(100.0, discountPercent));
    }

    @Override
    public String toString() {
        return "User{" + describeCoreFields() + '}';
    }
}
