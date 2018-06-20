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

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public enum Header {
        NHI, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, DATE_OF_DEATH, BIRTH_GENDER, GENDER_IDENTITY, BLOOD_TYPE, HEIGHT,
        WEIGHT, STREET_NUMBER, STREET_NAME, NEIGHBORHOOD, CITY, REGION, POSTCODE, CURRENT_COUNTRY, BIRTH_COUNTRY,
        PHONE_HOME, PHONE_MOBILE, EMAIL
    }

    public Client deserialise(CSVRecord record) throws IllegalArgumentException {
        Client client = new Client();
        client.setFirstName(record.get(FIRST_NAME));
        client.setLastName(record.get(LAST_NAME));
        client.setDateOfBirth(parseDate(record.get(DATE_OF_BIRTH)));
        client.setDateOfDeath(parseDate(record.get(DATE_OF_DEATH)));
        client.setGender(Gender.fromString(record.get(BIRTH_GENDER)));
        client.setGenderIdentity(Gender.fromString(record.get(GENDER_IDENTITY)));
        client.setBloodType(BloodType.fromString(record.get(BLOOD_TYPE)));
        client.setHeight(Double.parseDouble(record.get(HEIGHT)));
        client.setWeight(Double.parseDouble(record.get(WEIGHT)));
        client.setCurrentAddress(
                record.get(STREET_NUMBER) + " " +
                record.get(STREET_NAME) + ", " +
                record.get(NEIGHBORHOOD) + ", " +
                record.get(CITY) + ", " +
                record.get(POSTCODE));
        client.setRegion(Region.fromString(record.get(REGION)));
        return client;
    }

    private LocalDate parseDate(String string) throws IllegalArgumentException {
        try {
            return LocalDate.parse(string, dateFormat);
        } catch (DateTimeParseException exc) {
            throw new IllegalArgumentException(exc);
        }
    }
}
