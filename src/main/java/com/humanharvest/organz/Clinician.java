package com.humanharvest.organz;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.humanharvest.organz.utilities.enums.Region;

/**
 * The main Clinician class.
 */
@Entity
@Table
public class Clinician {

    @Id
    private Integer staffId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String workAddress;
    private String password;

    @Enumerated(EnumType.STRING)
    private Region region;

    private final LocalDateTime createdOn;
    private LocalDateTime modifiedOn;

    @ElementCollection
    private List<String> updateLog = new ArrayList<>();

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

    private void addUpdate(String function) {
        LocalDateTime timestamp = LocalDateTime.now();
        updateLog.add(String.format("%s; updated %s", timestamp, function));
        modifiedOn = LocalDateTime.now();
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public List<String> getUpdateLog() {
        return updateLog;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        addUpdate("firstName");
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        addUpdate("lastName");
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
        addUpdate("middleName");
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
        addUpdate("workAddress");
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
        addUpdate("region");
    }

    public int getStaffId() {
        return staffId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        addUpdate("password");
    }

    public LocalDateTime getModifiedOn() {
        return modifiedOn;
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
}
