package DAO;

import DTO.ShiftAssignmentDTO;
import DataAccessLayer.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// shiftId in ShiftAssignmentDTO is the surrogate shift_pk; employeeId is the employee user_name
public class ShiftAssignmentDAO {

    public void insert(ShiftAssignmentDTO assignment) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "INSERT INTO shift_assignment (shift_pk, user_name, role_id) VALUES (?, ?, ?);";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignment.getShiftId());
            ps.setString(2, assignment.getEmployeeId());
            ps.setInt(3, assignment.getRoleId());
            ps.executeUpdate();
        }
    }

    public void update(ShiftAssignmentDTO assignment) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "UPDATE shift_assignment SET role_id = ? WHERE shift_pk = ? AND user_name = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignment.getRoleId());
            ps.setInt(2, assignment.getShiftId());
            ps.setString(3, assignment.getEmployeeId());
            ps.executeUpdate();
        }
    }

    public void delete(int shiftPk, String userName) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "DELETE FROM shift_assignment WHERE shift_pk = ? AND user_name = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shiftPk);
            ps.setString(2, userName);
            ps.executeUpdate();
        }
    }

    public void deleteByShift(int shiftPk) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "DELETE FROM shift_assignment WHERE shift_pk = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shiftPk);
            ps.executeUpdate();
        }
    }

    public List<ShiftAssignmentDTO> selectByShift(int shiftPk) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT shift_pk, user_name, role_id FROM shift_assignment WHERE shift_pk = ?;";
        List<ShiftAssignmentDTO> assignments = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shiftPk);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    assignments.add(mapRow(rs));
                }
            }
        }
        return assignments;
    }

    public List<ShiftAssignmentDTO> selectAll() throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT shift_pk, user_name, role_id FROM shift_assignment;";
        List<ShiftAssignmentDTO> assignments = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                assignments.add(mapRow(rs));
            }
        }
        return assignments;
    }

    private ShiftAssignmentDTO mapRow(ResultSet rs) throws SQLException {
        return new ShiftAssignmentDTO(
                rs.getInt("shift_pk"),
                rs.getString("user_name"),
                rs.getInt("role_id"));
    }
}
