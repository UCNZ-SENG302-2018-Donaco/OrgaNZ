package seng302;

import static seng302.TransplantRequest.RequestStatus.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import seng302.Utilities.Enums.BloodType;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;

/**
 * The main Client class.
 */
public class Client {

    private final int uid;
    private String firstName;
    private String lastName;
    private String middleName = "";
    private String preferredName = "";
    private String currentAddress;
    private Region region;
    private Gender gender;
    private Gender genderIdentity;
    private BloodType bloodType;
    private double height;
    private double weight;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;

    private final LocalDateTime createdTimestamp;
    private LocalDateTime modifiedTimestamp;

    private Map<Organ, Boolean> organDonationStatus;

    private List<MedicationRecord> medicationHistory = new ArrayList<>();

    private Collection<TransplantRequest> transplantRequests = new ArrayList<>();

    private List<String> updateLog = new ArrayList<>();

    private List<IllnessRecord> illnessHistory = new ArrayList<>();

    private List<ProcedureRecord> procedures = new ArrayList<>();

    public Client(int uid) {
        this.uid = uid;
        createdTimestamp = LocalDateTime.now();
        initDonationOrgans();
    }

    /**
     * Create a new client object
     * @param firstName First name string
     * @param middleName Middle name(s). May be null
     * @param lastName Last name string
     * @param dateOfBirth LocalDate formatted date of birth
     * @param uid A unique user ID. Should be queried to ensure uniqueness
     */
    public Client(String firstName, String middleName, String lastName, LocalDate dateOfBirth, int uid) {
        this.uid = uid;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;

        this.gender = Gender.UNSPECIFIED;
        this.createdTimestamp = LocalDateTime.now();

        initDonationOrgans();
    }

    private void initDonationOrgans() {
        organDonationStatus = new HashMap<>();
        for (Organ o : Organ.values()) {
            organDonationStatus.put(o, false);
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
    public void setOrganDonationStatus(Organ organ, boolean value) throws OrganAlreadyRegisteredException {
        if (value && organDonationStatus.get(organ)) {
            throw new OrganAlreadyRegisteredException(organ.toString() + " is already registered for donation");
        }
        addUpdate(organ.toString());
        organDonationStatus.replace(organ, value);
    }

    /**
     * Returns a string listing the organs that the client is currently donating, or a message that the client currently
     * has no organs registered for donation if that is the case.
     * @return The client's organ status string.
     */
    public String getOrganStatusString(String type) {
        StringBuilder builder = new StringBuilder();
        Map<Organ, Boolean> organsList;
        switch (type) {
            case "requests":
                organsList = getOrganRequestStatus();
                break;
            case "donations":
                organsList = organDonationStatus;
                break;
            default:
                return "Invalid input";
        }
        for (Map.Entry<Organ, Boolean> entry : organsList.entrySet()) {
            if (entry.getValue()) {
                if (builder.length() != 0) {
                    builder.append(", ");
                }
                builder.append(entry.getKey().toString());
            }
        }
        if (builder.length() == 0) {
            return "No organs found";
        } else {
            return builder.toString();
        }
    }

    /**
     * Returns a formatted string listing the client's ID number, full name, and the organs they are donating.
     * @return The formatted client info string.
     */
    public String getClientOrganStatusString(String type) {
        return String.format("User: %s. Name: %s, Donation status: %s.", uid, getFullName(), getOrganStatusString
                (type));
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
     * Get a formatted string with the clients user information. Does not include organ donation status
     * @return Formatted string with the clients user information. Does not include organ donation status
     */
    public String getClientInfoString() {
        return String.format("User: %s. Name: %s, date of birth: %tF, date of death: %tF, gender: %s," +
                        " height: %scm, weight: %skg, blood type: %s, current address: %s, region: %s," +
                        " created on: %s, modified on: %s",
                uid, getFullName(), dateOfBirth, dateOfDeath, gender,
                height, weight, bloodType, currentAddress, region, createdTimestamp, modifiedTimestamp);
    }

    /**
     * Get the full name of the client concatenating their names
     * @return The full name string
     */
    public String getFullName() {
        String fullName = firstName + " ";
        if (middleName != null && !middleName.equals("")) {
            fullName += middleName + " ";
        }
        if (preferredName != null && !preferredName.equals("")) {
            fullName += "\"" + preferredName + "\" ";
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

    public String getPreferredName() {
        if (preferredName == null || preferredName.equals("")) {
            return getFullName();
        }
        return preferredName;
    }

    public String getPreferredNameOnly() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        addUpdate("preferredName");
        this.preferredName = preferredName;
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

    public Gender getGenderIdentity() {
        return genderIdentity;
    }

    public void setGenderIdentity(Gender genderIdentity) {
        addUpdate("genderIdentity");
        this.genderIdentity = genderIdentity;
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

    public Map<Organ, Boolean> getOrganDonationStatus() {
        return organDonationStatus;
    }

    public Set<Organ> getCurrentlyDonatedOrgans() {
        return organDonationStatus.entrySet().stream()
                .filter(Entry::getValue)
                .map(Entry::getKey)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Organ.class)));
    }

    public Set<Organ> getCurrentlyRequestedOrgans() {
        return transplantRequests
                .stream()
                .filter(request -> request.getStatus() == WAITING)
                .map(TransplantRequest::getRequestedOrgan)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Organ.class)));
    }

