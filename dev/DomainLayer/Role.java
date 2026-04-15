package DomainLayer;

public class Role {
    private String roleName;
    private String description;
    private int roleID;

    // Permanent known ID's
    public static int SHIFT_MANAGER_ID = 1;

    public Role(int roleID, String roleName, String description) {
        if (roleID == 0) {
            throw new IllegalArgumentException("roleId must not be 0");
        }
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("roleName must not be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must not be null or blank");
        }

        this.roleID = roleID;
        this.roleName = roleName;
        this.description = description;
    }

    public int getRoleID() {
        return roleID;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null) {
            this.description = description;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role other)) return false;
        return  roleID == other.roleID;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(roleID);
    }

    @Override
    public String toString() {
        return "======= Role =======\n" +
                "ID          : " + roleID + "\n" +
                "Name        : " + roleName + "\n" +
                "Description : " + description + "\n" +
                "====================";
    }
}
