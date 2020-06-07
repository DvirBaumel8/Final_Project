package Company;

import java.util.ArrayList;
import java.util.List;

public class EmployeeValidatorImpl implements EmployeeValidator {
    private final static int AGE_LIM = 21;
    private static List<String> currentIDs;

    static {
        currentIDs = new ArrayList<>();
    }


    @Override
    public boolean validateUniqueID(String id) {
        if(currentIDs.contains(id)) {
            throw new ExceptionInInitializerError("Duplicate ID, failed..");
        }
        else {
            currentIDs.add(id);
            return true;
        }
    }

    @Override
    public boolean validateEmployeesAge(int age) {
        if(age < AGE_LIM) {
            throw new ExceptionInInitializerError("new Employee age is too young.");
        }
        else {
            return false;
        }
    }
}
