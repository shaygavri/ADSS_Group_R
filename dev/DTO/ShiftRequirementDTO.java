package DTO;

public class ShiftRequirementDTO {
    private int shiftId;
    private int roleId;
    private int requiredAmount;

    public ShiftRequirementDTO(int shiftId, int roleId, int requiredAmount) {
        this.shiftId = shiftId;
        this.roleId = roleId;
        this.requiredAmount = requiredAmount;
    }

    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getRequiredAmount() {
        return requiredAmount;
    }

    public void setRequiredAmount(int requiredAmount) {
        this.requiredAmount = requiredAmount;
    }
}
