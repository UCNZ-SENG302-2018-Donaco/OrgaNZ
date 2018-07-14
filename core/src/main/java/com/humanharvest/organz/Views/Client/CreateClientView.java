package com.humanharvest.organz.views.client;

import com.humanharvest.organz.IllnessRecord;
import java.time.LocalDate;
import java.util.List;

public class CreateClientView {

    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate dateOfBirth;
    private List<IllnessRecord> illnessRecords;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

}
