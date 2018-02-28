package seng302;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Donor {

    private final LocalDateTime created_on;
    private LocalDateTime modified_on;
    private String name;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    private String gender;
    private int height;
    private int weight;
    private String bloodType;
    private String currentAddress;
    private String region;

    private int uid;

    public Donor(String name, LocalDate dateOfBirth, int uid) {
        created_on = LocalDateTime.now();
        modified_on = LocalDateTime.now();
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public int getUid() {
        return uid;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getDonorInfoString() {
        return String.format("User: %s. Name: %s, date of birth: %tF, date of death: %tF, gender: %s, height: %scm, weight: %skg, blood type: %s, current address: %s, region: %s, created on: %s, modified on: %s", uid, name, dateOfBirth, dateOfDeath, gender, height, weight, bloodType, currentAddress, region, created_on, modified_on);
    }

    public void setIntField(String fieldName, int value) throws NoSuchFieldException, IllegalAccessException {
        Field field = getClass().getDeclaredField(fieldName);
        field.setInt(this, value);
        modified_on = LocalDateTime.now();
    }

    public void setDateField(String fieldName, LocalDate value) throws NoSuchFieldException, IllegalAccessException {
        Field field = getClass().getDeclaredField(fieldName);
        field.set(this, value);
        modified_on = LocalDateTime.now();
    }

    public void setStringField(String fieldName, String value) throws NoSuchFieldException, IllegalAccessException {
        Field field = getClass().getDeclaredField(fieldName);
        field.set(this, value);
        modified_on = LocalDateTime.now();
    }

    /**
     * Donor objects are identified by their uid
     * @param obj
     * @return
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
