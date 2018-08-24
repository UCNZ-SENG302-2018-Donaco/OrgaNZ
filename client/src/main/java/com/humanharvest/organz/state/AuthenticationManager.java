package com.humanharvest.organz.state;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;

public interface AuthenticationManager {
    Client loginClient(int id) throws AuthenticationException;

    Clinician loginClinician(int staffId, String password) throws AuthenticationException;

    Administrator loginAdministrator(String username, String password) throws AuthenticationException;
}