    public Map<Organ, Boolean> getOrganRequestStatus() {
        Map<Organ, Boolean> organStatus = new HashMap<>();
        Set<Organ> requestedOrgans = getCurrentlyRequestedOrgans();

        for (Organ organ : Organ.values()) {
            organStatus.put(organ, requestedOrgans.contains(organ));
        }
        return organStatus;
    }

    /**
     * Returns a new list containing the medications which are currently being used by the Client.
     * @return The list of medications currently being used by the Client.
     */
    public List<MedicationRecord> getCurrentMedications() {
        return medicationHistory.stream().filter(
                record -> record.getStopped() == null
        ).collect(Collectors.toList());
    }

    /**
     * Returns a new list containing the medications which were previously used by the Client.
     * @return The list of medications used by the Client in the past.
     */
    public List<MedicationRecord> getPastMedications() {
        return medicationHistory.stream().filter(
                record -> record.getStopped() != null
        ).collect(Collectors.toList());
    }

    /**
     * Adds a new MedicationRecord to the client's history.
     * @param record The given MedicationRecord.
     */
    public void addMedicationRecord(MedicationRecord record) {
        medicationHistory.add(record);
        addUpdate("medicationHistory");
    }

    /**
     * Deletes the given MedicationRecord from the client's history.
     * @param record The given MedicationRecord.
     */
    public void deleteMedicationRecord(MedicationRecord record) {
        medicationHistory.remove(record);
        addUpdate("medicationHistory");
    }

    /**
     * Calculates the BMI of the Client based off their height and weight - BMI = weight/height^2.
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
     * @return age of the Client.
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
     * Returns a list of illnesses that Client previously had
     * @return List of illnesses held by Client
     */
    public List<IllnessRecord> getPastIllnesses() {
        return illnessHistory.stream().filter(
                record -> record.getCuredDate() != null

        ).collect(Collectors.toList());
    }

    /**
     * Returns list of illnesses client currently has
     * @return List of illnesses client currently has
     */
    public List<IllnessRecord> getCurrentIllnesses() {
        return illnessHistory.stream().filter(
                record -> record.getCuredDate() == null
        ).collect(Collectors.toList());
    }

    /**
     * Adds Illness history to Person
     * @param record IllnessRecord that is wanted to be added
     */
    public void addIllnessRecord(IllnessRecord record) {
        illnessHistory.add(record);
        addUpdate("illnessHistory");
    }

    /**
     * Deletes illness history from Person
     * @param record The illness history that is wanted to be deleted
     */
    public void deleteIllnessRecord(IllnessRecord record) {
        illnessHistory.remove(record);
        addUpdate("illnessHistory");
    }

