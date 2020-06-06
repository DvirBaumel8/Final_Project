package Vehicles;

public class Engine {
    private final String type;
    private final double enginePower;

    public Engine(String type, double enginePower) {
        this.type = type;
        this.enginePower = enginePower;
    }

    public String getType() {
        return type;
    }

    public double getEnginePower() {
        return enginePower;
    }
}
