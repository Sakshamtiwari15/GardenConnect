package com.gardenconnect.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Domain model representing a database-backed system runtime configuration setting.
 */
public class SystemSetting implements Serializable {
    private static final long serialVersionUID = 1L;

    private String key;
    private String value;
    private String description;
    private String dataType;
    private Timestamp updatedAt;

    public SystemSetting() {}

    public SystemSetting(String key, String value, String description, String dataType) {
        this.key = key;
        this.value = value;
        this.description = description;
        this.dataType = dataType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean asBoolean(boolean defaultValue) {
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value.trim());
    }

    public int asInt(int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemSetting that = (SystemSetting) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "SystemSetting{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