    /**
     * Returns a list of procedures that the client has previously undergone.
     * @return A list of past procedures for the client.
     */
    public List<ProcedureRecord> getPastProcedures() {
        return procedures.stream()
                .filter(record -> record.getDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of procedures that the client is going to undergo.
     * @return A list of pending procedures for the client.
     */
    public List<ProcedureRecord> getPendingProcedures() {
        return procedures.stream()
                .filter(record -> !record.getDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }

    /**
     * Adds the given procedure record to the list of procedures for this client.
     * @param record The procedure record to add.
     */
    public void addProcedureRecord(ProcedureRecord record) {
        procedures.add(record);
        addUpdate("procedures");
    }

    /**
     * Removes the given procedure record from the list of procedures for this client.
     * @param record The procedure record to delete.
     */
    public void deleteProcedureRecord(ProcedureRecord record) {
        procedures.remove(record);
        addUpdate("procedures");
    }

    /**
     * Takes a string and checks if each space separated string section matches one of the names
     * @param searchParam The string to be checked
     * @return True if all sections of the passed string match any of the names of the client
     */
    public boolean nameContains(String searchParam) {
        String lowerSearch = searchParam.toLowerCase();
        String[] splitSearchItems = lowerSearch.split("\\s+");

        boolean isMatch = true;
        for (String string : splitSearchItems) {
            if (!firstName.toLowerCase().contains(string) &&
                    (middleName == null || !middleName.toLowerCase().contains(string)) &&
                    (preferredName == null || !preferredName.toLowerCase().contains(string)) &&
                    !lastName.toLowerCase().contains(string)) {
                isMatch = false;
                break;
            }
        }

        return isMatch;
    }


    /**
     * Returns a HashSet of all names of the Client. If they do not have a middle/preferred name, this is set as "".
     * @return the Hashset of all the Clients names.
     */
    private HashSet<String> splitNames() {

        String[] fname = firstName.split("\\s+");
        String[] lname = lastName.split("\\s+");
        String[] mname;
        String[] pname;

        if (middleName == null) {
            mname = new String[0];
        } else {
            mname = middleName.split("\\s+");
        }
        if (preferredName == null) {
            pname = new String[0];
        } else {
            pname = preferredName.split("\\s+");
        }

        HashSet<String> names = new HashSet<>(Arrays.asList(fname));
        names.addAll(Arrays.asList(lname));
        names.addAll(Arrays.asList(mname));
        names.addAll(Arrays.asList(pname));
        return names;
    }


    /**
     * Takes a string and checks if each space separated string section begins with the same values as the search
     * parameter.
     * @param searchParam The string to be checked
     * @return True if all sections of the passed string match any of the names of the client
     */
    public boolean profileSearch(String searchParam) {
        String lowerSearch = searchParam.toLowerCase();
        String[] splitSearchItems = lowerSearch.split("\\s+");

        Collection<String> searched = new ArrayList<>(Arrays.asList(splitSearchItems));

        Collection<String> names = this.splitNames();
        Collection<String> lowercaseNames = new ArrayList<>();
        for (String name : names) {
            lowercaseNames.add(name.toLowerCase());
        }

        Collection<String> matchedNames = new ArrayList<>();

        for (String searchedParam : searched) {
            for (String name : lowercaseNames) {

                if (name.startsWith(searchedParam)) {
                    matchedNames.add(name);
                    break;
                }
            }

        }
        return matchedNames.size() == searched.size();
    }

    /**
     * Client objects are identified by their uid
     * @param o The object to compare
     * @return If the Client is a match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        Client client = (Client) o;
        return client.uid == this.uid;
    }

    /**
     * Client objects are identified by their uid
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(uid);
    }

    public Collection<TransplantRequest> getTransplantRequests() {
        return transplantRequests;
    }

    public void addTransplantRequest(TransplantRequest request) {
        transplantRequests.add(request);
    }

    public void removeTransplantRequest(TransplantRequest request) {
        transplantRequests.remove(request);
    }

    /**
     * Indicates whether the client is a donor (has chosen to donate at least one organ)
     * @return boolean of whether the client has chosen to donate any organs
     */
    public boolean isDonor() {
        return !getCurrentlyDonatedOrgans().isEmpty();
    }

    /**
     * Indicates whether the client is a receiver (has at least one transplant request)
     * @return boolean of whether the client has any organ transplant requests
     */
    public boolean isReceiver() {
        return !transplantRequests.isEmpty();
    }

    /**
     * Marks the client as dead and marks all organs as no for reception
     * @param dateOfDeath LocalDate that the client died
     */
    public void markDead(LocalDate dateOfDeath) {
        this.dateOfDeath = dateOfDeath;

        for (TransplantRequest request : transplantRequests) {
            if (request.getStatus() == WAITING) {
                request.setStatus(CANCELLED);
                request.setResolvedDate(LocalDateTime.now());
                request.setResolvedReason("death");
            }
        }
    }
}
