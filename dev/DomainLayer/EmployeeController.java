package DomainLayer;

import java.util.ArrayList;

public class EmployeeController {
    private static EmployeeController instance;
    private ArrayList<Employee> employees;
    private ArrayList<Employee> firedEmployees;
    private ArrayList<Branch> branches;
    private ArrayList<Role> roles;

    private EmployeeController() {
        branches = new ArrayList<>();
        employees = new ArrayList<>();
        firedEmployees = new ArrayList<>();
        roles = new ArrayList<>();
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
            if (e.getUserName().equalsIgnoreCase(userName.trim())) {
                return e;
            }
        }
        return null;
    }

    public boolean employeeExists(String userName) {
        return getEmployee(userName) != null;
    }

    public boolean addRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("role cannot be null");
        }
        if (getRole(role.getRoleID()) != null || getRoleByName(role.getRoleName()) != null) {
            return false;
        }
        roles.add(role);
        return true;
    }

    public Role getRole(int roleID) {
        for (Role role : roles) {
            if (role.getRoleID() == roleID) {
                return role;
            }
        }
        return null;
    }

    public Role getRoleByName(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            return null;
        }

        for (Role role : roles) {
            if (role.getRoleName().trim().equalsIgnoreCase(roleName.trim())) {
                return role;
            }
        }
        return null;
    }

    public Role getRoleByIdOrName(String roleInput) {
        if (roleInput == null || roleInput.isBlank()) {
            return null;
        }

        try {
            return getRole(Integer.parseInt(roleInput.trim()));
        } catch (NumberFormatException e) {
            return getRoleByName(roleInput);
        }
    }

    public boolean roleExists(int roleID) {
        return getRole(roleID) != null;
    }

    public boolean addRoleToEmployee(String userName, int roleID) {
        Employee employee = getEmployee(userName);
        Role role = getRole(roleID);

        if (employee == null || role == null) {
            return false;
        }

        if (employee.getRoles().contains(role)) {
            return false;
        }

        employee.addRole(role);
        return true;
    }

    public boolean employeeHasRole(String userName, int roleID) {
        Employee employee = getEmployee(userName);
        Role role = getRole(roleID);

        if (employee == null || role == null) {
            return false;
        }

        return employee.getRoles().contains(role);
    }

    public boolean setEmployeeDriverLicenseType(String userName, Employee.DriverLicenseType driverLicenseType) {
        Employee employee = getEmployee(userName);
        if (employee == null || driverLicenseType == null) {
            return false;
        }
        if (!employeeHasRole(userName, Role.DRIVER_ID)) {
            return false;
        }

        employee.setDriverLicenseType(driverLicenseType);
        return true;
    }

    public boolean changeEmployeeDriverLicenseType(String userName, Employee.DriverLicenseType newDriverLicenseType) {
        return setEmployeeDriverLicenseType(userName, newDriverLicenseType);
    }

    public Employee.DriverLicenseType getEmployeeDriverLicenseType(String userName) {
        Employee employee = getEmployee(userName);
        if (employee == null) {
            return null;
        }

        return employee.getDriverLicenseType();
    }

    public boolean employeeCanDrive(String userName, Employee.DriverLicenseType requiredLicenseType) {
        Employee employee = getEmployee(userName);
        if (employee == null || requiredLicenseType == null) {
            return false;
        }
        if (!employeeHasRole(userName, Role.DRIVER_ID)) {
            return false;
        }

        return employee.canDrive(requiredLicenseType);
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

    public boolean changeEmployeeSalary(String userName, int newSalary) {
        Employee employee = getEmployee(userName);
        if (employee == null || newSalary <= 0) {
            return false;
        }

        employee.setHourlySalary(newSalary);
        return true;
    }

    public String employeesAndRolesToString() {
        if (employees.isEmpty()) {
            return "No employees in the system";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("===== Employees And Roles =====\n");

        for (Employee employee : employees) {
            builder.append("Employee: ").append(employee.getUserName()).append("\n");
            builder.append("Roles:\n");

            ArrayList<Role> employeeRoles = employee.getRoles();
            if (employeeRoles.isEmpty()) {
                builder.append("None\n");
            } else {
                for (Role role : employeeRoles) {
                    builder.append("ID: ")
                            .append(role.getRoleID())
                            .append(", Name: ")
                            .append(role.getRoleName())
                            .append("\n");
                }
            }

            builder.append("-------------------------------\n");
        }

        return builder.toString();
    }

    // ADDED: returns each role with the number of employees that have it
    public String rolesAndEmployeeCountToString() {
        if (roles.isEmpty()) {
            return "No roles in the system";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("===== Existing Roles =====\n");
        for (Role role : roles) {
            int count = 0;
            for (Employee employee : employees) {
                if (employee.getRoles().contains(role)) {
                    count++;
                }
            }
            builder.append("Role: ").append(role.getRoleName())
                    .append(" (ID: ").append(role.getRoleID()).append(")")
                    .append("\nDescription: ").append(role.getDescription())
                    .append("\nEmployees with this role: ").append(count).append("\n")
                    .append("-------------------------------\n");
        }
        builder.append("==========================");
        return builder.toString();
    }

    public String rolesListToString() {
        if (roles.isEmpty()) {
            return "No roles in the system";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Current roles:\n");
        for (Role role : roles) {
            builder.append("ID: ")
                    .append(role.getRoleID())
                    .append(" - ")
                    .append(role.getRoleName())
                    .append("\n");
        }
        return builder.toString();
    }

    public boolean addBranch(int branchID) {
        Branch branch = new Branch(branchID);
        if (branches.contains(branch)) {
            return false;
        }
        branches.add(branch);
        return true;
    }

    public boolean branchExists(int branchID) {
        return branches.contains(new Branch(branchID));
    }

    public boolean branchHasActiveEmployees(int branchID) {
        for (Employee employee : employees) {
            if (employee.getBranchID() == branchID) {
                return true;
            }
        }
        return false;
    }

    public boolean removeBranch(int branchID) {
        Branch branch = new Branch(branchID);
        if (!branches.contains(branch)) {
            return false;
        }
        branches.remove(branch);
        return true;
    }

    public ArrayList<Employee> getEmployees() {
        return new ArrayList<>(employees);
    }

    public ArrayList<Branch> getBranches() {
        return new ArrayList<>(branches);
    }

    public ArrayList<Employee> getFiredEmployees() {
        return new ArrayList<>(firedEmployees);
    }

    public ArrayList<Role> getRoles() {
        return new ArrayList<>(roles);
    }
}
