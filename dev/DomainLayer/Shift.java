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
//    private LocalTime startTime;
//    private LocalTime endTime;
    private ShiftType shiftType;
    private Map<Role, Integer> requirements;
    private Map<Employee, Role> employeeRoleAssignments;

    public enum ShiftType{
        MORNING,
        EVENING
    }

    public Shift(int shiftId, int branchId, LocalDate date, ShiftType shiftType){
        this.shiftId = shiftId;
        this.branchId = branchId;
        this.date = date;
        this.shiftType = shiftType;
        this.requirements = new HashMap<>();
        this.employeeRoleAssignments = new HashMap<>();
    }

    public void addRequirement(Role role, int minimumCount) {
        requirements.put(role, minimumCount);
    }

    public void removeRequirement(Role role) {
        requirements.remove(role);
    }

    public Map<Role, Integer> getRequirements() {
        return requirements;
    }

    public void assignEmployee(Employee employee, Role role) {
        employeeRoleAssignments.put(employee, role);
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

    public int getShiftId() { return shiftId; }

    /** @return the branch this shift belongs to */
    public int getBranchId() { return branchId; }

    /** @return the date of this shift */
    public LocalDate getDate() { return date; }

    /** @return MORNING or EVENING */
    public ShiftType getShiftType() { return shiftType; }
}
