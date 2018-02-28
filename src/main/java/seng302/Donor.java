package seng302;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Donor {

    private LocalDateTime created_on;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

}
