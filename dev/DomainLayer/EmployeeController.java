package DomainLayer;
import java.util.ArrayList;

public class EmployeeController {
    private static EmployeeController instance;
    private ArrayList<Employee> employees;
    private ArrayList<Employee> firedEmployees;
    private ArrayList<Integer> branches;

    private EmployeeController() {
        branches = new ArrayList<>();
        employees = new ArrayList<>();
        firedEmployees = new ArrayList<>();
    }

    public static EmployeeController getInstance() {
        if (instance == null) {
            instance = new EmployeeController();
        }
        return instance;
    }

    public boolean addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("employee cannot be null");
        }
        if (getEmployee(employee.getUserName()) != null) {
            return false;
        }
        employees.add(employee);
        return true;
    }

    public Employee getEmployee(String userName) {
        if (userName == null || userName.isBlank()) {
            return null;
        }

        for (Employee e : employees) {
            if (e.getUserName().equals(userName)) {
                return e;
            }
        }
        return null;
    }

    public boolean employeeExists(String userName) {
        return getEmployee(userName) != null;
    }

    public boolean removeEmployee(String userName) {
        Employee employee = getEmployee(userName);
        if (employee == null) {
            return false;
        }

        employees.remove(employee);
        firedEmployees.add(employee);
        return true;
    }

    public boolean addBranch(int branchID) {
        if (branches.contains(branchID)) {
            return false;
        }
        branches.add(branchID);
        return true;
    }

    public boolean branchExists(int branchID) {
        return branches.contains(branchID);
    }

    public ArrayList<Employee> getEmployees() {
        return new ArrayList<>(employees);
    }

    public ArrayList<Integer> getBranches() {
        return new ArrayList<>(branches);
    }

    public ArrayList<Employee> getFiredEmployees() {
        return new ArrayList<>(firedEmployees);
    }
}