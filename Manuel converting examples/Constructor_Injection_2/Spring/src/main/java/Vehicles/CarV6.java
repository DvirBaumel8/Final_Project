package Vehicles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarV6 extends Car{

    @Autowired
    public CarV6(Engine engineV6, Wheel[] wheelsPC) {
        super(engineV6, wheelsPC);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append(String.format("Engine Type: %s,  Engine Power: %.2f\n", engine.getType(), engine.getEnginePower()));
        for(int i =0; i < this.wheels.length; i++) {
            str.append(String.format("Wheel_%d   Size: %d,  Type: %s\n", i, wheels[i].getSize(), wheels[i].getType()));
        }

        return str.toString();
    }
}
