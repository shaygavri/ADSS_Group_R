package TransportationIntegrationMock;

public enum MockDriverLicenseType {
    B(1),
    C1(2),
    C(3),
    CE(4);

    private final int level;

    MockDriverLicenseType(int level) {
        this.level = level;
    }

    public boolean canCover(MockDriverLicenseType requiredLicenseType) {
        if (requiredLicenseType == null) {
            return false;
        }

        return this.level >= requiredLicenseType.level;
    }
}
