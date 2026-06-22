package RepositoryLayer;

import DTO.BranchDTO;
import DataAccessLayer.BranchDAO;
import DomainLayer.Branch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BranchRepository {
    private final BranchDAO branchDAO;

    public BranchRepository() {
        this.branchDAO = new BranchDAO();
    }

    public void insert(Branch branch) {
        try {
            branchDAO.insert(new BranchDTO(branch.getId()));
        } catch (SQLException e) {
            throw new RuntimeException("failed to save branch " + branch.getId(), e);
        }
    }

    public void delete(int branchId) {
        try {
            branchDAO.delete(branchId);
        } catch (SQLException e) {
            throw new RuntimeException("failed to delete branch " + branchId, e);
        }
    }

    public List<Branch> loadAll() {
        try {
            List<Branch> branches = new ArrayList<>();
            for (BranchDTO dto : branchDAO.selectAll()) {
                branches.add(new Branch(dto.getBranchId()));
            }
            return branches;
        } catch (SQLException e) {
            throw new RuntimeException("failed to load branches", e);
        }
    }
}
