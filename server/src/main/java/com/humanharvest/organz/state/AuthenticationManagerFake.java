package com.humanharvest.organz.state;

public class AuthenticationManagerFake extends AuthenticationManager {

    @Override
    public void verifyAdminAccess(String authenticationToken) {
        // Do nothing
    }
}
