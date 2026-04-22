package org.example.model;

public class ExpressDriver extends Driver {
    private String vehicleType;
    private double speedMultiplier;

    public ExpressDriver(int id, String name, String vehicleType, double speedMultiplier) {
        super(id, name);
        this.vehicleType = vehicleType;
        this.speedMultiplier = speedMultiplier;
    }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public double getSpeedMultiplier() { return speedMultiplier; }
    public void setSpeedMultiplier(double speedMultiplier) { this.speedMultiplier = speedMultiplier; }

    @Override
    public String toString() {
        return "ExpressDriver{" + describeDriverFields() + ", vehicleType='" + vehicleType + '\''
                + ", speedMultiplier=" + speedMultiplier + '}';
    }
}