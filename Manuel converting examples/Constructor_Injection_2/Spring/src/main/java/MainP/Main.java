package MainP;

import Vehicles.CarBoard;
import Vehicles.CarV4;
import Vehicles.CarV6;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(MainConfiguration.class);
        CarV6 carV6;
        CarV4 carV4;

        try {
            carV6 = ctx.getBean(CarV6.class);
            if(CarBoard.testNewCar(carV6)) {
                System.out.println(carV6.toString());
            }
            else {
                System.out.println("Car isn't legal please contact the car board");
            }

        }
        catch (BeansException ex) {
            System.out.println("Something went wrong..");
        }

        try {
            carV4 = ctx.getBean(CarV4.class);
            if(CarBoard.testNewCar(carV4)) {
                System.out.println(carV4.toString());
            }
            else {
                System.out.println("Car isn't legal please contact the car board");
            }
        }
        catch (BeansException ex) {
            System.out.println("Something went wrong..");
        }

    }
}
