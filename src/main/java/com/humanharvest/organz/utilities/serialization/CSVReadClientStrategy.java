package com.humanharvest.organz.utilities.serialization;

import static com.humanharvest.organz.utilities.serialization.CSVReadClientStrategy.Header.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Region;
import org.apache.commons.csv.CSVRecord;

public class CSVReadClientStrategy {

    public enum Header {
        CODE, FIRST, LAST, DATE_OF_BIRTH, DATE_OF_DEATH, BIRTH_GENDER, GENDER_IDENTITY, BLOOD_TYPE, HEIGHT, WEIGHT,
        ADDRESS_NUMBER, ADDRESS_STREET, ADDRESS_SUBURB, ADDRESS_CITY, REGION, POSTCODE, BIRTH_COUNTRY_CODE,
        CURR_COUNTRY_CODE, PHONE_HOME, PHONE_MOBILE, EMAIL
    }

    public Client parse(CSVRecord record) throws IllegalArgumentException {
        Client client = new Client();
        client.setFirstName(record.get(FIRST));
        client.setLastName(record.get(LAST));
        client.setDateOfBirth(parseDate(record.get(DATE_OF_BIRTH)));
        client.setDateOfDeath(parseDate(record.get(DATE_OF_DEATH)));
        client.setGender(Gender.fromString(record.get(BIRTH_GENDER)));
        client.setGenderIdentity(Gender.fromString(record.get(GENDER_IDENTITY)));
        client.setBloodType(BloodType.fromString(record.get(BLOOD_TYPE)));
        client.setHeight(Double.parseDouble(record.get(HEIGHT)));
        client.setWeight(Double.parseDouble(record.get(WEIGHT)));
        client.setCurrentAddress(
                record.get(ADDRESS_NUMBER) + " " +
                record.get(ADDRESS_STREET) + ", " +
                record.get(ADDRESS_SUBURB) + ", " +
                record.get(ADDRESS_CITY) + ", " +
                record.get(POSTCODE));
        client.setRegion(Region.fromString(record.get(REGION)));
        return client;
    }

    private LocalDate parseDate(String string) throws IllegalArgumentException {
        final DateTimeFormatter slashFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        final DateTimeFormatter dashFormat = DateTimeFormatter.ofPattern("dd-MM-yy");

        try {
            return LocalDate.parse(string, slashFormat);
        } catch (DateTimeParseException exc1) {
            try {
                return LocalDate.parse(string, dashFormat);
            } catch (DateTimeParseException exc2) {
                throw new IllegalArgumentException("Given text did not match either accepted date format.");
            }
        }
    }
}
