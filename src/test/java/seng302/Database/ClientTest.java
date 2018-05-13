package seng302.Database;

import java.time.LocalDate;

import seng302.Client;

import org.junit.Test;

public class ClientTest {

    Client client = new Client("Thomas","Declan","Kearsley", LocalDate.now().minusDays(7000),222);
    ClientInsert insert = new ClientInsert();


    @Test
    public void insertClient(){
        insert.databaseAddClient(client);

    }

}
