package com.humanharvest.organz.utilities.serialisation;

import static com.humanharvest.organz.utilities.serialisation.CSVReadClientStrategy.Header.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Region;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * An implementation of {@link ReadClientStrategy} that can be used for reading clients serialized to CSV. This
 * strategy can only read basic data about the client, not including any collections of data such as
 * {@link com.humanharvest.organz.MedicationRecord}s or {@link com.humanharvest.organz.TransplantRequest}s.
 */
public class CSVReadClientStrategy implements ReadClientStrategy {

    /**
     * Describes which columns represent which client data in the CSV format.
     */
    public enum Header {
        nhi, first_names, last_names, date_of_birth, date_of_death, birth_gender, gender, blood_type, height, weight,
        street_number, street_name, neighborhood, city, region, zip_code, country, birth_country, home_number,
        mobile_number, email
    }

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("M/dd/yyyy");

    private CSVParser parser;

    @Override
    public void setup(File inputFile) throws IOException {
        parser = new CSVParser(new FileReader(inputFile), CSVFormat.RFC4180.withFirstRecordAsHeader());
    }

    @Override
    public Client readNext() throws InvalidObjectException {
        try {
            try {
                return deserialise(parser.iterator().next());
            } catch (IllegalArgumentException exc) {
                throw new InvalidObjectException(exc.getMessage());
            }
        } catch (NoSuchElementException exc) {
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        parser.close();
    }

    /**
     * Deseralizes a given {@link CSVRecord} to a {@link Client} object, using the {@link Header} to determine which
     * columns represent which data.
     * @param record The CSVRecord to deserialise.
     * @return The deserialized client.
     * @throws IllegalArgumentException If any data value specified in the record is not valid for its data type.
     */
    private Client deserialise(CSVRecord record) throws IllegalArgumentException {
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

    /**
     * Creates a {@link LocalDate} object from a date in string format (M/dd/yyyy).
     * @param string A date in string format M/dd/yyyy.
     * @return A local date object representing the same date, or null if the string is blank.
     * @throws IllegalArgumentException If the string does not match the M/dd/yyyy format.
     */
    private LocalDate parseDate(String string) throws IllegalArgumentException {
        if ("".equals(string)) {
            return null;
        }

        try {
            return LocalDate.parse(string, dateFormat);
        } catch (DateTimeParseException exc) {
            throw new IllegalArgumentException(exc);
        }
    }
}
