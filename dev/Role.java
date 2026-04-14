public class Role {
    private String roleName;
    private String description;
    private int roleID;

    public Role(int roleID, String roleName) {
        if (roleID == 0) {
            throw new IllegalArgumentException("roleId must not be 0");
        }
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("roleName must not be null or blank");
        }

        this.roleID = roleID;
        this.roleName = roleName;
        this.description = "";
    }

    public Role(int roleID, String roleName, String description) {
        this(roleID, roleName);

        if (description != null) {
            this.description = description;
        }
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
    public String toString() {
        return "Role: " + roleName + " (ID: " + roleID + ")";
    }
}
