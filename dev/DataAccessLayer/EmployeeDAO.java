package DataAccessLayer;

import DTO.EmployeeDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public void insert(EmployeeDTO employee) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "INSERT INTO employee (user_name, password, national_id, branch_id, hourly_salary, " +
                "vacation_days, employment_type, driver_license_type, start_contract, end_contract, " +
                "is_logged_in, bank_number, bank_branch_number, bank_account_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            bindEmployee(ps, employee);
            ps.executeUpdate();
        }
    }

    public void update(EmployeeDTO employee) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "UPDATE employee SET password = ?, national_id = ?, branch_id = ?, hourly_salary = ?, " +
                "vacation_days = ?, employment_type = ?, driver_license_type = ?, start_contract = ?, " +
                "end_contract = ?, is_logged_in = ?, bank_number = ?, bank_branch_number = ?, " +
                "bank_account_number = ? WHERE user_name = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employee.getPassword());
            ps.setString(2, employee.getEmployeeId());
            ps.setInt(3, employee.getBranchId());
            ps.setInt(4, employee.getHourlySalary());
            ps.setInt(5, employee.getVacationDays());
            ps.setString(6, employee.getEmploymentType());
            ps.setString(7, employee.getDriverLicenseType());
            ps.setString(8, employee.getStartContract().toString());
            ps.setString(9, employee.getEndContract().toString());
            ps.setInt(10, employee.isLoggedIn() ? 1 : 0);
            ps.setInt(11, employee.getBankNumber());
            ps.setInt(12, employee.getBankBranchNumber());
            ps.setInt(13, employee.getBankAccountNumber());
            ps.setString(14, employee.getUserName());
            ps.executeUpdate();
        }
    }

    public void delete(String userName) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "DELETE FROM employee WHERE user_name = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            ps.executeUpdate();
        }
    }

    public EmployeeDTO select(String userName) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT * FROM employee WHERE user_name = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public List<EmployeeDTO> selectAll() throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT * FROM employee;";
        List<EmployeeDTO> employees = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                employees.add(mapRow(rs));
            }
        }
        return employees;
    }

    private void bindEmployee(PreparedStatement ps, EmployeeDTO employee) throws SQLException {
        ps.setString(1, employee.getUserName());
        ps.setString(2, employee.getPassword());
        ps.setString(3, employee.getEmployeeId());
        ps.setInt(4, employee.getBranchId());
        ps.setInt(5, employee.getHourlySalary());
        ps.setInt(6, employee.getVacationDays());
        ps.setString(7, employee.getEmploymentType());
        ps.setString(8, employee.getDriverLicenseType());
        ps.setString(9, employee.getStartContract().toString());
        ps.setString(10, employee.getEndContract().toString());
        ps.setInt(11, employee.isLoggedIn() ? 1 : 0);
        ps.setInt(12, employee.getBankNumber());
        ps.setInt(13, employee.getBankBranchNumber());
        ps.setInt(14, employee.getBankAccountNumber());
    }

    private EmployeeDTO mapRow(ResultSet rs) throws SQLException {
        String driverLicenseType = rs.getString("driver_license_type");
        return new EmployeeDTO(
                rs.getInt("branch_id"),
                rs.getString("user_name"),
                rs.getString("password"),
                rs.getString("national_id"),
                rs.getInt("bank_number"),
                rs.getInt("bank_branch_number"),
                rs.getInt("bank_account_number"),
                rs.getInt("hourly_salary"),
                rs.getString("employment_type"),
                rs.getInt("vacation_days"),
                LocalDate.parse(rs.getString("start_contract")),
                LocalDate.parse(rs.getString("end_contract")),
                driverLicenseType,
                rs.getInt("is_logged_in") == 1);
    }
}
