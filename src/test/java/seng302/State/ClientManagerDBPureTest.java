package seng302.State;

import org.junit.Test;

public class ClientManagerDBPureTest {

    @Test
    public void retrieveAllUsers(){
        ClientManagerDBPure test = new ClientManagerDBPure();

        System.out.println(test.getClients());

    }
}
