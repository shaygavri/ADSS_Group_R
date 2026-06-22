package DataAccessLayer;

import DTO.EmployeeRoleDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRoleDAO {

    public void insert(EmployeeRoleDTO employeeRole) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "INSERT INTO employee_role (user_name, role_id) VALUES (?, ?);";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeRole.getEmployeeId());
            ps.setInt(2, employeeRole.getRoleId());
            ps.executeUpdate();
        }
    }

    public void delete(String userName, int roleId) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "DELETE FROM employee_role WHERE user_name = ? AND role_id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            ps.setInt(2, roleId);
            ps.executeUpdate();
        }
    }

    public void deleteByEmployee(String userName) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "DELETE FROM employee_role WHERE user_name = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            ps.executeUpdate();
        }
    }

    public List<EmployeeRoleDTO> selectByEmployee(String userName) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT user_name, role_id FROM employee_role WHERE user_name = ?;";
        List<EmployeeRoleDTO> employeeRoles = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    employeeRoles.add(mapRow(rs));
                }
            }
        }
        return employeeRoles;
    }

    public List<EmployeeRoleDTO> selectAll() throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT user_name, role_id FROM employee_role;";
        List<EmployeeRoleDTO> employeeRoles = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                employeeRoles.add(mapRow(rs));
            }
        }
        return employeeRoles;
    }

    private EmployeeRoleDTO mapRow(ResultSet rs) throws SQLException {
        return new EmployeeRoleDTO(
                rs.getString("user_name"),
                rs.getInt("role_id"));
    }
}
