package DTO;

public class ShiftAssignmentDTO {
    private int shiftId;
    private String employeeId;
    private int roleId;

    public ShiftAssignmentDTO(int shiftId, String employeeId, int roleId) {
        this.shiftId = shiftId;
        this.employeeId = employeeId;
        this.roleId = roleId;
    }

    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
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
