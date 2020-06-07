package Company;

import java.util.List;

public interface CompanyValidator {
     boolean validateCompanyNameIsAccepted(String companyName);
     boolean validateSalaryVsEmployeeRole(List<Employee> employees);
}
