package seng302;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import seng302.Utilities.Enums.BloodType;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;

/**
 * The main Person class.
 */

public class Person {

    private int uid;
    private String firstName;
    private String lastName;
    private String middleName;
    private String currentAddress;
    private Region region;
    private Gender gender;
    private BloodType bloodType;
    private double height;
    private double weight;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;

    private final LocalDateTime createdTimestamp;
    private LocalDateTime modifiedTimestamp;

    private Map<Organ, Boolean> organStatus;

    private List<MedicationRecord> medicationHistory = new ArrayList<>();

    private List<Condition> conditions = new ArrayList<>();

    private List<TransplantRequest> transplantRequests = new ArrayList<>();

    private ArrayList<String> updateLog = new ArrayList<>();

    private List<IllnessRecord> illnessHistory = new ArrayList<>();

    public Person() {
        createdTimestamp = LocalDateTime.now();
        initOrgans();
    }

    /**
     * Create a new person object
     * @param firstName First name string
     * @param middleName Middle name(s). May be null
     * @param lastName Last name string
     * @param dateOfBirth LocalDate formatted date of birth
     * @param uid A unique user ID. Should be queried to ensure uniqueness
     */
    public Person(String firstName, String middleName, String lastName, LocalDate dateOfBirth, int uid) {
        this.uid = uid;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;

        this.gender = Gender.UNSPECIFIED;
        this.createdTimestamp = LocalDateTime.now();

        initOrgans();
    }

    private void initOrgans() {
        organStatus = new HashMap<>();
        for (Organ o : Organ.values()) {
            organStatus.put(o, false);
        }
    }

    private void addUpdate(String function) {
        LocalDateTime timestamp = LocalDateTime.now();
        updateLog.add(String.format("%s; updated %s", timestamp, function));
        modifiedTimestamp = LocalDateTime.now();
    }

    /**
     * Set a single organs donation status
     * @param organ The organ to be set
     * @param value Boolean value to set the status too
     * @throws OrganAlreadyRegisteredException Thrown if the organ is set to true when it already is
     */
    public void setOrganStatus(Organ organ, boolean value) throws OrganAlreadyRegisteredException {
        if (value && organStatus.get(organ)) {
            throw new OrganAlreadyRegisteredException(organ.toString() + " is already registered for donation");
        }
        addUpdate(organ.toString());
        organStatus.replace(organ, value);
    }

    public Map<Organ, Boolean> getOrganStatus() {
        return organStatus;
    }

