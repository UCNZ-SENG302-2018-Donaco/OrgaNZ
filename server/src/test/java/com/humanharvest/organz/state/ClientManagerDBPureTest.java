package com.humanharvest.organz.state;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.database.DBManager;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ClientManagerDBPureTest extends BaseTest {

    private DBManager dbManager;

    @Test
    public void retrieveAllUsers() {
        ClientManagerDBPure test = new ClientManagerDBPure();
        Client client = new Client();
        Client client1 = new Client();
        ArrayList<Client> clients = new ArrayList<Client>();
        clients.add(client);
        clients.add(client1);
        test.addClient(client);
        test.addClient(client1);
        List<Client> result = test.getClients();
        Assert.assertEquals(clients, result);

    }

    @Test
    public void collisionExistsTest() {
        ClientManagerDBPure test = new ClientManagerDBPure();
        LocalDate date = LocalDate.now();
        Client client = new Client("Thomas", "Declan", "Kearsley", date, 3);
        test.addClient(client);
        Boolean result = test.doesClientExist("Thomas", "Kearsley", date);
        Assert.assertEquals(true, result);

    }

    @Test
    public void addListofUsers() {
        ClientManagerDBPure test = new ClientManagerDBPure();
        ArrayList<Client> clients = new ArrayList<Client>();
        LocalDate date = LocalDate.now();
        Client client = new Client("Thomas", "Declan", "Kearsley", date, 1);
        Client client2 = new Client("Thomas", "Declan", "Kearsley", date, 2);
        Client client3 = new Client("Thomas", "Declan", "Kearsley", date, 3);
        clients.add(client);
        clients.add(client2);
        clients.add(client3);
        test.setClients(clients);
        Assert.assertEquals(clients, test.getClients());
    }
}
