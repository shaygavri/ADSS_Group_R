package RepositoryLayer;

import DTO.RoleDTO;
import DAO.RoleDAO;
import DomainLayer.Role;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleRepository {
    private final RoleDAO roleDAO;

    public RoleRepository() {
        this.roleDAO = new RoleDAO();
    }

    public void insert(Role role) {
        try {
            roleDAO.insert(new RoleDTO(role.getRoleID(), role.getRoleName(), role.getDescription()));
        } catch (SQLException e) {
            throw new RuntimeException("failed to save role " + role.getRoleID(), e);
        }
    }

    public void update(Role role) {
        try {
            roleDAO.update(new RoleDTO(role.getRoleID(), role.getRoleName(), role.getDescription()));
        } catch (SQLException e) {
            throw new RuntimeException("failed to update role " + role.getRoleID(), e);
        }
    }

    public List<Role> loadAll() {
        try {
            List<Role> roles = new ArrayList<>();
            for (RoleDTO dto : roleDAO.selectAll()) {
                roles.add(new Role(dto.getRoleId(), dto.getRoleName(), dto.getDescription()));
            }
            return roles;
        } catch (SQLException e) {
            throw new RuntimeException("failed to load roles", e);
        }
    }
}
