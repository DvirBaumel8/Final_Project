package Company;

public class Employee extends Person {
    private String department;
    private EmployeeRole role;
    private int salary;

    public Employee(String fullName, String id, int age, String address, String department, EmployeeRole role, int salary) {
        super(fullName, id, age, address);
        this.department = department;
        this.role = role;
        this.salary = salary;
    }

    public String getDepartment() {
        return department;
    }

    public EmployeeRole getRole() {
        return role;
    }

    public int getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("Employee Details: \n -Department %s\n -Role: %s\n -Salary: %d\n", department, role, salary);
    }
}
