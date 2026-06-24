package DTO;

public class AvailabilityDTO {
    private String employeeId;
    private int shiftIndex;

    public AvailabilityDTO(String employeeId, int shiftIndex) {
        this.employeeId = employeeId;
        this.shiftIndex = shiftIndex;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public int getShiftIndex() {
        return shiftIndex;
    }

    public void setShiftIndex(int shiftIndex) {
        this.shiftIndex = shiftIndex;
    }
}
