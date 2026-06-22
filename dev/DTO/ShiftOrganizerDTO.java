package DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ShiftOrganizerDTO {
    private int branchId;
    private LocalDate weekStartDate;
    private boolean availabilityChangesAllowed;
    private boolean published;
    private LocalDateTime requirementsPublishedAt;
    private LocalDateTime availabilityDeadline;

    public ShiftOrganizerDTO(int branchId, LocalDate weekStartDate, boolean availabilityChangesAllowed,
                             boolean published, LocalDateTime requirementsPublishedAt,
                             LocalDateTime availabilityDeadline) {
        this.branchId = branchId;
        this.weekStartDate = weekStartDate;
        this.availabilityChangesAllowed = availabilityChangesAllowed;
        this.published = published;
        this.requirementsPublishedAt = requirementsPublishedAt;
        this.availabilityDeadline = availabilityDeadline;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    public boolean isAvailabilityChangesAllowed() {
        return availabilityChangesAllowed;
    }

    public void setAvailabilityChangesAllowed(boolean availabilityChangesAllowed) {
        this.availabilityChangesAllowed = availabilityChangesAllowed;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public LocalDateTime getRequirementsPublishedAt() {
        return requirementsPublishedAt;
    }

    public void setRequirementsPublishedAt(LocalDateTime requirementsPublishedAt) {
        this.requirementsPublishedAt = requirementsPublishedAt;
    }

    public LocalDateTime getAvailabilityDeadline() {
        return availabilityDeadline;
    }

    public void setAvailabilityDeadline(LocalDateTime availabilityDeadline) {
        this.availabilityDeadline = availabilityDeadline;
    }
}
