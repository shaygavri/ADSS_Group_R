package DataAccessLayer;

import DTO.ShiftOrganizerDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShiftOrganizerDAO {

    public void insert(ShiftOrganizerDTO organizer) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "INSERT INTO shift_organizer (branch_id, week_start_date, availability_changes_allowed, " +
                "is_published, requirements_published_at, availability_deadline) VALUES (?, ?, ?, ?, ?, ?);";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, organizer.getBranchId());
            ps.setString(2, organizer.getWeekStartDate().toString());
            ps.setInt(3, organizer.isAvailabilityChangesAllowed() ? 1 : 0);
            ps.setInt(4, organizer.isPublished() ? 1 : 0);
            ps.setString(5, dateTimeToString(organizer.getRequirementsPublishedAt()));
            ps.setString(6, dateTimeToString(organizer.getAvailabilityDeadline()));
            ps.executeUpdate();
        }
    }

    public void update(ShiftOrganizerDTO organizer) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "UPDATE shift_organizer SET week_start_date = ?, availability_changes_allowed = ?, " +
                "is_published = ?, requirements_published_at = ?, availability_deadline = ? WHERE branch_id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, organizer.getWeekStartDate().toString());
            ps.setInt(2, organizer.isAvailabilityChangesAllowed() ? 1 : 0);
            ps.setInt(3, organizer.isPublished() ? 1 : 0);
            ps.setString(4, dateTimeToString(organizer.getRequirementsPublishedAt()));
            ps.setString(5, dateTimeToString(organizer.getAvailabilityDeadline()));
            ps.setInt(6, organizer.getBranchId());
            ps.executeUpdate();
        }
    }

    public void delete(int branchId) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "DELETE FROM shift_organizer WHERE branch_id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.executeUpdate();
        }
    }

    public ShiftOrganizerDTO select(int branchId) throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT * FROM shift_organizer WHERE branch_id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public List<ShiftOrganizerDTO> selectAll() throws SQLException {
        Connection conn = DatabaseManager.getInstance().getConnection();
        String sql = "SELECT * FROM shift_organizer;";
        List<ShiftOrganizerDTO> organizers = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                organizers.add(mapRow(rs));
            }
        }
        return organizers;
    }

    private ShiftOrganizerDTO mapRow(ResultSet rs) throws SQLException {
        return new ShiftOrganizerDTO(
                rs.getInt("branch_id"),
                LocalDate.parse(rs.getString("week_start_date")),
                rs.getInt("availability_changes_allowed") == 1,
                rs.getInt("is_published") == 1,
                stringToDateTime(rs.getString("requirements_published_at")),
                stringToDateTime(rs.getString("availability_deadline")));
    }

    private String dateTimeToString(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toString();
    }

    private LocalDateTime stringToDateTime(String text) {
        return text == null ? null : LocalDateTime.parse(text);
    }
}
