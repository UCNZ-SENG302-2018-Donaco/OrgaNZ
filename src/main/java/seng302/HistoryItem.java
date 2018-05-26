package seng302;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

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
    @Expose(serialize = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Client_uid")
    private Client client;
    private String type;
    private String details;
    private LocalDateTime timestamp;

    protected HistoryItem() {
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
