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


// test "/Users/db384r/Dev/Final_Project/projects to convert/3/without spring";
// test "C:\\Users\\amira\\Desktop\\projects to convert\\1\\without spring";
