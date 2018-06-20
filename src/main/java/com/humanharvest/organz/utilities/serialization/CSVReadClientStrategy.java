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

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("M/dd/yyyy");

    public enum Header {
        nhi, first_names, last_names, date_of_birth, date_of_death, birth_gender, gender, blood_type, height, weight,
        street_number, street_name, neighborhood, city, region, zip_code, country, birth_country, home_number,
        mobile_number, email
    }

    public Client deserialise(CSVRecord record) throws IllegalArgumentException {
        Client client = new Client();
        client.setFirstName(record.get(first_names));
        client.setLastName(record.get(last_names));
        client.setDateOfBirth(parseDate(record.get(date_of_birth)));
        client.setDateOfDeath(parseDate(record.get(date_of_death)));
        client.setGender(Gender.fromString(record.get(birth_gender)));
        client.setGenderIdentity(Gender.fromString(record.get(gender)));
        client.setBloodType(BloodType.fromString(record.get(blood_type)));
        client.setHeight(Double.parseDouble(record.get(height)));
        client.setWeight(Double.parseDouble(record.get(weight)));
        client.setCurrentAddress(
                record.get(street_number) + " " +
                record.get(street_name) + ", " +
                record.get(neighborhood) + ", " +
                record.get(city) + ", " +
                record.get(zip_code));
        client.setRegion(Region.fromString(record.get(region)));
        return client;
    }

    private LocalDate parseDate(String string) throws IllegalArgumentException {
        if (string.equals("")) {
            return null;
        }

        try {
            return LocalDate.parse(string, dateFormat);
        } catch (DateTimeParseException exc) {
            throw new IllegalArgumentException(exc);
        }
    }
}
