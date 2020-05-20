package Manager;

public class Main {
    public static void main(String[] args) {
        String projectDirectoryPath = "/Users/db384r/Dev/Final_Project/projects to convert/3/without spring";
        Manager manager = Manager.getInstance();
        if(manager.validateProjectDirectoryPath(projectDirectoryPath)) {
            try {
                manager.start(projectDirectoryPath);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println(manager.getProjectPathErrorMessage());
        }
    }
}