    /**
     * Returns a string listing the organs that the person is currently donating, or a message that the person currently
     * has no organs registered for donation if that is the case.
     * @return The person's organ status string.
     */
    public String getOrganStatusString() {
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
            return "No organs registered for donation";
        } else {
            return builder.toString();
        }
    }

    /**
     * Returns a formatted string listing the person's ID number, full name, and the organs they are donating.
     * @return The formatted person info string.
     */
    public String getPersonOrganStatusString() {
        return String.format("User: %s. Name: %s, Donation status: %s.", uid, getFullName(), getOrganStatusString());
    }

    /**
     * Returns a preformatted string of the users change history
     * @return Formatted string with newlines
     */
    public String getUpdatesString() {
        StringBuilder out = new StringBuilder(String.format("User: %s. Name: %s, updates:\n", uid, getFullName()));
        for (String update : updateLog) {
            out.append(update).append('\n');
        }
        return out.toString();
    }

    /**
     * Get a formatted string with the persons user information. Does not include organ donation status
     * @return Formatted string with the persons user information. Does not include organ donation status
     */
    public String getPersonInfoString() {
        return String.format("User: %s. Name: %s, date of birth: %tF, date of death: %tF, gender: %s," +
                        " height: %scm, weight: %skg, blood type: %s, current address: %s, region: %s," +
                        " created on: %s, modified on: %s",
                uid, getFullName(), dateOfBirth, dateOfDeath, gender,
                height, weight, bloodType, currentAddress, region, createdTimestamp, modifiedTimestamp);
    }

    /**
     * Get the full name of the person concatenating their names
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

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        addUpdate("height");
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
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

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        addUpdate("region");
        this.region = region;
    }

    public int getUid() {
        return uid;
    }

    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public LocalDateTime getModifiedTimestamp() {
        return modifiedTimestamp;
    }

    /**
     * Returns a new list containing the medications which are currently being used by the Person.
     * @return The list of medications currently being used by the Person.
     */
    public List<MedicationRecord> getCurrentMedications() {
        return medicationHistory.stream().filter(
                record -> record.getStopped() == null
        ).collect(Collectors.toList());
    }

    /**
     * Returns a new list containing the medications which were previously used by the Person.
     * @return The list of medications used by the Person in the past.
     */
    public List<MedicationRecord> getPastMedications() {
        return medicationHistory.stream().filter(
                record -> record.getStopped() != null
        ).collect(Collectors.toList());
    }

    /**
     * Adds a new MedicationRecord to the person's history.
     * @param record The given MedicationRecord.
     */
    public void addMedicationRecord(MedicationRecord record) {
        medicationHistory.add(record);
        addUpdate("medicationHistory");
    }

    /**
     * Deletes the given MedicationRecord from the person's history.
     * @param record The given MedicationRecord.
     */
    public void deleteMedicationRecord(MedicationRecord record) {
        medicationHistory.remove(record);
        addUpdate("medicationHistory");
    }

    /**
     * Calculates the BMI of the Person based off their height and weight - BMI = weight/height^2.
     * If either field is 0, the result returned is 0.
     * @return the users calculated BMI.
     */
    public double getBMI() {
        double BMI;
        if (weight == 0 || height == 0) {
            BMI = 0;
        } else {
            BMI = weight / (height * 0.01 * height * 0.01);
        }
        return BMI;
    }

    /**
     * Calculates the users age based on their date of birth and date of death. If the date of death is null the age
     * is calculated base of the LocalDate.now().
     * @return age of the Person.
     */
    public int getAge() {
        int age;
        if (dateOfDeath == null) {
            age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        } else {
            age = Period.between(dateOfBirth, dateOfDeath).getYears();
        }
        return age;
    }


    /**
     * Returns a list of illnesses that Person previously had
     * @return List of illnesses held by Person
     */
    public List<IllnessRecord> getPastIllnesses(){
        return illnessHistory.stream().filter(
            record -> record.getCuredDate() != null

        ).collect(Collectors.toList());
    }

    /**
     * Returns list of illnesses person currently has
     * @return List of illnesses Person currently has
     */
    public List<IllnessRecord> getCurrentIllnesses(){
        return illnessHistory.stream().filter(
            record -> record.getCuredDate() == null
        ).collect(Collectors.toList());
    }


    /**
     * Adds Illness history to Person
     * @param record IllnessRecord that is wanted to be added
     */
    public void addIllnessRecord(IllnessRecord record){
        illnessHistory.add(record);
        addUpdate("illnessHistory");
    }

    /**
     * Deletes illness history from Person
     * @param record The illness history that is wanted to be deleted
     */
    public void deleteIllnessRecord(IllnessRecord record){
        illnessHistory.remove(record);
        addUpdate("illnessHistory");
    }

    /**
     * Returns all conditions that the person has
     * @return ArrayList of all the conditions that the person has
     */
    public List<Condition> getAllConditions() {
        return conditions;
    }

    /**
     * Finds which conditions are current (those that do not have a resolution date)
     * and returns them
     * @return List of current conditions of the person
     */
    public List<Condition> getCurrentConditions() {
        List<Condition> currentConditions = new ArrayList<>();

        for (Condition condition: conditions) {
            if (condition.getResolutionDate() == null) {
                currentConditions.add(condition);
            }
        }
        return currentConditions;
    }

    /**
     * Adds the given Condition to the persons list of conditions
     * @param condition Condition to be added to the persons list of conditions
     */
    public void addCondition(Condition condition) {
        conditions.add(condition);
    }

    /**
     * Finds which conditions are resolved (those that do have a resolution date)
     * and returns them
     * @return List of resolved conditions of the person
     */
    public List<Condition> getResolvedConditions() {
        List<Condition> resolvedConditions = new ArrayList<>();

        for (Condition condition: conditions) {
            if (condition.getResolutionDate() != null) {
                resolvedConditions.add(condition);
            }
        }
        return resolvedConditions;
    }

    /**
     * Takes a string and checks if each space separated string section matches one of the names
     * @param searchParam The string to be checked
     * @return True if all sections of the passed string match any of the names of the person
     */
    public boolean nameContains(String searchParam) {
        String lowerSearch = searchParam.toLowerCase();
        String[] splitSearchItems = lowerSearch.split("\\s+");

        boolean isMatch = true;
        for (String string : splitSearchItems) {
            if (!firstName.toLowerCase().contains(string) &&
                    (middleName == null || !middleName.toLowerCase().contains(string)) &&
                    !lastName.toLowerCase().contains(string)) {
                isMatch = false;
                break;
            }
        }

        return isMatch;
    }

    /**
     * Person objects are identified by their uid
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Person)) {
            return false;
        }
        Person d = (Person) obj;
        return d.uid == this.uid;
    }
}