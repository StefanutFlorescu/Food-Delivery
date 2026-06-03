package org.example.model;

public class StudentUser extends User {
    private String university;
    private double gpa = 5.0;

    public StudentUser(int id, String name, String address, String email, String phoneNumber, String university, double discountPercent) {
        super(id, name, address, email, phoneNumber);
        this.university = university;
        this.discountPercent = discountPercent;
    }

    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public void setGpa(double gpa) { this.gpa = gpa; }
    public double getGpa() { return gpa; }

    @Override
    public double getDiscountPercentOverall() {
        double adjusted = discountPercent * gpa / 5.0;
        return Math.max(0.0, Math.min(100.0, adjusted));
    }

    @Override
    public String toString() {
        return "StudentUser{" + describeCoreFields() + ", university='" + university + '\''
                + ", discountPercent=" + discountPercent + '}';
    }
}