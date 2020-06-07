package Company;

import java.util.List;

public interface EmployeeValidator {
    boolean validateUniqueID(String id);
    boolean validateEmployeesAge(int age);
}
