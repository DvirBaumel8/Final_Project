package Company;

import java.util.List;
import java.util.Map;

public class WazeCompanyValidatorImpl implements CompanyValidator{

    @Override
    public boolean validateCompanyNameIsAccepted(String companyName) {
        if(CompanyBoard.getCurrentCompaniesNames().contains(companyName)) {
            throw new ExceptionInInitializerError("Failed to validate company unique name");
        }
        else {
            return true;
        }
    }

    @Override
    public boolean validateSalaryVsEmployeeRole(List<Employee> employees) {
        Map<EmployeeRole, Integer> map = CompanyBoard.getAcceptedSalaries();
        EmployeeRole currRole;
        int currEmployeeSalary, currentMapSalary;

        for(Employee employee : employees) {
            currRole = employee.getRole();
            currEmployeeSalary = employee.getSalary();
            currentMapSalary = map.get(currRole);

            if(currEmployeeSalary < currentMapSalary) {
                throw new ExceptionInInitializerError("Salary is too low for the new employee role");
            }
        }
        return true;
    }
}
