package seng302;

import java.time.LocalDateTime;

/**
 * Object to display an action a user takes when using the system. It includes the
 * type of action, details of the action, and the timestamp for the action.
 */
public class Action {

    private String type;
    private String details;
    private LocalDateTime timestamp;

    public Action(String type, String details) {
        this.type = type;
        this.details = details;
        timestamp = LocalDateTime.now();
    }
}
