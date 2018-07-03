package com.humanharvest.organz.state;

import java.util.List;

import com.humanharvest.organz.Administrator;

public class AdministratorManagerRest implements AdministratorManager {

    @Override
    public void addAdministrator(Administrator administrator) {

    }

    @Override
    public List<Administrator> getAdministrators() {
        return null;
    }

    @Override
    public void removeAdministrator(Administrator administrator) {

    }

    @Override
    public boolean collisionExists(String username) {
        return false;
    }

    @Override
    public Administrator getAdministratorByUsername(String username) {
        return null;
    }

    @Override
    public Administrator getDefaultAdministrator() {
        return null;
    }
}
