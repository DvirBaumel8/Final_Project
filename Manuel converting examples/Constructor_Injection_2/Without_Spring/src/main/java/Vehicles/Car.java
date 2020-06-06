package Vehicles;

public class Car {
    private Engine engine;
    private Wheel[] wheels;

    public Car(Engine engine, Wheel[] wheels) {
        this.engine = engine;
        this.wheels = wheels;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append(String.format("Engine Type: %s,  Engine Power: %.2f\n", engine.getType(), engine.getEnginePower()));
        for(int i =0; i < wheels.length; i++) {
            str.append(String.format("Wheel_%d   Size: %d,  Type: %s\n", i, wheels[i].getSize(), wheels[i].getType()));
        }

        return str.toString();
    }

    public Engine getEngine() {
        return engine;
    }

    public Wheel[] getWheels() {
        return wheels;
    }
}
