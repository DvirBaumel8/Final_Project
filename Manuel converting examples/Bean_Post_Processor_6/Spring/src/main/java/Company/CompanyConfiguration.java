package Company;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CompanyConfiguration {

    @Bean
    public Company cisco() {
        Company company = new Company("Cisco", "Tel-Aviv", new CiscoCompanyValidatorImpl());
        List<Employee> ciscoEmployees = new ArrayList<>();
        ciscoEmployees.add(employee1());
        ciscoEmployees.add(employee2());
        ciscoEmployees.add(employee3());
        ciscoEmployees.add(employee4());
        ciscoEmployees.add(employee5());
        ciscoEmployees.add(employee6());
        ciscoEmployees.add(employee7());
        ciscoEmployees.add(employee8());
        ciscoEmployees.add(employee9());
        ciscoEmployees.add(employee10());
        company.setEmployees(ciscoEmployees);
        return company;
    }

    @Bean
    public Company waze() {
        Company company = new Company("Waze", "Tel-Aviv", new WazeCompanyValidatorImpl());
        List<Employee> wazeEmployees = new ArrayList<>();
        wazeEmployees.add(employee2());
        wazeEmployees.add(employee3());
        wazeEmployees.add(employee4());
        wazeEmployees.add(employee5());
        wazeEmployees.add(employee6());
        wazeEmployees.add(employee8());
        wazeEmployees.add(employee10());
        company.setEmployees(wazeEmployees);
        return company;
    }

    @Bean
    public Employee employee1() {
        Employee employee = new Employee("Avi Cohen","552255", 35, "Rimon", "Marketing", EmployeeRole.DEVELOPER, 15000);
        return employee;
    }

    @Bean
    public Employee employee2() {
        Employee employee = new Employee("Avi Levi","552256", 32, "Keshet", "System", EmployeeRole.TEAM_LEADER, 17000);
        return employee;
    }

    @Bean
    public Employee employee3() {
        Employee employee = new Employee("Adi Nimrod","452255", 32, "Levi Aharon", "Publishing", EmployeeRole.QUALITY_INSURANCE, 11500);
        return employee;
    }

    @Bean
    public Employee employee4() {
        Employee employee = new Employee("Avital Shoshan","552155", 37, "Hadarim", "System", EmployeeRole.QUALITY_INSURANCE, 11000);
        return employee;
    }

    @Bean
    public Employee employee5() {
        Employee employee = new Employee("Lior Nachmias","882255", 42, "Rimon", "Developing", EmployeeRole.PRODUCT_MANAGER, 18000);
        return employee;
    }

    @Bean
    public Employee employee6() {
        Employee employee = new Employee("Nataly Haluba","322255", 35, "Brosh", "Publishing", EmployeeRole.DEVELOPER, 15000);
        return employee;
    }

    @Bean
    public Employee employee7() {
        Employee employee = new Employee("Lidor Tzarfati","327255", 21, "Rimon", "Marketing", EmployeeRole.TEAM_LEADER, 19000);
        return employee;
    }

    @Bean
    public Employee employee8() {
        Employee employee = new Employee("Maor Shalom","566666", 39, "Yasmin", "Developing", EmployeeRole.DEVELOPER, 16000);
        return employee;
    }

    @Bean
    public Employee employee9() {
        Employee employee = new Employee("Amir aloni","565255", 30, "Rimon", "Marketing", EmployeeRole.DEVELOPER, 15000);
        return employee;
    }

    @Bean
    public Employee employee10() {
        Employee employee = new Employee("Eliav Ezer","772255", 34, "Neot Kdomim", "System", EmployeeRole.HR, 12000);
        return employee;
    }

    @Bean
    public BeanPostProcessorImpl beanPostProcessor() {
        return new BeanPostProcessorImpl();
    }

}
