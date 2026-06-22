package DTO;

import java.time.LocalDate;

public class ShiftDTO {
    private int shiftId;
    private int branchId;
    private LocalDate date;
    private String shiftType;
    private boolean closedDay;

    public ShiftDTO(int shiftId, int branchId, LocalDate date, String shiftType, boolean closedDay) {
        this.shiftId = shiftId;
        this.branchId = branchId;
        this.date = date;
        this.shiftType = shiftType;
        this.closedDay = closedDay;
    }

    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }

    public boolean isClosedDay() {
        return closedDay;
    }

    public void setClosedDay(boolean closedDay) {
        this.closedDay = closedDay;
    }
}
