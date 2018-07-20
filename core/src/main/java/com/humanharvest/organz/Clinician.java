package com.humanharvest.organz;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.views.client.Views;

/**
 * The main Clinician class.
 */
@Entity
@Table
public class Clinician implements ConcurrencyControlledEntity {

    @Id
    @JsonView(Views.Overview.class)
    private Integer staffId;
    @JsonView(Views.Overview.class)
    private String firstName;
    @JsonView(Views.Overview.class)
    private String lastName;
    @JsonView(Views.Overview.class)
    private String middleName;
    @JsonView(Views.Overview.class)
    private String workAddress;
    @JsonView(Views.Details.class)
    private String password;

    @Enumerated(EnumType.STRING)
    @JsonView(Views.Overview.class)
    private Region region;

    @JsonView(Views.Details.class)
    private final LocalDateTime createdOn;
    @JsonView(Views.Details.class)
    private LocalDateTime modifiedOn;

    @OneToMany(
            cascade = CascadeType.ALL
    )
    private List<HistoryItem> changesHistory = new ArrayList<>();

    protected Clinician() {
        createdOn = LocalDateTime.now();
    }

    /**
     * Create a new Clinician object
     * @param firstName First name string
     * @param middleName Middle name(s). May be null
     * @param lastName Last name string
     * @param workAddress Address string
     * @param region Region from the Region ENUM
     * @param staffId The unique staffId. Should be checked using the ClinicianManager to ensure uniqueness
     * @param password The clinicians password for logins. Stored in plaintext
     */
    public Clinician(String firstName, String middleName, String lastName, String workAddress, Region region,
            int staffId, String password) {
        createdOn = LocalDateTime.now();

        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.workAddress = workAddress;
        this.region = region;
        this.staffId = staffId;
        this.password = password;
    }

    public boolean isPasswordValid(String password) {
        return this.password.equals(password);
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateModifiedTimestamp();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateModifiedTimestamp();
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
        updateModifiedTimestamp();
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
        updateModifiedTimestamp();
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
        updateModifiedTimestamp();
    }

    public int getStaffId() {
        return staffId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        updateModifiedTimestamp();
    }

    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    private void updateModifiedTimestamp() {
        modifiedOn = LocalDateTime.now();
    }

    /**
     * Get the full name of the clinician concatenating their names
     * @return The full name string
     */
    public String getFullName() {
        String fullName = firstName + " ";
        if (middleName != null) {
            fullName += middleName + " ";
        }
        fullName += lastName;
        return fullName;
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
     * Clinician objects are identified by their staffID
     * @param obj The object to compare
     * @return If the Clinician is a match
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Clinician)) {
            return false;
        }
        Clinician clinician = (Clinician) obj;
        return clinician.staffId.equals(this.staffId);
    }

    /**
     * Clinician objects are identified by their staffID
     */
    @Override
    public int hashCode() {
        return staffId;
    }

    public String getETag() {
        if (modifiedOn == null) {
            return "\"" + String.valueOf(createdOn.hashCode()) + "\"";
        } else {
            return "\"" + String.valueOf(modifiedOn.hashCode()) + "\"";
        }
    }
}
