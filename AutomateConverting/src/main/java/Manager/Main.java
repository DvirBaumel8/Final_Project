package Manager;

public class Main {
    public static void main(String[] args) {
        Manager manager = Manager.getInstance();
            try {
                manager.start();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

