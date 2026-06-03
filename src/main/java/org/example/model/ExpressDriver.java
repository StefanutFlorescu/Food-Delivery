package org.example.model;

public class ExpressDriver extends Driver {
    private String vehicleType;
    private double priceMultiplier;

    public ExpressDriver(int id, String name, String vehicleType, double priceMultiplier) {
        super(id, name);
        this.vehicleType = vehicleType;
        this.priceMultiplier = priceMultiplier;
    }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public double getPriceMultiplier() { return priceMultiplier; }
    public void setPriceMultiplier(double priceMultiplier) { this.priceMultiplier = priceMultiplier; }

    @Override
    public String toString() {
        return "ExpressDriver{" + describeDriverFields() + ", vehicleType='" + vehicleType + '\''
                + ", priceMultiplier=" + priceMultiplier + '}';
    }
}