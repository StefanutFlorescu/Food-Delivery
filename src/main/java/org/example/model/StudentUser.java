package org.example.model;

public class StudentUser extends User {
    private String university;
    private int discountPercent;

    public StudentUser(int id, String name, String address, String email, String phoneNumber, String university, int discountPercent) {
        super(id, name, address, email, phoneNumber);
        this.university = university;
        this.discountPercent = discountPercent;
    }

    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }

    @Override
    public String toString() {
        return "StudentUser{" + describeCoreFields() + ", university='" + university + '\''
                + ", discountPercent=" + discountPercent + '}';
    }
}