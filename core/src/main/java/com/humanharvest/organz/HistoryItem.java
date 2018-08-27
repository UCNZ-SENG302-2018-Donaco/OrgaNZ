package com.humanharvest.organz;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Object to display an action a user takes when using the system. It includes the
 * type of action, details of the action, and the timestamp for the action.
 */
@Entity
@Table
public class HistoryItem {

    @Id
    @GeneratedValue
    private Long id;
    private String type;
    @Column(columnDefinition = "text")
    private String details;
    private LocalDateTime timestamp;

    @JsonCreator
    protected HistoryItem() {
    }

    public HistoryItem(String type, String details) {
        this.type = type;
        this.details = details;
        timestamp = LocalDateTime.now();
    }

    private HistoryItem(String type, String details, LocalDateTime timestamp) {
        this.type = type;
        this.details = details;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof HistoryItem)) {
            return false;
        }
        HistoryItem other = (HistoryItem) obj;

        return Objects.equals(other.details, details) &&
                Objects.equals(other.type, type) &&
                Objects.equals(other.timestamp, timestamp);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    public HistoryItem copy() {
        return new HistoryItem(type, details, timestamp);
    }
}
