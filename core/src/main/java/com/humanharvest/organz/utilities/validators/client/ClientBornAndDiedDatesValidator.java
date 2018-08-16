package com.humanharvest.organz.utilities.validators.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.views.client.ModifyClientObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class ClientBornAndDiedDatesValidator {

    /**
     * Returns true if the date of birth is not null and is not in the future
     *
     * @param dateOfBirth their date of birth
     */
    public static boolean dateOfBirthIsValid(LocalDate dateOfBirth) {
        return dateOfBirth != null && !dateOfBirth.isAfter(LocalDate.now());
    }

    /**
     * Returns true if the date of death is not null, and
     * is not in the future, and
     * is not before their date of birth (or the date of birth is null)
     *
     * @param dateOfDeath their date of death
     * @param dateOfBirth their date of birth
     */
    public static boolean dateOfDeathIsValid(LocalDate dateOfDeath, LocalDate dateOfBirth) {
        return dateOfDeath != null
                && !dateOfDeath.isAfter(LocalDate.now())
                && (dateOfBirth == null || !dateOfDeath.isBefore(dateOfBirth));
    }

    /**
     * Returns true if the time of death is not null, and
     * (they didn't die today or they died today, but before the current time)
     *
     * @param dateOfDeath their date of death
     * @param timeOfDeath their time of death
     */
    public static boolean timeOfDeathIsValid(LocalDate dateOfDeath, LocalTime timeOfDeath) {
        return dateOfDeath == null
                || (timeOfDeath != null &&
                (!LocalDate.now().equals(dateOfDeath) || !timeOfDeath.isAfter(LocalTime.now().plusMinutes(1))));
    }

    private static boolean dateTimeOfDeathIsValid(LocalDate dateOfDeath, LocalTime timeOfDeath, LocalDate dateOfBirth) {
        // Check if either time and date of death are both null (if so, we're done!), or only time is (which is illegal)
        if (dateOfDeath == null && timeOfDeath == null) {
            return true;
        } else if (dateOfDeath == null || timeOfDeath == null) { // only one is null
            return false;
        }

        LocalDateTime dateTimeOfDeath = LocalDateTime.of(dateOfDeath, timeOfDeath);

        // Check datetime of death is not in the future, and is not before their date of birth
        return !dateTimeOfDeath.isAfter(LocalDateTime.now().plusMinutes(1)) && !dateOfDeath.isBefore(dateOfBirth);
    }

    public static boolean isValid(ModifyClientObject clientView, Client client) {
        //Get a list of unmodified fields so we don't check fields that haven't changed
        List<String> unmodifiedFields = Arrays.asList(clientView.getUnmodifiedFields());

        // Neither have been modified, all okay
        if (unmodifiedFields.contains("dateOfBirth") && unmodifiedFields.contains("dateOfDeath") &&
                unmodifiedFields.contains("timeOfDeath")) {
            return true;
        } else { //stuff has been modified. This function used to check what has been modified, but with time of
            // death, there is now 2^3-1 = 7 cases to check; it's easier to just check everything.

            // Get the data from the client or clientView as appropriate
            LocalDate dateOfBirth;
            LocalDate dateOfDeath;
            LocalTime timeOfDeath;

            if (clientView.getDateOfBirth() == null) {
                dateOfBirth = client.getDateOfBirth();
            } else {
                dateOfBirth = clientView.getDateOfBirth();
            }

            if (clientView.getDateOfDeath() == null) {
                dateOfDeath = client.getDateOfDeath();
            } else {
                dateOfDeath = clientView.getDateOfDeath();
            }

            if (clientView.getTimeOfDeath() == null) {
                timeOfDeath = client.getTimeOfDeath();
            } else {
                timeOfDeath = clientView.getTimeOfDeath();
            }

            return dateOfBirthIsValid(dateOfBirth) && dateTimeOfDeathIsValid(dateOfDeath, timeOfDeath, dateOfBirth);

        }
    }
}