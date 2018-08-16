package com.humanharvest.organz;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.utilities.enums.*;
import com.humanharvest.organz.utilities.exceptions.OrganAlreadyRegisteredException;
import com.humanharvest.organz.views.client.Views;

import javax.persistence.*;
import java.time.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * The main Client class.
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@Entity
@Table
@Access(AccessType.FIELD)
public class Client implements ConcurrencyControlledEntity {

    @Id
    @GeneratedValue
    @JsonView(Views.Overview.class)
    private Integer uid;
    @JsonView(Views.Overview.class)
    private String firstName;
    @JsonView(Views.Overview.class)
    private String lastName;
    @JsonView(Views.Overview.class)
    private String middleName = "";
    @JsonView(Views.Overview.class)
    private String preferredName = "";
    @JsonView(Views.Details.class)
    @Column(columnDefinition = "text")
    private String currentAddress;

    @JsonView(Views.Overview.class)
    private String region = "Unspecified";
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Overview.class)
    private Country country = Country.NZ;
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Overview.class)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Overview.class)
    private BloodType bloodType;
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Details.class)
    private Gender genderIdentity;

    @JsonView(Views.Details.class)
    private double height;
    @JsonView(Views.Details.class)
    private double weight;

    @JsonView(Views.Overview.class)
    private LocalDate dateOfBirth;

    @JsonView(Views.Details.class)
    private LocalDate dateOfDeath;
    @JsonView(Views.Details.class)
    private LocalTime timeOfDeath;
    @JsonView(Views.Overview.class)
    private String regionOfDeath;
    @JsonView(Views.Details.class)
    private String cityOfDeath;
    @JsonView(Views.Details.class)
    @Enumerated(EnumType.STRING)
    private Country countryOfDeath;
    @JsonView(Views.Details.class)
    private boolean dateOfDeathIsEditable = true;

    @JsonView(Views.Details.class)
    private final Instant createdTimestamp;
    @JsonView(Views.Details.class)
    private Instant modifiedTimestamp;

    @JsonView(Views.Overview.class)
    private boolean isDonor = false;
    @JsonView(Views.Overview.class)
    private boolean isReceiver = false;

    @ElementCollection(targetClass = Organ.class)
    @Enumerated(EnumType.STRING)
    private Set<Organ> organsDonating = EnumSet.noneOf(Organ.class);

    @OneToMany(
            mappedBy = "client",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<TransplantRequest> transplantRequests = new ArrayList<>();

    @OneToMany(
            mappedBy = "client",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<MedicationRecord> medicationHistory = new ArrayList<>();

    @OneToMany(
            mappedBy = "client",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<IllnessRecord> illnessHistory = new ArrayList<>();

    @OneToMany(
            mappedBy = "client",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<ProcedureRecord> procedures = new ArrayList<>();

    @OneToMany(
            mappedBy = "donor",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference(value = "donatedOrgan")
    private List<DonatedOrgan> donatedOrgans = new ArrayList<>();

    @OneToMany(
            mappedBy = "receiver",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference(value = "receivedOrgan")
    private List<DonatedOrgan> receivedOrgans = new ArrayList<>();

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<HistoryItem> changesHistory = new ArrayList<>();

    public Client() {
        createdTimestamp = Instant.now();
    }

    public Client(int uid) {
        this.uid = uid;
        createdTimestamp = Instant.now();
    }

    /**
     * Create a new client object
     * @param firstName First name string
     * @param middleName Middle name(s). May be null
     * @param lastName Last name string
     * @param dateOfBirth LocalDate formatted date of birth
     * @param uid A unique user ID. Should be queried to ensure uniqueness
     */
    public Client(String firstName, String middleName, String lastName, LocalDate dateOfBirth, Integer uid) {
        this.uid = uid;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        gender = Gender.UNSPECIFIED;
        createdTimestamp = Instant.now();
    }

    private void updateModifiedTimestamp() {
        modifiedTimestamp = Instant.now();
    }

    /**
     * Set a single organs donation status
     * @param organ The organ to be set
     * @param value Boolean value to set the status too
     * @throws OrganAlreadyRegisteredException Thrown if the organ is set to true when it already is
     */
    public void setOrganDonationStatus(Organ organ, boolean value) throws OrganAlreadyRegisteredException {
        if (value) {
            if (organsDonating.contains(organ)) {
                throw new OrganAlreadyRegisteredException(organ + " is already registered for donation");
            } else {
                organsDonating.add(organ);
            }
        } else {
            organsDonating.remove(organ);
        }
        isDonor = !organsDonating.isEmpty();
        updateModifiedTimestamp();
    }

    public void setOrganDonationStatus(Map<Organ, Boolean> map) {
        for (Entry<Organ, Boolean> entry : map.entrySet()) {
            if (entry.getValue()) {
                organsDonating.add(entry.getKey());
            } else {
                organsDonating.remove(entry.getKey());
            }
        }
        isDonor = !organsDonating.isEmpty();
        updateModifiedTimestamp();
    }

    public void setDonatedOrgans(Collection<DonatedOrgan> donatedOrgans) {
        this.donatedOrgans = new ArrayList<>(donatedOrgans);
        for (DonatedOrgan donatedOrgan : donatedOrgans) {
            donatedOrgan.setDonor(this);
        }
        isDonor = !organsDonating.isEmpty();
        updateModifiedTimestamp();
    }

    public void setMedicationHistory(List<MedicationRecord> medicationHistory) {
        this.medicationHistory = new ArrayList<>(medicationHistory);
        for (MedicationRecord record : medicationHistory) {
            record.setClient(this);
        }
        updateModifiedTimestamp();
    }

    public void setIllnessHistory(List<IllnessRecord> illnessHistory) {
        this.illnessHistory = new ArrayList<>(illnessHistory);
        for (IllnessRecord illnessRecord : illnessHistory) {
            illnessRecord.setClient(this);
        }
        updateModifiedTimestamp();
    }

    public void setProcedures(List<ProcedureRecord> procedures) {
        this.procedures = new ArrayList<>(procedures);
        for (ProcedureRecord record : procedures) {
            record.setClient(this);
        }
        updateModifiedTimestamp();
    }

    public Set<Organ> getCurrentlyDonatedOrgans() {
        return Collections.unmodifiableSet(organsDonating);
    }

    /**
     * Returns a string listing the organs that the client is currently donating, or a message that the client currently
     * has no organs registered for donation if that is the case.
     * @return The client's organ status string.
     * @throws IllegalArgumentException If the type is not either requests or donations
     */
    public String getOrganStatusString(String type) throws IllegalArgumentException {
        StringBuilder builder = new StringBuilder();
        Set<Organ> organSet;
        switch (type) {
            case "requests":
                organSet = getCurrentlyRequestedOrgans();
                break;
            case "donations":
                organSet = getCurrentlyDonatedOrgans();
                break;
            default:
                throw new IllegalArgumentException("Organ type should either be \"donations\" or \"requests\"");
        }
        for (Organ organ : organSet) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(organ);
        }
        if (builder.length() == 0) {
            return "None";
        } else {
            return builder.toString();
        }
    }

    /**
     * Returns a formatted string listing the client's ID number, full name, and the organs they are donating.
     * @return The formatted client info string.
     * @throws IllegalArgumentException If the type is not either requests or donations
     */
    public String getClientOrganStatusString(String type) throws IllegalArgumentException {
        switch (type) {
            case "donations":
                return String.format("User: %s. Name: %s. Donation status: %s", uid, getFullName(),
                        getOrganStatusString(type));
            case "requests":
                return String.format("User: %s. Name: %s. Request status: %s", uid, getFullName(),
                        getOrganStatusString(type));
            default:
                throw new IllegalArgumentException("Organ type should either be \"donations\" or \"requests\"");
        }
    }

    /**
     * Returns a preformatted string of the users change history
     * @return Formatted string with newlines
     */
    public String getUpdatesString() {
        StringBuilder out = new StringBuilder(String.format("User: %s. Name: %s, updates:\n", uid, getFullName()));
        if (Objects.isNull(changesHistory)) {
            changesHistory = new ArrayList<>();
        }
        for (HistoryItem item : changesHistory) {
            out.append(String.format("%s: %s\n", item.getTimestamp(), item.getDetails()));
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
        if (middleName != null && !middleName.isEmpty()) {
            fullName += middleName + " ";
        }
        if (preferredName != null && !preferredName.isEmpty()) {
            fullName += "\"" + preferredName + "\" ";
        }
        fullName += lastName;
        return fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        updateModifiedTimestamp();
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        updateModifiedTimestamp();
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        updateModifiedTimestamp();
        this.middleName = middleName;
    }

    public String getPreferredNameFormatted() {
        if (preferredName == null || preferredName.isEmpty()) {
            return getFullName();
        }
        return preferredName;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        updateModifiedTimestamp();
        this.preferredName = preferredName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        updateModifiedTimestamp();
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    public LocalTime getTimeOfDeath() {
        return timeOfDeath;
    }

    @JsonIgnore
    public LocalDateTime getDatetimeOfDeath() {
        return LocalDateTime.of(dateOfDeath, timeOfDeath);
    }

    public void setTimeOfDeath(LocalTime newTimeOfDeath) {
        updateModifiedTimestamp();
        // Check if date/time of donation needs to be updated for any DonatedOrgans
        if (timeOfDeath != null && dateOfDeath != null) {
            for (DonatedOrgan organ : donatedOrgans) {
                if (organ.getDateTimeOfDonation().equals(LocalDateTime.of(dateOfDeath, timeOfDeath))) {
                    organ.setDateTimeOfDonation(LocalDateTime.of(dateOfDeath, newTimeOfDeath));
                }
            }
        }
        this.timeOfDeath = newTimeOfDeath;
    }

    public String getRegionOfDeath() {
        return regionOfDeath;
    }

    public void setRegionOfDeath(String regionOfDeath) {
        updateModifiedTimestamp();
        this.regionOfDeath = regionOfDeath;
    }

    public String getCityOfDeath() {
        return cityOfDeath;
    }

    public void setCityOfDeath(String cityOfDeath) {
        updateModifiedTimestamp();
        this.cityOfDeath = cityOfDeath;
    }

    public Country getCountryOfDeath() {
        return countryOfDeath;
    }

    public void setCountryOfDeath(Country countryOfDeath) {
        updateModifiedTimestamp();
        this.countryOfDeath = countryOfDeath;
    }

    @JsonIgnore
    public boolean isAlive() {
        return dateOfDeath == null;
    }

    @JsonIgnore
    public boolean isDead() {
        return dateOfDeath != null;
    }

    public void setDateOfDeath(LocalDate newDateOfDeath) {
        updateModifiedTimestamp();
        // Check if date/time of donation needs to be updated for any DonatedOrgans
        if (timeOfDeath != null && dateOfDeath != null) {
            for (DonatedOrgan organ : donatedOrgans) {
                if (organ.getDateTimeOfDonation().equals(LocalDateTime.of(dateOfDeath, timeOfDeath))) {
                    organ.setDateTimeOfDonation(LocalDateTime.of(newDateOfDeath, timeOfDeath));
                }
            }
        }
        this.dateOfDeath = newDateOfDeath;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        updateModifiedTimestamp();
        this.gender = gender;
    }

    public Gender getGenderIdentity() {
        return genderIdentity;
    }

    public void setGenderIdentity(Gender genderIdentity) {
        updateModifiedTimestamp();
        this.genderIdentity = genderIdentity;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        updateModifiedTimestamp();
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        updateModifiedTimestamp();
        this.weight = weight;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        updateModifiedTimestamp();
        this.bloodType = bloodType;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        updateModifiedTimestamp();
        this.currentAddress = currentAddress;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        updateModifiedTimestamp();
        this.region = region;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    public Instant getModifiedTimestamp() {
        return modifiedTimestamp;
    }

    public Map<Organ, Boolean> getOrganDonationStatus() {
        Map<Organ, Boolean> organDonationStatus = new HashMap<>();
        for (Organ organ : Organ.values()) {
            organDonationStatus.put(organ, organsDonating.contains(organ));
        }
        return organDonationStatus;
    }

    public Set<Organ> getCurrentlyRequestedOrgans() {
        return Collections.unmodifiableSet(transplantRequests
                .stream()
                .filter(request -> request.getStatus() == TransplantRequestStatus.WAITING)
                .map(TransplantRequest::getRequestedOrgan)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Organ.class))));
    }

    /**
     * Returns a list of illnesses that Client previously had
     *
     * @return List of illnesses held by Client
     */
    public List<IllnessRecord> getPastIllnesses() {
        return Collections.unmodifiableList(illnessHistory.stream().filter(
                record -> record.getCuredDate() != null

        ).collect(Collectors.toList()));
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

    public List<HistoryItem> getChangesHistory() {
        return Collections.unmodifiableList(changesHistory);
    }

    /**
     * Returns a new list containing all medications used by the Client, past and current.
     * @return The list of all medications used by the Client.
     */
    public List<MedicationRecord> getMedications() {
        return Collections.unmodifiableList(medicationHistory);
    }

    /**
     * Adds a new MedicationRecord to the client's history.
     * @param record The given MedicationRecord.
     */
    public void addMedicationRecord(MedicationRecord record) {
        medicationHistory.add(record);
        record.setClient(this);
        updateModifiedTimestamp();
    }

    /**
     * Deletes the given MedicationRecord from the client's history.
     * @param record The given MedicationRecord.
     */
    public void deleteMedicationRecord(MedicationRecord record) {
        medicationHistory.remove(record);
        record.setClient(null);
        updateModifiedTimestamp();
    }

    /**
     * Returns the MedicationRecord for the client with the given index
     * @param index index of the MedicationRecord
     * @return the MedicationRecord with the given id
     */
    public MedicationRecord getMedicationRecord(int index) {
        return medicationHistory.stream().filter(record -> record.getId() == index).findFirst().orElse(null);
    }

    /**
     * Calculates the BMI of the Client based off their height and weight - BMI = weight/height^2.
     * If either field is 0, the result returned is 0.
     * @return the users calculated BMI.
     */
    public double getBMI() {
        double bmi;
        if (weight == 0 || height == 0) {
            bmi = 0;
        } else {
            bmi = weight / (height * 0.01 * height * 0.01);
        }
        return bmi;
    }

    /**
     * Calculates the users age based on their date of birth and date of death.
     * If the date of death is null the age is calculated base of the LocalDate.now().
     * If the date of birth is null, -1 is returned.
     * @return age of the Client.
     */
    public int getAge() {
        if (dateOfBirth == null) {
            return -1;
        }
        int age;
        if (dateOfDeath == null) {
            age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        } else {
            age = Period.between(dateOfBirth, dateOfDeath).getYears();
        }
        return age;
    }

    /**
     * Returns a list of illnesses that Client currently has or previously had
     * @return List of illnesses held by Client
     */
    public List<IllnessRecord> getIllnesses() {
        return Collections.unmodifiableList(illnessHistory);
    }

    /**
     * Returns an illness given it's ID value.
     * @param id The id of the illness.
     * @return The illness record requested, or null if it doesn't exist.
     */
    public IllnessRecord getIllnessById(long id) {
        // TODO: Should be optional? Usages don't seem to take into account the null case.
        return illnessHistory
                .stream()
                .filter(record -> record.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public DonatedOrgan getDonatedOrganById(long id) {
        return donatedOrgans
                .stream()
                .filter(organ -> organ.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns list of illnesses client currently has
     * @return List of illnesses client currently has
     */
    public List<IllnessRecord> getCurrentIllnesses() {
        return Collections.unmodifiableList(illnessHistory.stream().filter(
                record -> record.getCuredDate() == null
        ).collect(Collectors.toList()));
    }

    /**
     * Returns a list of procedures that the client has previously undergone.
     *
     * @return A list of past procedures for the client.
     */
    public List<ProcedureRecord> getPastProcedures() {
        return Collections.unmodifiableList(procedures.stream()
                .filter(record -> record.getDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList()));
    }

    /**
     * Adds Illness history to Person
     * @param record IllnessRecord that is wanted to be added
     */
    public void addIllnessRecord(IllnessRecord record) {
        illnessHistory.add(record);
        record.setClient(this);
        updateModifiedTimestamp();
    }

    /**
     * Deletes illness history from Person
     * @param record The illness history that is wanted to be deleted
     */
    public void deleteIllnessRecord(IllnessRecord record) {
        illnessHistory.remove(record);
        record.setClient(null);
        updateModifiedTimestamp();
    }

    /**
     * Returns a list of procedures that the client has undergone or is going to undergo.
     * @return A list of procedures for the client.
     */
    public List<ProcedureRecord> getProcedures() {
        return Collections.unmodifiableList(procedures);
    }

    /**
     * Returns a list of procedures that the client is going to undergo.
     * @return A list of pending procedures for the client.
     */
    public List<ProcedureRecord> getPendingProcedures() {
        return Collections.unmodifiableList(procedures.stream()
                .filter(record -> !record.getDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList()));
    }

    public List<TransplantRequest> getTransplantRequests() {
        return Collections.unmodifiableList(transplantRequests);
    }

    /**
     * Adds the given procedure record to the list of procedures for this client.
     * @param record The procedure record to add.
     */
    public void addProcedureRecord(ProcedureRecord record) {
        procedures.add(record);
        record.setClient(this);
        updateModifiedTimestamp();
    }

    /**
     * Removes the given procedure record from the list of procedures for this client.
     * @param record The procedure record to delete.
     */
    public void deleteProcedureRecord(ProcedureRecord record) {
        procedures.remove(record);
        record.setClient(null);
        updateModifiedTimestamp();
    }

    public List<DonatedOrgan> getDonatedOrgans() {
        return Collections.unmodifiableList(donatedOrgans);
    }

    public List<DonatedOrgan> getReceivedOrgans() {
        return Collections.unmodifiableList(receivedOrgans);
    }

    public void addToChangesHistory(HistoryItem historyItem) {
        changesHistory.add(historyItem);
    }

    public void removeFromChangesHistory(HistoryItem historyItem) {
        changesHistory.remove(historyItem);
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
            if (firstName == null || !firstName.toLowerCase().contains(string) &&
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
        return client.uid.equals(this.uid);
    }

    /**
     * Client objects are identified by their uid
     */
    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }

    public void setTransplantRequests(List<TransplantRequest> requests) {
        transplantRequests = new ArrayList<>(requests);
        for (TransplantRequest request : requests) {
            request.setClient(this);
        }
        isReceiver = !transplantRequests.isEmpty();
        updateModifiedTimestamp();
    }

    public Optional<TransplantRequest> getTransplantRequestById(long id) {
        return transplantRequests.stream()
                .filter(transplantRequest -> transplantRequest.getId() == id)
                .findFirst();
    }

    public void addTransplantRequest(TransplantRequest request) {
        transplantRequests.add(request);
        request.setClient(this);
        isReceiver = !transplantRequests.isEmpty();
    }

    public void removeTransplantRequest(TransplantRequest request) {
        transplantRequests.remove(request);
        request.setClient(null);
        isReceiver = !transplantRequests.isEmpty();
    }

    /**
     * Indicates whether the client is a donor (has chosen to donate at least one organ)
     * @return boolean of whether the client has chosen to donate any organs
     */
    @JsonIgnore
    public boolean isDonor() {
        return isDonor;
    }

    @JsonIgnore
    public boolean getDateOfDeathIsEditable() {
        return dateOfDeathIsEditable;
    }

    /**
     * Updates dateOfDeathIsEditable to reflect if any organs are overridden.
     */
    @JsonIgnore
    public void updateHasOverriddenOrgans() {
        // You can edit the date of death if there are no overridden organs
        dateOfDeathIsEditable = !hasOverriddenOrgans();
    }

    /**
     * @return true if any of their organs' expiry has been manually overridden
     */
    @JsonIgnore
    private boolean hasOverriddenOrgans() {
        for (DonatedOrgan donatedOrgan : donatedOrgans) {
            if (donatedOrgan.getOverrideReason() != null) {
                // there is some reason why they were overridden
                return true;
            }
        }
        return false;
    }

    /**
     * Indicates whether the client is a receiver (has at least one transplant request)
     * @return boolean of whether the client has any organ transplant requests
     */
    @JsonIgnore
    public boolean isReceiver() {
        return isReceiver;
    }

    /**
     * Marks the client as dead.
     * - Resolves any pending organ requests by this client.
     * - Marks all the organs they were willing to donate as donated.
     * @param dateOfDeath Their date of death.
     * @param timeOfDeath Their time of death.
     * @param countryOfDeath The country they died in.
     * @param regionOfDeath The region they died in.
     * @param cityOfDeath The city they died in.
     */
    public void markDead(LocalDate dateOfDeath, LocalTime timeOfDeath, Country countryOfDeath, String regionOfDeath,
            String cityOfDeath) {
        this.dateOfDeath = dateOfDeath;
        this.timeOfDeath = timeOfDeath;
        this.countryOfDeath = countryOfDeath;
        this.regionOfDeath = regionOfDeath;
        this.cityOfDeath = cityOfDeath;

        for (TransplantRequest request : transplantRequests) {
            if (request.getStatus() == TransplantRequestStatus.WAITING) {
                request.setStatus(TransplantRequestStatus.CANCELLED);
                request.setResolvedDate(LocalDateTime.now());
                request.setResolvedReason("The client died.");
            }
        }
        for (Organ organType : getCurrentlyDonatedOrgans()) {
            donateOrgan(organType, LocalDateTime.of(dateOfDeath, timeOfDeath));
        }
    }

    /**
     * Registers the given {@link Organ} as having been donated by this client at this moment.
     *
     * @param organ The organ donated.
     */
    public void donateOrgan(Organ organ) {
        donatedOrgans.add(new DonatedOrgan(organ, this, LocalDateTime.now()));
    }

    /**
     * Registers the given {@link Organ} as having been donated by this client at this moment.
     *
     * @param organ The organ donated.
     * @param id    The id to use for the {@link DonatedOrgan}
     */
    public void donateOrgan(Organ organ, Long id) {
        donatedOrgans.add(new DonatedOrgan(organ, this, LocalDateTime.now(), id));
    }

    /**
     * Registers the given {@link Organ} as having been donated by this client at the given time.
     * @param organ The organ donated.
     * @param timeDonated When the organ was removed from this client's body.
     */
    public void donateOrgan(Organ organ, LocalDateTime timeDonated) {
        donatedOrgans.add(new DonatedOrgan(organ, this, timeDonated));
    }

    /**
     * Registers the given {@link Organ} as having been donated by this client at the given time.
     * @param organ The organ donated.
     * @param timeDonated When the organ was removed from this client's body.
     * @param id The id to use for the {@link DonatedOrgan}
     */
    public void donateOrgan(Organ organ, LocalDateTime timeDonated, Long id) {
        donatedOrgans.add(new DonatedOrgan(organ, this, timeDonated, id));
    }

    /**
     * Returns a hashed version of the latest timestamp for use in concurrency control
     * The hash is surrounded by double quotes as required for a valid ETag
     * @return The ETag string with double quotes
     */
    @Override
    public String getETag() {
        if (modifiedTimestamp == null) {
            return String.format("\"%d\"", createdTimestamp.hashCode());
        } else {
            return String.format("\"%d\"", modifiedTimestamp.hashCode());
        }
    }

    /**
     * Does the specified Client match the ClientType
     * @param type The ClientType to match
     * @return If the Client matches that type
     */
    public boolean isOfType(ClientType type) {
        switch (type) {
            case ANY:
                return true;

            case BOTH:
                return isDonor() && isReceiver();

            case NEITHER:
                return !isDonor() && !isReceiver();

            case ONLY_DONOR:
                return isDonor() && !isReceiver();

            case ONLY_RECEIVER:
                return !isDonor() && isReceiver();

            default:
                throw new IllegalArgumentException("type isn't a valid ClientType value");
        }
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
        updateModifiedTimestamp();
    }
}
