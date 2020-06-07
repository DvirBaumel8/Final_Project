package Company;

import java.util.List;

public class Company {
    private String name;
    private List<Employee> employees;
    private String location;
    private CompanyValidator validator;

    public Company(String name, String location, CompanyValidator validator) {
        this.name = name;
        this.location = location;
        this.validator = validator;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void validateCompany() {
        validator.validateCompanyNameIsAccepted(name);
        validator.validateSalaryVsEmployeeRole(employees);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(String.format("\nCompany Details: \n -Company name: %s\n -Company location: %s\n", name, location));
        str.append("Company Employees:\n");
        for(int i =0; i < employees.size(); i++) {
            str.append(employees.get(i).toString());
            str.append(System.lineSeparator());
        }
        return str.toString();
    }
}
