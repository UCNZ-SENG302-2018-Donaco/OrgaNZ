package com.humanharvest.organz.utilities.serialisation;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.LocalTime;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;

/**
 * A strategy that describes how to read the contents of a file as a series of {@link Client}s.
 */
public interface ReadClientStrategy extends Closeable {

    LocalTime DEFAULT_TIME_OF_DEATH = LocalTime.of(0, 0);
    String DEFAULT_CITY_OF_DEATH = "Unspecified";
    String DEFAULT_REGION_OF_DEATH = Region.UNSPECIFIED.toString();
    Country DEFAULT_COUNTRY_OF_DEATH = Country.NZ;

    /**
     * Sets up the ReadClientStrategy to read from the given file.
     *
     * @param inputFile The file to read from.
     * @throws IOException If an IO error occurs when attempting to set up reading of the file.
     */
    void setup(File inputFile) throws IOException;

    /**
     * Reads the next client from the file.
     *
     * @return The parsed client, or null if there are no more clients in the file.
     * @throws InvalidObjectException If the next record does not represent a valid client.
     * @throws IOException If a critical error occurrs which ends reading of the file
     * (invalid syntax or input stream broken)
     */
    Client readNext() throws IOException;
}
