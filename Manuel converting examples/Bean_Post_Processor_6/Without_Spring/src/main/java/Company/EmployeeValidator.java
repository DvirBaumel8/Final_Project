package Company;

public interface EmployeeValidator {
    boolean validateUniqueID(String id);
    boolean validateEmployeesAge(int age);
    void cleanIDs();
}
