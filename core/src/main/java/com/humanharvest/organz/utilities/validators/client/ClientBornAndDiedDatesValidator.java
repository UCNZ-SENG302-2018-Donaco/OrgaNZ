package com.humanharvest.organz.utilities.validators.client;

import java.util.Arrays;
import java.util.List;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.views.client.ModifyClientObject;

public class ClientBornAndDiedDatesValidator {

    public static boolean isValid(ModifyClientObject clientView, Client client) {
        //Get a list of unmodified fields so we don't check fields that haven't changed
        List<String> unmodifiedFields = Arrays.asList(clientView.getUnmodifiedFields());

        //Neither have been modified, all okay
        if (unmodifiedFields.contains("dateOfBirth") && unmodifiedFields.contains("dateOfDeath")) {
            return true;
        }
        //Both have been modified, need to compare them to each other
        else if (!unmodifiedFields.contains("dateOfBirth") && !unmodifiedFields.contains("dateOfDeath")) {
            return clientView.getDateOfDeath() == null ||
                    !clientView.getDateOfBirth().isAfter(clientView.getDateOfDeath());
        }
        //Date of birth has been modified only
        else if (!unmodifiedFields.contains("dateOfBirth")) {
            return client.getDateOfDeath() == null || !clientView.getDateOfBirth().isAfter(client.getDateOfDeath());
        }
        //Date of death has been modified only
        else {
            return clientView.getDateOfDeath() == null ||
                    !client.getDateOfBirth().isAfter(clientView.getDateOfDeath());
        }
    }
}