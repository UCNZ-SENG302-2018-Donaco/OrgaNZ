package com.humanharvest.organz;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.views.client.Views;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table
public class Administrator implements ConcurrencyControlledEntity {
    @Id
    @JsonView(Views.Overview.class)
    private String username;
    private String password;

    @JsonView(Views.Overview.class)
    private final Instant createdTimestamp;
    @JsonView(Views.Overview.class)
    private Instant modifiedTimestamp;

    @OneToMany(
            cascade = CascadeType.ALL
    )
    private List<HistoryItem> changesHistory = new ArrayList<>();

    protected Administrator() {
        createdTimestamp = Instant.now();
    }

    /**
     * Create a new Administrator object
     *
     * @param username The unique username. Should be checked using the AdministratorManager to ensure uniqueness
     * @param password The administrators password for logins. Stored in plaintext
     */
    public Administrator(String username, String password) {
        createdTimestamp = Instant.now();

        this.username = username;
        this.password = password;
    }

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String newPassword) {
        password = newPassword;
        updateModifiedTimestamp();
    }

    /**
     * Returns true if the given password matches the stored password.
     *
     * @param testPassword The given password to check.
     * @return If the two passwords are equal.
     */
    public boolean isPasswordValid(String testPassword) {
        return password.equals(testPassword);
    }

    public Instant getModifiedTimestamp() {
        return modifiedTimestamp;
    }

    @Override
    public String getETag() {
        if (modifiedTimestamp == null) {
            return String.format("\"%d\"", createdTimestamp.hashCode());
        } else {
            return String.format("\"%d\"", modifiedTimestamp.hashCode());
        }
    }

    private void updateModifiedTimestamp() {
        modifiedTimestamp = Instant.now();
    }

    public List<HistoryItem> getChangesHistory() {
        return Collections.unmodifiableList(changesHistory);
    }

    public void addToChangesHistory(HistoryItem historyItem) {
        changesHistory.add(historyItem);
    }

    public void removeFromChangesHistory(HistoryItem historyItem) {
        changesHistory.remove(historyItem);
    }

    /**
     * Administrator objects are identified by their administratorId
     *
     * @param obj The object to compare
     * @return If the Administrator is a match
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Administrator)) {
            return false;
        }
        Administrator administrator = (Administrator) obj;
        return administrator.username.equals(username);
    }

    /**
     * Administrator objects are identified by their administratorId
     */
    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
