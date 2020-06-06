package MainP;

import Vehicles.Car;
import Vehicles.CarBoard;
import Vehicles.Engine;
import Vehicles.Wheel;

public class Main {
    public static void main(String[] args) {
        Engine engine1 = new Engine("v6", 2.6);
        Wheel[] wheels1 = new Wheel[4];

        for(int i =0; i < 4; i++) {
            wheels1[i] = new Wheel(23, "PC");
        }

        Car carV6 = new Car(engine1, wheels1);
        if(CarBoard.testNewCar(carV6)) {
            System.out.println(carV6.toString());
        }
        else {
            System.out.println("Car isn't legal please contact the car board");
        }

        Engine engine2 = new Engine("v4", 1.6);
        Wheel[] wheels2 = new Wheel[4];

        for(int i =0; i < 4; i++) {
            wheels2[i] = new Wheel(17, "BP");
        }

        Car carV4 = new Car(engine2, wheels2);

        if(CarBoard.testNewCar(carV4)) {
            System.out.println(carV4.toString());
        }
        else {
            System.out.println("Car isn't legal please contact the car board");
        }
    }
}
