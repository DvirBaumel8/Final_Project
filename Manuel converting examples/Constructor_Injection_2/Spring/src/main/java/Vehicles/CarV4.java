package Vehicles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarV4 extends Car {

    @Autowired
    public CarV4(Engine engineV4, Wheel[] wheelsBP) {
        super(engineV4, wheelsBP);
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
}
