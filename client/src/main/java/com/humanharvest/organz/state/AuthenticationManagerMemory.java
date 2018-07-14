package com.humanharvest.organz.state;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;

public class AuthenticationManagerMemory implements AuthenticationManager {

    @Override
    public Clinician loginClinician(int staffId, String password) throws AuthenticationException {
        Clinician clinician = State.getClinicianManager().getClinicianByStaffId(staffId);

        if (clinician == null) {
            throw new AuthenticationException("Clinician Staff ID does not exist.");
        }

        if (!clinician.getPassword().equals(password)) {
            throw new AuthenticationException("Password is incorrect.");
        }
        return clinician;
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
