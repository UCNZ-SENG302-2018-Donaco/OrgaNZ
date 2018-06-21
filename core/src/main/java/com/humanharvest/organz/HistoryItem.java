package com.humanharvest.organz;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Object to display an action a user takes when using the system. It includes the
 * type of action, details of the action, and the timestamp for the action.
 */
public class HistoryItem {

    private String type;
    private String details;
    private LocalDateTime timestamp;

    @JsonCreator
    private HistoryItem() {
    }

    public HistoryItem(String type, String details) {
        this.type = type;
        this.details = details;
        timestamp = LocalDateTime.now();
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
}
