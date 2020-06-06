package Vehicles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarBoard {
    private static Map<String, Double> acceptedEngines;
    private static Map<Integer, String> acceptedWheels;

    static {
        acceptedEngines = new HashMap<>();
        acceptedEngines.put("v2", 1.25);
        acceptedEngines.put("v4", 1.6);
        acceptedEngines.put("v6", 2.6);
        acceptedEngines.put("v7", 2.8);
        acceptedEngines.put("vx1", 1.8);
        acceptedEngines.put("vx2", 2.0);

        acceptedWheels = new HashMap<>();
        acceptedWheels.put(23, "PC");
        acceptedWheels.put(17, "BP");
        acceptedWheels.put(27, "AC");
        acceptedWheels.put(15, "AA");
    }

    public static boolean testNewCar(Car car) {
        return TestEngine(car.getEngine()) && testWheels(car.getWheels());
    }

    private static boolean testWheels(Wheel[] wheels) {
        if(checkWheelsAreSame(wheels)) {
            if(checkWheelsAreAccepted(wheels)) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    private static boolean checkWheelsAreAccepted(Wheel[] wheels) {
        int size = wheels[0].getSize();
        String type = wheels[0].getType();

        for(Map.Entry<Integer, String > entry : acceptedWheels.entrySet()) {
            if(entry.getKey() == size &&entry.getValue().equals(type)  ) {
                return true;
            }
        }

        return false;
    }

    private static boolean checkWheelsAreSame(Wheel[] wheels) {
        String type = wheels[0].getType();
        int size = wheels[0].getSize();

        for(int i =0; i < 4; i ++) {
            if(wheels[i].getType() == type && wheels[i].getSize() == size) {
                continue;
            }
            else {
                return false;
            }
        }
        return true;
    }

    private static boolean TestEngine(Engine engine) {
        String type = engine.getType();
        double enginePower = engine.getEnginePower();

        for(Map.Entry<String, Double> entry : acceptedEngines.entrySet()) {
            if(entry.getKey().equals(type) && entry.getValue() == enginePower) {
                return true;
            }
        }

        return false;
    }
}
