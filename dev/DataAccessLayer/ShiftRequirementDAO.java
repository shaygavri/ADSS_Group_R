package DataAccessLayer;

import DTO.ShiftRequirementDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// shiftId in ShiftRequirementDTO is the surrogate shift_pk returned by ShiftDAO.insert
public class ShiftRequirementDAO {

    public void insert(ShiftRequirementDTO requirement) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "INSERT INTO shift_requirement (shift_pk, role_id, required_count) VALUES (?, ?, ?);";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requirement.getShiftId());
            ps.setInt(2, requirement.getRoleId());
            ps.setInt(3, requirement.getRequiredAmount());
            ps.executeUpdate();
        }
    }

    public void update(ShiftRequirementDTO requirement) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "UPDATE shift_requirement SET required_count = ? WHERE shift_pk = ? AND role_id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requirement.getRequiredAmount());
            ps.setInt(2, requirement.getShiftId());
            ps.setInt(3, requirement.getRoleId());
            ps.executeUpdate();
        }
    }

    public void delete(int shiftPk, int roleId) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "DELETE FROM shift_requirement WHERE shift_pk = ? AND role_id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shiftPk);
            ps.setInt(2, roleId);
            ps.executeUpdate();
        }
    }

    public void deleteByShift(int shiftPk) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "DELETE FROM shift_requirement WHERE shift_pk = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shiftPk);
            ps.executeUpdate();
        }
    }

    public List<ShiftRequirementDTO> selectByShift(int shiftPk) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT shift_pk, role_id, required_count FROM shift_requirement WHERE shift_pk = ?;";
        List<ShiftRequirementDTO> requirements = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shiftPk);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requirements.add(mapRow(rs));
                }
            }
        }
        return requirements;
    }

    public List<ShiftRequirementDTO> selectAll() throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT shift_pk, role_id, required_count FROM shift_requirement;";
        List<ShiftRequirementDTO> requirements = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                requirements.add(mapRow(rs));
            }
        }
        return requirements;
    }

    private ShiftRequirementDTO mapRow(ResultSet rs) throws SQLException {
        return new ShiftRequirementDTO(
                rs.getInt("shift_pk"),
                rs.getInt("role_id"),
                rs.getInt("required_count"));
    }
}
