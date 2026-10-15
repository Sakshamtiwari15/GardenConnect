package com.gardenconnect.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Domain model representing a personal gardening project managed by a gardener.
 */
public class GardeningProject implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String title;
    private String description;
    private String plantCropType;
    private Date startDate;
    private Date targetCompletionDate;
    private ProjectStatus status;
    private int progressPercentage;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public GardeningProject() {
        this.status = ProjectStatus.PLANNED;
        this.progressPercentage = 0;
    }

    public GardeningProject(Long userId, String title, String description, String plantCropType,
                            Date startDate, Date targetCompletionDate, ProjectStatus status,
                            int progressPercentage, String notes) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.plantCropType = plantCropType;
        this.startDate = startDate;
        this.targetCompletionDate = targetCompletionDate;
        this.status = (status != null) ? status : ProjectStatus.PLANNED;
        this.progressPercentage = Math.max(0, Math.min(100, progressPercentage));
        this.notes = notes;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlantCropType() {
        return plantCropType;
    }

    public void setPlantCropType(String plantCropType) {
        this.plantCropType = plantCropType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getTargetCompletionDate() {
        return targetCompletionDate;
    }

    public void setTargetCompletionDate(Date targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        if (progressPercentage < 0) {
            this.progressPercentage = 0;
        } else if (progressPercentage > 100) {
            this.progressPercentage = 100;
        } else {
            this.progressPercentage = progressPercentage;
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GardeningProject that = (GardeningProject) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "GardeningProject{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", progress=" + progressPercentage + "%" +
                '}';
    }
}
