package org.example.model;

public class Review {
    private final int id;
    private final int userId;
    private final int restaurantId;
    private int rating; // 1-5
    private String comment;

    public Review(int id, int userId, int restaurantId, int rating, String comment) {
        this.id = id;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.rating = rating;
        this.comment = comment;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getRestaurantId() { return restaurantId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    @Override
    public String toString() {
        return "Review{" + "id=" + id + ", userId=" + userId + ", restaurantId=" + restaurantId + ", rating=" + rating + ", comment='" + comment + '\'' + '}';
    }
}
