package DataAccessLayer;

import DTO.ShiftDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// The shift table has a surrogate key shift_pk. ShiftDTO.shiftId holds the 0-13 index within the week.
// week_category (CURRENT/NEXT/HISTORY) and historyWeekIndex are passed in because ShiftDTO does not carry them.
public class ShiftDAO {

    public int insert(ShiftDTO shift, String weekCategory, Integer historyWeekIndex) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "INSERT INTO shift (branch_id, week_category, history_week_index, shift_index, " +
                "shift_date, shift_type, closed_day) VALUES (?, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, shift.getBranchId());
            ps.setString(2, weekCategory);
            setNullableInt(ps, 3, historyWeekIndex);
            ps.setInt(4, shift.getShiftId());
            ps.setString(5, shift.getDate().toString());
            ps.setString(6, shift.getShiftType());
            ps.setInt(7, shift.isClosedDay() ? 1 : 0);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                return -1;
            }
        }
    }

    public void update(int shiftPk, ShiftDTO shift) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "UPDATE shift SET branch_id = ?, shift_index = ?, shift_date = ?, shift_type = ?, " +
                "closed_day = ? WHERE shift_pk = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shift.getBranchId());
            ps.setInt(2, shift.getShiftId());
            ps.setString(3, shift.getDate().toString());
            ps.setString(4, shift.getShiftType());
            ps.setInt(5, shift.isClosedDay() ? 1 : 0);
            ps.setInt(6, shiftPk);
            ps.executeUpdate();
        }
    }

    public void delete(int shiftPk) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "DELETE FROM shift WHERE shift_pk = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shiftPk);
            ps.executeUpdate();
        }
    }

    public void deleteByBranch(int branchId) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "DELETE FROM shift WHERE branch_id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.executeUpdate();
        }
    }

    public ShiftDTO select(int shiftPk) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT * FROM shift WHERE shift_pk = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shiftPk);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public List<ShiftDTO> selectByBranchAndWeek(int branchId, String weekCategory, Integer historyWeekIndex)
            throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT * FROM shift WHERE branch_id = ? AND week_category = ? AND " +
                (historyWeekIndex == null ? "history_week_index IS NULL" : "history_week_index = ?") +
                " ORDER BY shift_index;";
        List<ShiftDTO> shifts = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setString(2, weekCategory);
            if (historyWeekIndex != null) {
                ps.setInt(3, historyWeekIndex);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    shifts.add(mapRow(rs));
                }
            }
        }
        return shifts;
    }

    // resolves the surrogate shift_pk for a shift identified by its week position
    public int findShiftPk(int branchId, String weekCategory, Integer historyWeekIndex, int shiftIndex)
            throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT shift_pk FROM shift WHERE branch_id = ? AND week_category = ? AND " +
                (historyWeekIndex == null ? "history_week_index IS NULL" : "history_week_index = ?") +
                " AND shift_index = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            ps.setInt(index++, branchId);
            ps.setString(index++, weekCategory);
            if (historyWeekIndex != null) {
                ps.setInt(index++, historyWeekIndex);
            }
            ps.setInt(index, shiftIndex);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("shift_pk");
                }
                return -1;
            }
        }
    }

    private ShiftDTO mapRow(ResultSet rs) throws SQLException {
        ShiftDTO dto = new ShiftDTO(
                rs.getInt("shift_index"),
                rs.getInt("branch_id"),
                LocalDate.parse(rs.getString("shift_date")),
                rs.getString("shift_type"),
                rs.getInt("closed_day") == 1);
        dto.setShiftPk(rs.getInt("shift_pk"));
        return dto;
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, java.sql.Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }
}
