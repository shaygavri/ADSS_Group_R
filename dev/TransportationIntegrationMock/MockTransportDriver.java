package TransportationIntegrationMock;

public class MockTransportDriver {
    private final String employeeId;
    private String employeeUserName;
    private int branchId;
    private MockDriverLicenseType licenseType;
    private boolean active;

    public MockTransportDriver(String employeeId, String employeeUserName, int branchId,
                               MockDriverLicenseType licenseType) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException("employee id cannot be empty");
        }
        if (employeeUserName == null || employeeUserName.isBlank()) {
            throw new IllegalArgumentException("employee username cannot be empty");
        }
        if (branchId <= 0) {
            throw new IllegalArgumentException("branch id must be positive");
        }
        if (licenseType == null) {
            throw new IllegalArgumentException("license type cannot be null");
        }

        this.employeeId = employeeId.trim();
        this.employeeUserName = employeeUserName.trim();
        this.branchId = branchId;
        this.licenseType = licenseType;
        this.active = true;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeUserName() {
        return employeeUserName;
    }

    public int getBranchId() {
        return branchId;
    }

    public MockDriverLicenseType getLicenseType() {
        return licenseType;
    }

    public boolean isActive() {
        return active;
    }

    public void updateDetails(String employeeUserName, int branchId, MockDriverLicenseType licenseType) {
        if (employeeUserName == null || employeeUserName.isBlank()) {
            throw new IllegalArgumentException("employee username cannot be empty");
        }
        if (branchId <= 0) {
            throw new IllegalArgumentException("branch id must be positive");
        }
        if (licenseType == null) {
            throw new IllegalArgumentException("license type cannot be null");
        }

        this.employeeUserName = employeeUserName.trim();
        this.branchId = branchId;
        this.licenseType = licenseType;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public boolean canDrive(MockDriverLicenseType requiredLicenseType) {
        return active && licenseType.canCover(requiredLicenseType);
    }

    @Override
    public String toString() {
        return "MockTransportDriver{" +
                "employeeId='" + employeeId + '\'' +
                ", employeeUserName='" + employeeUserName + '\'' +
                ", branchId=" + branchId +
                ", licenseType=" + licenseType +
                ", active=" + active +
                '}';
    }
}
