package org.example.model;

public class PremiumUser extends User {
    private int loyaltyPoints;

    public PremiumUser(int id, String name, String address, int loyaltyPoints) {
        this(id, name, address, null, null, loyaltyPoints);
    }

    public PremiumUser(int id, String name, String address, String email, String phoneNumber, int loyaltyPoints) {
        super(id, name, address, email, phoneNumber);
        this.loyaltyPoints = loyaltyPoints;
    }

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    @Override
    public String toString() {
        return "PremiumUser{" + describeCoreFields() + ", points=" + loyaltyPoints + '}';
    }
}
