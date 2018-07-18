package com.humanharvest.organz.state;

import java.util.Optional;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;

public class AuthenticationManagerMemory implements AuthenticationManager {

    @Override
    public Client loginClient(int id) throws AuthenticationException {
        Optional<Client> client = State.getClientManager().getClientByID(id);

        if (!client.isPresent()) {
            throw new AuthenticationException("Client ID does not exist.");
        }

        return client.get();
    }

    @Override
    public Clinician loginClinician(int staffId, String password) throws AuthenticationException {
        Optional<Clinician> clinician = State.getClinicianManager().getClinicianByStaffId(staffId);

        if (!clinician.isPresent()) {
            throw new AuthenticationException("Clinician Staff ID does not exist.");
        }

        if (!clinician.get().getPassword().equals(password)) {
            throw new AuthenticationException("Password is incorrect.");
        }

        return clinician.get();
    }

    @Override
    public Administrator loginAdministrator(String username, String password) throws AuthenticationException {
        Administrator administrator = State.getAdministratorManager()
                .getAdministratorByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Administrator does not exist."));

        if (!administrator.getPassword().equals(password)) {
            throw new AuthenticationException("Password is incorrect.");
        }

        return administrator;
    }
}
