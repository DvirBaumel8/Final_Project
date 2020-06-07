package Company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyBoard {
    private static List<String> currentCompaniesNames;
    private static Map<EmployeeRole,Integer> acceptedSalaries;

    static {
        currentCompaniesNames = new ArrayList<>();
        acceptedSalaries = new HashMap<EmployeeRole, Integer>();
        initCompanyList();
        initAcceptedSalaries();
    }

    private static void initAcceptedSalaries() {
        acceptedSalaries.put(EmployeeRole.DEVELOPER, 15000);
        acceptedSalaries.put(EmployeeRole.TEAM_LEADER, 16000);
        acceptedSalaries.put(EmployeeRole.HR, 9000);
        acceptedSalaries.put(EmployeeRole.QUALITY_INSURANCE, 10000);
        acceptedSalaries.put(EmployeeRole.PRODUCT_MANAGER, 15000);
    }

    private static void initCompanyList() {
        currentCompaniesNames.add("Teva");
        currentCompaniesNames.add("Wix");
        currentCompaniesNames.add("Tabola");
        currentCompaniesNames.add("AT&T");
        currentCompaniesNames.add("Metrix");
    }

    public static List<String> getCurrentCompaniesNames() {
        return currentCompaniesNames;
    }

    public static Map<EmployeeRole, Integer> getAcceptedSalaries() {
        return acceptedSalaries;
    }
}
