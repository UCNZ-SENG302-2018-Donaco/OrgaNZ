package com.humanharvest.organz.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.humanharvest.organz.Administrator;

/**
 * The class to handle the Administrators.
 */
public class AdministratorManagerMemory implements AdministratorManager{

    private final List<Administrator> administrators;
    private String defaultAdministratorUsername = "admin";

    public AdministratorManagerMemory() {
        administrators = new ArrayList<>();
        administrators.add(new Administrator(defaultAdministratorUsername, ""));
    }

    public AdministratorManagerMemory(List<Administrator> administrators) {
        this.administrators = administrators;
        administrators.add(new Administrator(defaultAdministratorUsername, ""));
    }

    /**
     * Add an administrator
     * @param administrator Administrator to be added
     */
    public void addAdministrator(Administrator administrator) {
        if (doesUsernameExist(administrator.getUsername())) {
            throw new IllegalArgumentException("Username already exists or is invalid");
        }

        administrators.add(administrator);
    }

    /**
     * Get the list of administrators
     * @return ArrayList of current administrators
     */
    public List<Administrator> getAdministrators() {
        return Collections.unmodifiableList(administrators);
    }

    /**
     * Remove an administrator
     * @param administrator Administrator to be removed
     */
    public void removeAdministrator(Administrator administrator) {
        administrators.remove(administrator);
    }

    /**
     * Checks if an administrator already exists with that username
     * @param username The username of the administrator
     */
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
     * @param username The username to be matched
     * @return Administrator or null if none exists
     */
    public Optional<Administrator> getAdministratorByUsername(String username) {
        return administrators.stream()
                .filter(o -> o.getUsername().equals(username))
                .findFirst();
    }

    /**
     * Return the default administrator
     * @return the default administrator
     */
    public Administrator getDefaultAdministrator() {
        return getAdministratorByUsername(defaultAdministratorUsername).orElseThrow(RuntimeException::new);
    }

    @Override
    public void applyChangesTo(Administrator administrator) {
        // Doesn't need to do anything
    }
}
