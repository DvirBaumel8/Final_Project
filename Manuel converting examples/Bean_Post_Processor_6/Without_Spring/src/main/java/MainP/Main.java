package MainP;


import Company.*;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Company[] companies = new Company[2];
        companies[0] = createCisco();
        companies[1] = createWaze();
        try {
            processCompaniesValidation(companies);
        }
        catch (ExceptionInInitializerError ex) {
            System.out.println(ex);
            throw new RuntimeException();
        }

        for(Company company : companies) {
            System.out.println(company);
        }

    }

    private static void processCompaniesValidation(Company[] companies) {
        EmployeeValidator employeesValidator = new EmployeeValidatorImpl();
        List<Employee> currEmployees;
        for(int i =0; i < companies.length; i++) {
            companies[i].validateCompany();
            currEmployees = companies[i].getEmployees();
            for(Employee employee : currEmployees) {
                employeesValidator.validateEmployeesAge(employee.getAge());
                employeesValidator.validateUniqueID(employee.getId());
                employeesValidator.cleanIDs();
            }
        }
    }

    private static Company createWaze() {
        Company company = new Company("Waze", "Tel-Aviv", new WazeCompanyValidatorImpl());
        company.setEmployees(createWazeEmplpyeesList());
        return company;
    }

    private static Company createCisco() {
        Company company = new Company("Cisco", "Tel-Aviv", new CiscoCompanyValidatorImpl());
        company.setEmployees(createCiscoEmployees());
        return company;
    }

    private static List<Employee> createWazeEmplpyeesList() {
        List<Employee> wazeEmployees = new ArrayList<>();
        Employee employee2 = new Employee("Avi Levi","552256", 32, "Keshet", "System", EmployeeRole.TEAM_LEADER, 17000);
        Employee employee3 = new Employee("Adi Nimrod","452255", 32, "Levi Aharon", "Publishing", EmployeeRole.QUALITY_INSURANCE, 11500);
        Employee employee4 = new Employee("Avital Shoshan","552155", 37, "Hadarim", "System", EmployeeRole.QUALITY_INSURANCE, 11000);
        Employee employee5 = new Employee("Lior Nachmias","882255", 42, "Rimon", "Developing", EmployeeRole.PRODUCT_MANAGER, 18000);
        Employee employee6 = new Employee("Nataly Haluba","322255", 35, "Brosh", "Publishing", EmployeeRole.DEVELOPER, 15000);
        Employee employee8 = new Employee("Maor Shalom","566666", 39, "Yasmin", "Developing", EmployeeRole.DEVELOPER, 16000);
        Employee employee10 = new Employee("Eliav Ezer","772255", 34, "Neot Kdomim", "System", EmployeeRole.HR, 12000);

        wazeEmployees.add(employee2);
        wazeEmployees.add(employee3);
        wazeEmployees.add(employee4);
        wazeEmployees.add(employee5);
        wazeEmployees.add(employee6);
        wazeEmployees.add(employee8);
        wazeEmployees.add(employee10);
        return wazeEmployees;
    }

    private static List<Employee> createCiscoEmployees() {
        List<Employee> ciscoEmployees = new ArrayList<Employee>();
        Employee employee1 = new Employee("Avi Cohen","552255", 35, "Rimon", "Marketing", EmployeeRole.DEVELOPER, 15000);
        Employee employee2 = new Employee("Avi Levi","552256", 32, "Keshet", "System", EmployeeRole.TEAM_LEADER, 17000);
        Employee employee3 = new Employee("Adi Nimrod","452255", 32, "Levi Aharon", "Publishing", EmployeeRole.QUALITY_INSURANCE, 11500);
        Employee employee4 = new Employee("Avital Shoshan","552155", 37, "Hadarim", "System", EmployeeRole.QUALITY_INSURANCE, 11000);
        Employee employee5 = new Employee("Lior Nachmias","882255", 42, "Rimon", "Developing", EmployeeRole.PRODUCT_MANAGER, 18000);
        Employee employee6 = new Employee("Nataly Haluba","322255", 35, "Brosh", "Publishing", EmployeeRole.DEVELOPER, 15000);
        Employee employee7 = new Employee("Lidor Tzarfati","327255", 21, "Rimon", "Marketing", EmployeeRole.TEAM_LEADER, 19000);
        Employee employee8 = new Employee("Maor Shalom","566666", 39, "Yasmin", "Developing", EmployeeRole.DEVELOPER, 16000);
        Employee employee9 = new Employee("Amir aloni","565255", 30, "Rimon", "Marketing", EmployeeRole.DEVELOPER, 15000);
        Employee employee10 = new Employee("Eliav Ezer","772255", 34, "Neot Kdomim", "System", EmployeeRole.HR, 12000);

        ciscoEmployees.add(employee1);
        ciscoEmployees.add(employee2);
        ciscoEmployees.add(employee3);
        ciscoEmployees.add(employee4);
        ciscoEmployees.add(employee5);
        ciscoEmployees.add(employee6);
        ciscoEmployees.add(employee7);
        ciscoEmployees.add(employee8);
        ciscoEmployees.add(employee9);
        ciscoEmployees.add(employee10);

        return ciscoEmployees;
    }

}
