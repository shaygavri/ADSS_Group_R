package DAO;

import DTO.AvailabilityDTO;
import DataAccessLayer.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AvailabilityDAO {

    public void insert(AvailabilityDTO availability) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "INSERT INTO employee_availability (user_name, shift_index) VALUES (?, ?);";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, availability.getEmployeeId());
            ps.setInt(2, availability.getShiftIndex());
            ps.executeUpdate();
        }
    }

    public void delete(String userName, int shiftIndex) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "DELETE FROM employee_availability WHERE user_name = ? AND shift_index = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            ps.setInt(2, shiftIndex);
            ps.executeUpdate();
        }
    }

    public void deleteByEmployee(String userName) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "DELETE FROM employee_availability WHERE user_name = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            ps.executeUpdate();
        }
    }

    public List<AvailabilityDTO> selectByEmployee(String userName) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT user_name, shift_index FROM employee_availability WHERE user_name = ?;";
        List<AvailabilityDTO> availabilities = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    availabilities.add(mapRow(rs));
                }
            }
        }
        return availabilities;
    }

    public List<AvailabilityDTO> selectAll() throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT user_name, shift_index FROM employee_availability;";
        List<AvailabilityDTO> availabilities = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                availabilities.add(mapRow(rs));
            }
        }
        return availabilities;
    }

    private AvailabilityDTO mapRow(ResultSet rs) throws SQLException {
        return new AvailabilityDTO(
                rs.getString("user_name"),
                rs.getInt("shift_index"));
    }
}
