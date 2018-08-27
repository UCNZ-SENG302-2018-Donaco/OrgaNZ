package com.humanharvest.organz.utilities;

import java.util.Comparator;

import com.humanharvest.organz.Client;

/**
 * Compare and sort Clients based on the names in order:
 * Last name - Pref name - First name - Middle name - Client ID
 * Also takes a searchTerm, where the name must contain the search term to be higher ranked than the other
 */
public class ClientNameSorter implements Comparator<Client> {

    private String searchTerm;

    /**
     * Create a sorter
     *
     * @param searchTerm The searchTerm to match. Can be null or empty string
     */
    public ClientNameSorter(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    /**
     * Compare two string, and return an integer value representing which one is greater
     * Will always favor one name if it matches the search string, if both match it will compare the strings directly
     * If neither match, it will return 0 always.
     *
     * @param searchTerm The search term to consider
     * @param name1 The first name to check
     * @param name2 The second name to check
     * @return The resulting sort integer
     */
    private static int compareName(String searchTerm, String name1, String name2) {
        boolean name1Matches = name1 != null && name1.toLowerCase().startsWith(searchTerm);
        boolean name2Matches = name2 != null && name2.toLowerCase().startsWith(searchTerm);

        if (name1Matches && name2Matches) {
            return name1.compareTo(name2);
        } else if (name1Matches) {
            return -1;
        } else if (name2Matches) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Compare two clients based on their names in order:
     * Last name - Pref name - First name - Middle name - Client ID
     *
     * @param client1 The first Client to compare
     * @param client2 The second Client to compare
     * @return An integer comparison value
     */
    @Override
    public int compare(Client client1, Client client2) {
        if (searchTerm == null) {
            searchTerm = "";
        }
        return compareNames(client1, client2, searchTerm);
    }

    /**
     * Compares the names based off the priority Last name - Pref name - First name - Middle name - Client ID
     * Falls back to comparing the user id's if the names are identical to ensure a consistent order
     *
     * @param client1 The first client object being compared
     * @param client2 The second client object being compared
     * @param searchTerm The search term to consider
     * @return -1 if client1 is higher priority. 1 if client1 is lower priority. 0 only if they have the same user ID.
     */
    private int compareNames(Client client1, Client client2, String searchTerm) {
        //Last name -> Pref name -> First name -> Middle name -> Client ID
        int result;

        //Last name check
        result = compareName(searchTerm, client1.getLastName(), client2.getLastName());
        if (result != 0) {
            return result;
        }

        //Preferred name check
        result = compareName(searchTerm, client1.getPreferredName(), client2.getPreferredName());
        if (result != 0) {
            return result;
        }

        //First name check
        result = compareName(searchTerm, client1.getFirstName(), client2.getFirstName());
        if (result != 0) {
            return result;
        }

        //Middle name check
        result = compareName(searchTerm, client1.getMiddleName(), client2.getMiddleName());
        if (result != 0) {
            return result;
        }

        return client1.getUid() - client2.getUid();
    }
}
