package RepositoryLayer;

import DTO.AvailabilityDTO;
import DTO.EmployeeDTO;
import DTO.EmployeeRoleDTO;
import DAO.AvailabilityDAO;
import DAO.EmployeeDAO;
import DAO.EmployeeRoleDAO;
import DomainLayer.Employee;
import DomainLayer.Role;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {
    private static final String ACTIVE = "ACTIVE";
    private static final String FIRED = "FIRED";

    private final EmployeeDAO employeeDAO;
    private final EmployeeRoleDAO employeeRoleDAO;
    private final AvailabilityDAO availabilityDAO;

    public EmployeeRepository() {
        this.employeeDAO = new EmployeeDAO();
        this.employeeRoleDAO = new EmployeeRoleDAO();
        this.availabilityDAO = new AvailabilityDAO();
    }

    public void insert(Employee employee) {
        try {
            employeeDAO.insert(toDTO(employee, ACTIVE));
            saveRolesAndAvailabilities(employee);
        } catch (SQLException e) {
            throw new RuntimeException("failed to save employee " + employee.getUserName(), e);
        }
    }

    public void update(Employee employee) {
        try {
            employeeDAO.update(toDTO(employee, ACTIVE));
            employeeRoleDAO.deleteByEmployee(employee.getUserName());
            availabilityDAO.deleteByEmployee(employee.getUserName());
            saveRolesAndAvailabilities(employee);
        } catch (SQLException e) {
            throw new RuntimeException("failed to update employee " + employee.getUserName(), e);
        }
    }

    // marks an employee as fired instead of deleting the row
    public void fire(String userName) {
        try {
            employeeDAO.updateStatus(userName, FIRED);
        } catch (SQLException e) {
            throw new RuntimeException("failed to fire employee " + userName, e);
        }
    }

    // knownRoles is the controller's role list, used to attach the real Role objects
    public List<Employee> loadActive(List<Role> knownRoles) {
        return loadByStatus(ACTIVE, knownRoles);
    }

    public List<Employee> loadFired(List<Role> knownRoles) {
        return loadByStatus(FIRED, knownRoles);
    }

    private List<Employee> loadByStatus(String status, List<Role> knownRoles) {
        try {
            List<Employee> employees = new ArrayList<>();
            for (EmployeeDTO dto : employeeDAO.selectByStatus(status)) {
                employees.add(toDomain(dto, knownRoles));
            }
            return employees;
        } catch (SQLException e) {
            throw new RuntimeException("failed to load " + status + " employees", e);
        }
    }

    private void saveRolesAndAvailabilities(Employee employee) throws SQLException {
        for (Role role : employee.getRoles()) {
            employeeRoleDAO.insert(new EmployeeRoleDTO(employee.getUserName(), role.getRoleID()));
        }
        for (Integer shiftIndex : employee.getAvailabilities()) {
            availabilityDAO.insert(new AvailabilityDTO(employee.getUserName(), shiftIndex));
        }
    }

    private Employee toDomain(EmployeeDTO dto, List<Role> knownRoles) throws SQLException {
        Employee.BankAccount bankAccount = new Employee.BankAccount(
                dto.getBankNumber(), dto.getBankBranchNumber(), dto.getBankAccountNumber());

        Employee employee = new Employee(
                dto.getBranchId(),
                dto.getUserName(),
                bankAccount,
                dto.getHourlySalary(),
                dto.getPassword(),
                Employee.EmploymentType.valueOf(dto.getEmploymentType()),
                dto.getEmployeeId());

        employee.setVacationDays(dto.getVacationDays());
        employee.setStartContract(dto.getStartContract());
        employee.setEndContract(dto.getEndContract());
        employee.setIsLoggedIn(dto.isLoggedIn());
        if (dto.getDriverLicenseType() != null) {
            employee.setDriverLicenseType(Employee.DriverLicenseType.valueOf(dto.getDriverLicenseType()));
        }

        for (EmployeeRoleDTO link : employeeRoleDAO.selectByEmployee(dto.getUserName())) {
            Role role = findRole(knownRoles, link.getRoleId());
            if (role != null) {
                employee.addRole(role);
            }
        }

        ArrayList<Integer> availabilities = new ArrayList<>();
        for (AvailabilityDTO availability : availabilityDAO.selectByEmployee(dto.getUserName())) {
            availabilities.add(availability.getShiftIndex());
        }
        employee.setAvailabilities(availabilities);

        return employee;
    }

    private Role findRole(List<Role> knownRoles, int roleId) {
        for (Role role : knownRoles) {
            if (role.getRoleID() == roleId) {
                return role;
            }
        }
        return null;
    }

    private EmployeeDTO toDTO(Employee employee, String status) {
        Employee.BankAccount bankAccount = employee.getBankAccount();
        String licenseType = employee.getDriverLicenseType() == null
                ? null : employee.getDriverLicenseType().name();

        return new EmployeeDTO(
                employee.getBranchID(),
                employee.getUserName(),
                employee.getPassword(),
                employee.getId(),
                bankAccount.getBankNumber(),
                bankAccount.getBankBranchNumber(),
                bankAccount.getBankAccountNumber(),
                employee.getHourlySalary(),
                employee.getEmploymentType().name(),
                employee.getVacationDays(),
                employee.getStartContract(),
                employee.getEndContract(),
                licenseType,
                employee.getIsLoggedIn(),
                status);
    }
}
