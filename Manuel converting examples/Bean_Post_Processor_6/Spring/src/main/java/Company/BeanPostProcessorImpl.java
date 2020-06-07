package Company;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class BeanPostProcessorImpl implements BeanPostProcessor {
    private EmployeeValidator employeeValidator = new EmployeeValidatorImpl();

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
    {
        System.out.println("Called postProcessBeforeInitialization() for :" + beanName);
        if(bean instanceof Company) {
            Company company = (Company) bean;
            try {
                company.validateCompany();
                System.out.println(String.format("Validate company %s passed successfully", ((Company) bean).getName()));
            }
            catch (ExceptionInInitializerError ex) {
                System.out.println(ex.getMessage());
                throw new RuntimeException(ex.getMessage());
            }

        }
        else if(bean instanceof Employee) {
            Employee employee = (Employee) bean;
            try {
                employeeValidator.validateUniqueID(employee.getId());
                employeeValidator.validateEmployeesAge(employee.getAge());
                System.out.println(String.format("Validate employee %s passed successfully", ((Employee) bean).getFullName()));
            }
            catch (ExceptionInInitializerError ex) {
                System.out.println(ex.getMessage());
                throw new RuntimeException(ex.getMessage());
            }
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
    {
        System.out.println("Called postProcessAfterInitialization() for :" + beanName);
        return bean;
    }
}
