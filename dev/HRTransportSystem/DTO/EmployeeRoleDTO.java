package DTO;

public class EmployeeRoleDTO {
    private String employeeId;
    private int roleId;

    public EmployeeRoleDTO(String employeeId, int roleId) {
        this.employeeId = employeeId;
        this.roleId = roleId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}
