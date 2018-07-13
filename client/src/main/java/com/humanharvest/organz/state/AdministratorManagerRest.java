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
    public Iterable<Administrator> getAdministratorsFiltered(String nameQuery, Integer offset, Integer count) {
        // TODO: Implement this so it's optimised
        return AdministratorManager.super.getAdministratorsFiltered(nameQuery, offset, count);
    }

    @Override
    public void removeAdministrator(Administrator administrator) {

    }

    @Override
    public boolean doesUsernameExist(String username) {
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
