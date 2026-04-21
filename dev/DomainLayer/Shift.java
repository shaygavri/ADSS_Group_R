package DomainLayer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shift {

    private int shiftId; // 1-14
    private int branchId;
    private LocalDate date;
    // private LocalTime startTime;
    // private LocalTime endTime;
    private ShiftType shiftType;
    private boolean closedDay;
    private Map<Role, Integer> requirements;
    private Map<Employee, Role> employeeRoleAssignments;

    public enum ShiftType {
        MORNING,
        EVENING
    }

    public Shift(int shiftId, int branchId, LocalDate date, ShiftType shiftType) {
        this.shiftId = shiftId;
        this.branchId = branchId;
        this.date = date;
        this.shiftType = shiftType;
        this.closedDay = false;
        this.requirements = new HashMap<>();
        this.employeeRoleAssignments = new HashMap<>();
    }

    public void addRequirement(Role role, int minimumCount) {
        if (closedDay) {
            return;
        }
        requirements.put(role, minimumCount);
    }

    public void removeRequirement(Role role) {
        requirements.remove(role);
    }

    public void clearRequirements() {
        requirements.clear();
    }

    public void addBackRequirement(Role role) {
        if (role == null || closedDay) {
            return;
        }
        requirements.put(role, getRequiredCountForRole(role) + 1);
    }

    public Map<Role, Integer> getRequirements() {
        return requirements;
    }

    public void assignEmployee(Employee employee, Role role) {
        employeeRoleAssignments.put(employee, role);
    }

    public boolean fulfillRequirement(Role role) {
        Integer requiredCount = requirements.get(role);
        if (requiredCount == null || requiredCount <= 0) {
            return false;
        }

        if (requiredCount == 1) {
            requirements.remove(role);
        } else {
            requirements.put(role, requiredCount - 1);
        }
        return true;
    }

    public boolean removeEmployee(Employee employee) {
        return employeeRoleAssignments.remove(employee) != null;
    }

    public Role getRoleOf(Employee employee) {
        return employeeRoleAssignments.get(employee);
    }

    public boolean isAssigned(Employee employee) {
        return employeeRoleAssignments.containsKey(employee);
    }

    public boolean requiresRole(Role role) {
        return requirements.containsKey(role);
    }

    public int getRequiredCountForRole(Role role) {
        Integer requiredCount = requirements.get(role);
        return requiredCount == null ? 0 : requiredCount;
    }

    public boolean isClosedDay() {
        return closedDay;
    }

    public void setClosedDay(boolean closedDay) {
        this.closedDay = closedDay;
        if (closedDay) {
            clearRequirements();
        }
    }

    public List<Employee> getAssignedEmployees() {
        return new ArrayList<>(employeeRoleAssignments.keySet());
    }

    public Map<Employee, Role> getEmployeeRoleAssignments() {
        return employeeRoleAssignments;
    }

    public int getAssignmentCount() {
        return employeeRoleAssignments.size();
    }

    public boolean hasShiftManager() {
        return employeeRoleAssignments.values().stream().anyMatch(role -> role.getRoleID() == Role.SHIFT_MANAGER_ID);
    }

    public int getShiftId() {
        return shiftId;
    }

    /** @return the branch this shift belongs to */
    public int getBranchId() {
        return branchId;
    }

    /** @return the date of this shift */
    public LocalDate getDate() {
        return date;
    }

    /** @return MORNING or EVENING */
    public ShiftType getShiftType() {
        return shiftType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Shift other))
            return false;
        return shiftId == other.shiftId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(shiftId);
    }

    @Override
    public String toString() {
        // CHANGED: show actual requirements (role name + count) instead of just the size
        StringBuilder sb = new StringBuilder();
        sb.append("Shift{id=").append(shiftId)
                .append(", branch=").append(branchId)
                .append(", day=").append(date.getDayOfWeek())
                .append(", date=").append(date)
                .append(", type=").append(shiftType)
                .append(", closed=").append(closedDay)
                .append(", assigned=").append(employeeRoleAssignments.size())
                .append(", requirements=[");
        if (requirements.isEmpty()) {
            sb.append("none");
        } else {
            boolean first = true;
            for (Map.Entry<Role, Integer> entry : requirements.entrySet()) {
                if (!first) sb.append(", ");
                sb.append(entry.getKey().getRoleName()).append(": ").append(entry.getValue());
                first = false;
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}
