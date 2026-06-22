package RepositoryLayer;

import DTO.ShiftAssignmentDTO;
import DTO.ShiftDTO;
import DTO.ShiftOrganizerDTO;
import DTO.ShiftRequirementDTO;
import DataAccessLayer.ShiftAssignmentDAO;
import DataAccessLayer.ShiftDAO;
import DataAccessLayer.ShiftOrganizerDAO;
import DataAccessLayer.ShiftRequirementDAO;
import DomainLayer.Employee;
import DomainLayer.Role;
import DomainLayer.Shift;
import DomainLayer.ShiftOrganizer;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShiftRepository {
    private static final String CURRENT = "CURRENT";
    private static final String NEXT = "NEXT";
    private static final String HISTORY = "HISTORY";

    private final ShiftDAO shiftDAO;
    private final ShiftOrganizerDAO shiftOrganizerDAO;
    private final ShiftRequirementDAO shiftRequirementDAO;
    private final ShiftAssignmentDAO shiftAssignmentDAO;

    public ShiftRepository() {
        this.shiftDAO = new ShiftDAO();
        this.shiftOrganizerDAO = new ShiftOrganizerDAO();
        this.shiftRequirementDAO = new ShiftRequirementDAO();
        this.shiftAssignmentDAO = new ShiftAssignmentDAO();
    }

    public Map<Integer, ShiftOrganizer> loadAll(List<Role> knownRoles, List<Employee> knownEmployees) {
        try {
            Map<Integer, ShiftOrganizer> organizers = new HashMap<>();
            for (ShiftOrganizerDTO dto : shiftOrganizerDAO.selectAll()) {
                ShiftOrganizer organizer = toDomain(dto, knownRoles, knownEmployees);
                organizers.put(dto.getBranchId(), organizer);
            }
            return organizers;
        } catch (SQLException e) {
            throw new RuntimeException("failed to load shift organizers", e);
        }
    }

    public void insertOrUpdate(ShiftOrganizer organizer) {
        try {
            int branchId = organizer.getBranchId();
            ShiftOrganizerDTO dto = toOrganizerDTO(organizer);

            if (shiftOrganizerDAO.select(branchId) == null) {
                shiftOrganizerDAO.insert(dto);
            } else {
                shiftOrganizerDAO.update(dto);
            }

            // nuke and rebuild all shifts for this branch (cascades to requirements + assignments)
            shiftDAO.deleteByBranch(branchId);

            saveWeek(branchId, CURRENT, null, organizer.getCurrentWeekShifts());
            saveWeek(branchId, NEXT, null, organizer.getNextWeekShifts());

            List<Shift[]> history = organizer.getShiftsHistory();
            for (int i = 0; i < history.size(); i++) {
                saveWeek(branchId, HISTORY, i, history.get(i));
            }
        } catch (SQLException e) {
            throw new RuntimeException("failed to save shift organizer for branch " + organizer.getBranchId(), e);
        }
    }

    private void saveWeek(int branchId, String weekCategory, Integer historyIndex, Shift[] shifts)
            throws SQLException {
        for (Shift shift : shifts) {
            int shiftPk = shiftDAO.insert(toShiftDTO(shift), weekCategory, historyIndex);

            for (Map.Entry<Role, Integer> entry : shift.getRequirements().entrySet()) {
                shiftRequirementDAO.insert(
                        new ShiftRequirementDTO(shiftPk, entry.getKey().getRoleID(), entry.getValue()));
            }

            for (Map.Entry<Employee, Role> entry : shift.getEmployeeRoleAssignments().entrySet()) {
                shiftAssignmentDAO.insert(
                        new ShiftAssignmentDTO(shiftPk, entry.getKey().getUserName(), entry.getValue().getRoleID()));
            }
        }
    }

    private ShiftOrganizer toDomain(ShiftOrganizerDTO dto, List<Role> knownRoles, List<Employee> knownEmployees)
            throws SQLException {
        int branchId = dto.getBranchId();

        Shift[] currentShifts = loadWeek(branchId, CURRENT, null, knownRoles, knownEmployees);
        Shift[] nextShifts = loadWeek(branchId, NEXT, null, knownRoles, knownEmployees);

        ShiftOrganizer organizer = new ShiftOrganizer(branchId, currentShifts, nextShifts);
        organizer.setAvailabilityChangesAllowed(dto.isAvailabilityChangesAllowed());
        organizer.setPublished(dto.isPublished());
        organizer.setRequirementsPublishedAt(dto.getRequirementsPublishedAt());
        organizer.setAvailabilityDeadline(dto.getAvailabilityDeadline());

        // load history weeks in index order (0 = oldest)
        int i = 0;
        while (true) {
            List<ShiftDTO> weekDTOs = shiftDAO.selectByBranchAndWeek(branchId, HISTORY, i);
            if (weekDTOs.isEmpty()) break;
            organizer.addHistoryWeek(toShifts(weekDTOs, knownRoles, knownEmployees));
            i++;
        }

        return organizer;
    }

    private Shift[] loadWeek(int branchId, String weekCategory, Integer historyIndex,
                              List<Role> knownRoles, List<Employee> knownEmployees) throws SQLException {
        List<ShiftDTO> dtos = shiftDAO.selectByBranchAndWeek(branchId, weekCategory, historyIndex);
        if (dtos.size() == 14) {
            return toShifts(dtos, knownRoles, knownEmployees);
        }
        return null; // ShiftOrganizer constructor will generate fresh shifts
    }

    private Shift[] toShifts(List<ShiftDTO> dtos, List<Role> knownRoles, List<Employee> knownEmployees)
            throws SQLException {
        Shift[] shifts = new Shift[14];
        for (ShiftDTO dto : dtos) {
            shifts[dto.getShiftId()] = toShift(dto, knownRoles, knownEmployees);
        }
        return shifts;
    }

    private Shift toShift(ShiftDTO dto, List<Role> knownRoles, List<Employee> knownEmployees)
            throws SQLException {
        Shift shift = new Shift(dto.getShiftId(), dto.getBranchId(), dto.getDate(),
                Shift.ShiftType.valueOf(dto.getShiftType()));

        // load requirements before setClosedDay so addRequirement is not blocked
        int shiftPk = dto.getShiftPk();
        for (ShiftRequirementDTO req : shiftRequirementDAO.selectByShift(shiftPk)) {
            Role role = findRole(knownRoles, req.getRoleId());
            if (role != null) {
                shift.addRequirement(role, req.getRequiredAmount());
            }
        }

        shift.setClosedDay(dto.isClosedDay());

        for (ShiftAssignmentDTO assignment : shiftAssignmentDAO.selectByShift(shiftPk)) {
            Employee employee = findEmployee(knownEmployees, assignment.getEmployeeId());
            Role role = findRole(knownRoles, assignment.getRoleId());
            if (employee != null && role != null) {
                shift.assignEmployee(employee, role);
            }
        }

        return shift;
    }

    private Role findRole(List<Role> knownRoles, int roleId) {
        for (Role role : knownRoles) {
            if (role.getRoleID() == roleId) return role;
        }
        return null;
    }

    private Employee findEmployee(List<Employee> knownEmployees, String userName) {
        for (Employee employee : knownEmployees) {
            if (employee.getUserName().equalsIgnoreCase(userName)) return employee;
        }
        return null;
    }

    private ShiftOrganizerDTO toOrganizerDTO(ShiftOrganizer organizer) {
        return new ShiftOrganizerDTO(
                organizer.getBranchId(),
                organizer.getWeekStartDate(),
                organizer.isAvailabilityChangesAllowed(),
                organizer.isPublished(),
                organizer.getRequirementsPublishedAt(),
                organizer.getAvailabilityDeadline());
    }

    private ShiftDTO toShiftDTO(Shift shift) {
        return new ShiftDTO(
                shift.getShiftId(),
                shift.getBranchId(),
                shift.getDate(),
                shift.getShiftType().name(),
                shift.isClosedDay());
    }
}
