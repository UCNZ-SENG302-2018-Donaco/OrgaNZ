package com.humanharvest.organz.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.humanharvest.organz.Administrator;

/**
 * The class to handle the Administrators.
 */
public class AdministratorManagerMemory implements AdministratorManager {

    private static final String DEFAULT_ADMINISTRATOR_USERNAME = "admin";

    private final List<Administrator> administrators;

    public AdministratorManagerMemory() {
        administrators = new ArrayList<>();
        administrators.add(new Administrator(DEFAULT_ADMINISTRATOR_USERNAME, ""));
    }

    public AdministratorManagerMemory(List<Administrator> administrators) {
        this.administrators = administrators;
        administrators.add(new Administrator(DEFAULT_ADMINISTRATOR_USERNAME, ""));
    }

    /**
     * Add an administrator
     *
     * @param administrator Administrator to be added
     */
    @Override
    public void addAdministrator(Administrator administrator) {
        if (doesUsernameExist(administrator.getUsername())) {
            throw new IllegalArgumentException("Username already exists or is invalid");
        }

        administrators.add(administrator);
    }

    /**
     * Get the list of administrators
     *
     * @return ArrayList of current administrators
     */
    @Override
    public List<Administrator> getAdministrators() {
        return Collections.unmodifiableList(administrators);
    }

    /**
     * Remove an administrator
     *
     * @param administrator Administrator to be removed
     */
    @Override
    public void removeAdministrator(Administrator administrator) {
        administrators.remove(administrator);
    }

    /**
     * Checks if an administrator already exists with that username
     *
     * @param username The username of the administrator
     */
    @Override
    public boolean doesUsernameExist(String username) {
        // Check if it is numeric (this could collide with a clinician ID)
        if (username.matches("[0-9]+")) {
            return true;
        }

        // Check it is not already being used by another administrator
        for (Administrator administrator : administrators) {
            if (administrator.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return an administrator matching that UID
     *
     * @param username The username to be matched
     * @return Administrator or null if none exists
     */
    @Override
    public Optional<Administrator> getAdministratorByUsername(String username) {
        return administrators.stream()
                .filter(o -> o.getUsername().equals(username))
                .findFirst();
    }

    /**
     * Return the default administrator
     *
     * @return the default administrator
     */
    @Override
    public Administrator getDefaultAdministrator() {
        return getAdministratorByUsername(DEFAULT_ADMINISTRATOR_USERNAME).orElseThrow(RuntimeException::new);
    }

    @Override
    public void applyChangesTo(Administrator administrator) {
        // Doesn't need to do anything
    }
}
