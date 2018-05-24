package seng302.State;

import java.util.List;

import seng302.Administrator;

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

    /**
     * Remove an administrator
     * @param administrator Administrator to be removed
     */
    void removeAdministrator(Administrator administrator);

    /**
     * Checks if an administrator already exists with that username
     * @param username The username of the administrator
     */
    boolean collisionExists(String username);

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
