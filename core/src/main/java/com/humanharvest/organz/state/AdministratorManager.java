package com.humanharvest.organz.state;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.humanharvest.organz.Administrator;

public interface AdministratorManager {

    /**
     * Add an administrator
     * @param administrator Administrator to be added
     */
    void addAdministrator(Administrator administrator);

    /**
     * Get the list of administrators
     * @return ArrayList of current administrators
     */
    List<Administrator> getAdministrators();

    default Iterable<Administrator> getAdministratorsFiltered(String nameQuery, Integer offset, Integer count) {
        Stream<Administrator> stream = getAdministrators().stream();
        if (nameQuery != null) {
            stream = stream.filter(administrator ->
                    administrator.getUsername().contains(nameQuery)
            );
        }

        if (offset != null) {
            stream = stream.skip(offset);
        }

        if (count != null) {
            stream = stream.limit(count);
        }

        return stream.collect(Collectors.toList());
    }

    /**
     * Remove an administrator
     * @param administrator Administrator to be removed
     */
    void removeAdministrator(Administrator administrator);

    /**
     * Checks if an administrator already exists with that username
     * @param username The username of the administrator
     */
    boolean doesUsernameExist(String username);

    /**
     * Return an administrator matching that UID
     * @param username The username to be matched
     * @return Administrator or null if none exists
     */
    Administrator getAdministratorByUsername(String username);
    /**
     * Return the default administrator
     * @return the default administrator
     */
    Administrator getDefaultAdministrator();
}
