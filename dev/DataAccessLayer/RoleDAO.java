package DataAccessLayer;

import DTO.RoleDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    public void insert(RoleDTO role) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "INSERT INTO role (role_id, role_name, description) VALUES (?, ?, ?);";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, role.getRoleId());
            ps.setString(2, role.getRoleName());
            ps.setString(3, role.getDescription());
            ps.executeUpdate();
        }
    }

    public void update(RoleDTO role) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "UPDATE role SET role_name = ?, description = ? WHERE role_id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.getRoleName());
            ps.setString(2, role.getDescription());
            ps.setInt(3, role.getRoleId());
            ps.executeUpdate();
        }
    }

    public void delete(int roleId) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "DELETE FROM role WHERE role_id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.executeUpdate();
        }
    }

    public RoleDTO select(int roleId) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT role_id, role_name, description FROM role WHERE role_id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public List<RoleDTO> selectAll() throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT role_id, role_name, description FROM role;";
        List<RoleDTO> roles = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(mapRow(rs));
            }
        }
        return roles;
    }

    private RoleDTO mapRow(ResultSet rs) throws SQLException {
        return new RoleDTO(
                rs.getInt("role_id"),
                rs.getString("role_name"),
                rs.getString("description"));
    }
}
