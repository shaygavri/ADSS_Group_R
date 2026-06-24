package DTO;

public class TransportDriverDTO {
    private String employeeId;
    private String employeeUserName;
    private int branchId;
    private String licenseType;
    private boolean active;

    public TransportDriverDTO(String employeeId, String employeeUserName, int branchId,
                              String licenseType, boolean active) {
        this.employeeId = employeeId;
        this.employeeUserName = employeeUserName;
        this.branchId = branchId;
        this.licenseType = licenseType;
        this.active = active;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeUserName() {
        return employeeUserName;
    }

    public void setEmployeeUserName(String employeeUserName) {
        this.employeeUserName = employeeUserName;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
