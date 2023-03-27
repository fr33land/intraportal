package org.intraportal.persistence.model;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "MigrationRecord")
@Table(name = "flyway_schema_history")
public class MigrationRecord {

    @Id
    Integer installedRank;

    @Column
    String version;

    @Column
    String description;

    @Column
    String type;

    @Column
    Integer checksum;

    @Column
    String installedBy;

    @CreatedDate
    LocalDateTime installedOn;

    @Column
    Integer executionTime;

    @Column
    Boolean success;

    public Integer getInstalledRank() {
        return installedRank;
    }

    public void setInstalledRank(Integer installedRank) {
        this.installedRank = installedRank;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getChecksum() {
        return checksum;
    }

    public void setChecksum(Integer checksum) {
        this.checksum = checksum;
    }

    public String getInstalledBy() {
        return installedBy;
    }

    public void setInstalledBy(String installedBy) {
        this.installedBy = installedBy;
    }

    public LocalDateTime getInstalledOn() {
        return installedOn;
    }

    public void setInstalledOn(LocalDateTime installedOn) {
        this.installedOn = installedOn;
    }

    public Integer getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Integer executionTime) {
        this.executionTime = executionTime;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MigrationRecord that = (MigrationRecord) o;
        return Objects.equals(installedRank, that.installedRank) && Objects.equals(version, that.version) && Objects.equals(description, that.description) && Objects.equals(type, that.type) && Objects.equals(checksum, that.checksum) && Objects.equals(installedBy, that.installedBy) && Objects.equals(installedOn, that.installedOn) && Objects.equals(executionTime, that.executionTime) && Objects.equals(success, that.success);
    }

    @Override
    public int hashCode() {
        return Objects.hash(installedRank, version, description, type, checksum, installedBy, installedOn, executionTime, success);
    }

    @Override
    public String toString() {
        return "MigrationRecord{" +
                "installedRank=" + installedRank +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", checksum=" + checksum +
                ", installedBy='" + installedBy + '\'' +
                ", installedOn=" + installedOn +
                ", executionTime=" + executionTime +
                ", success=" + success +
                '}';
    }

}
