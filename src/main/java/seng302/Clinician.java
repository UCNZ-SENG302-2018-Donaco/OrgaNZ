package seng302;

import java.time.LocalDateTime;
import java.util.ArrayList;

import seng302.Utilities.Enums.Region;

/**
 * The main Clinician class.
 */
public class Clinician {

    private final LocalDateTime created_on;
    private LocalDateTime modified_on;
    private ArrayList<String> updateLog = new ArrayList<>();

    private String firstName;
    private String lastName;
    private String middleName;

    private String workAddress;
    private Region region;

    private int staffId;
    private String password;

    /**
     * Create a new Clinician object
     * @param firstName     First name string
     * @param middleName    Middle name(s). May be null
     * @param lastName      Last name string
     * @param workAddress   Address string
     * @param region        Region from the Region ENUM
     * @param staffId       The unique staffId. Should be checked using the ClinicianManager to ensure uniqueness
     * @param password      The clinicians password for logins. Stored in plaintext
     */
    public Clinician(String firstName, String middleName, String lastName, String workAddress, Region region, int staffId, String password) {
        created_on = LocalDateTime.now();

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
        modified_on = LocalDateTime.now();
    }

    public LocalDateTime getCreated_on() {
        return created_on;
    }

    public ArrayList<String> getUpdateLog() {
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

    public void setStaffId(int staffId) {
        this.staffId = staffId;
        addUpdate("staffId");
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        addUpdate("password");
    }
    
    public LocalDateTime getModified_on() {
        return modified_on;
    }
}
