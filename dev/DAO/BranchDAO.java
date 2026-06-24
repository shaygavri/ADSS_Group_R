package DAO;

import DTO.BranchDTO;
import DataAccessLayer.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BranchDAO {

    public void insert(BranchDTO branch) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "INSERT INTO branch (branch_id) VALUES (?);";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branch.getBranchId());
            ps.executeUpdate();
        }
    }

    public void delete(int branchId) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "DELETE FROM branch WHERE branch_id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.executeUpdate();
        }
    }

    public BranchDTO select(int branchId) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT branch_id FROM branch WHERE branch_id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new BranchDTO(rs.getInt("branch_id"));
                }
                return null;
            }
        }
    }

    public List<BranchDTO> selectAll() throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT branch_id FROM branch;";
        List<BranchDTO> branches = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                branches.add(new BranchDTO(rs.getInt("branch_id")));
            }
        }
        return branches;
    }
}
