package seng302;

import seng302.Utilities.BloodType;
import seng302.Utilities.Gender;
import seng302.Utilities.Organ;
import seng302.Utilities.OrganAlreadyRegisteredException;

import static java.util.Optional.ofNullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The main donor class.
 *
 *@author Dylan Carlyle, Jack Steel
 *@version sprint 1.
 *date 05/03/2018
 */

public class Donor {

    private final LocalDateTime created_on;
    private LocalDateTime modified_on;

    private String firstName;
    private String lastName;
    private String middleName;

    private String currentAddress;
    private String region;

    private Gender gender;
    private BloodType bloodType;

    private int height;
    private int weight;

    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;

    private Map<Organ, Boolean> organStatus;

    private ArrayList<String> updateLog = new ArrayList<>();

    private int uid;

    public Donor() {

        created_on = LocalDateTime.now();
        modified_on = LocalDateTime.now();
    }

    /**
     * Create a new donor object
     * @param firstName First name string
     * @param middleName Middle name(s). May be null
     * @param lastName Last name string
     * @param dateOfBirth LocalDate formatted date of birth
     * @param uid A unique user ID. Should be queried to ensure uniqueness
     */
    public Donor(String firstName, String middleName, String lastName, LocalDate dateOfBirth, int uid) {
        created_on = LocalDateTime.now();
        modified_on = LocalDateTime.now();

        this.uid = uid;

        gender = Gender.UNSPECIFIED;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;

        initOrgans();
    }

    private void initOrgans() {
        organStatus = new HashMap<>();
        for (Organ o: Organ.values()) {
            organStatus.put(o, false);
        }
    }

    private void addUpdate(String function) {
        LocalDateTime timestamp = LocalDateTime.now();
        updateLog.add(String.format("%s; updated %s", timestamp, function));
    }

    /**
     * Set a single organs donation status
     * @param organ The organ to be set
     * @param value Boolean value to set the status too
     * @throws OrganAlreadyRegisteredException Thrown if the organ is set to true when it already is
     */
    public void setOrganStatus(Organ organ, boolean value) throws OrganAlreadyRegisteredException {
        if (value && organStatus.get(organ)) {
            throw new OrganAlreadyRegisteredException("That organ is already registered");
        }
        addUpdate(organ.toString());
        organStatus.replace(organ, value);
    }

    public Map<Organ, Boolean> getOrganStatus() {
        return organStatus;
    }

    /**
     * Get the donors organ donation status, with a formatted string listing the organs to be donated
     * @return A formatted string listing the organs to be donated
     */
    public String getDonorOrganStatusString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Organ, Boolean> entry : organStatus.entrySet()) {
            if (entry.getValue()) {
                if (builder.length() != 0) {
                    builder.append(", ");
                }
                builder.append(entry.getKey().toString());
            }
        }
        if (builder.length() == 0) {
            return String.format("User: %s. Name: %s %s %s, no organs registered for donation", uid, firstName, ofNullable(middleName).orElse(""), lastName);
        } else {
            return String.format("User: %s. Name: %s %s %s, Donation status: %s", uid, firstName, ofNullable(middleName).orElse(""), lastName, builder.toString());
        }
    }

    /**
     * Returns a preformatted string of the users change history
     * @return Formatted string with newlines
     */
    public String getUpdatesString() {
        StringBuilder out = new StringBuilder(String.format("User: %s. Name: %s %s %s, updates:\n", uid, firstName, ofNullable(middleName).orElse(""), lastName));
        for (String update : updateLog) {
            out.append(update).append('\n');
        }
        return out.toString();
    }

    /**
     * Get a formatted string with the donors user information. Does not include organ donation status
     * @return Formatted string with the donors user information. Does not include organ donation status
     */
    public String getDonorInfoString() {
        return String.format("User: %s. Name: %s %s %s, date of birth: %tF, date of death: %tF, gender: %s," +
                        " height: %scm, weight: %skg, blood type: %s, current address: %s, region: %s," +
                        " created on: %s, modified on: %s",
                uid, firstName, ofNullable(middleName).orElse(""), lastName, dateOfBirth, dateOfDeath, gender,
                height, weight, bloodType, currentAddress, region, created_on, modified_on);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        addUpdate("firstName");
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        addUpdate("lastName");
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        addUpdate("middleNames");
        this.middleName = middleName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        addUpdate("dateOfBirth");
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDate dateOfDeath) {
        addUpdate("dateOfDeath");
        this.dateOfDeath = dateOfDeath;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        addUpdate("gender");
        this.gender = gender;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        addUpdate("height");
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        addUpdate("weight");
        this.weight = weight;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        addUpdate("bloodType");
        this.bloodType = bloodType;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        addUpdate("currentAddress");
        this.currentAddress = currentAddress;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        addUpdate("region");
        this.region = region;
    }

    public int getUid() {
        return uid;
    }

    /**
     * Donor objects are identified by their uid
     *
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Donor))
            return false;
        Donor d = (Donor) obj;
        return d.uid == this.uid;
    }
}