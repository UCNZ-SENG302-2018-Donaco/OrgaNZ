package com.humanharvest.organz.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import com.humanharvest.organz.Administrator;

import com.humanharvest.organz.BaseTest;
import org.junit.Before;
import org.junit.Test;

public class AdministratorManagerTest extends BaseTest {

    private AdministratorManager manager;

    private String username1 = "bob";
    private String username2 = "greg";
    private Administrator administrator = new Administrator(username1, "letmein");
    private Administrator administrator2 = new Administrator(username2, "password123");


    @Before
    public void init() {
        manager = new AdministratorManagerMemory();
    }

    @Test
    public void addAdministratorTest() {
        manager.addAdministrator(administrator);
        assertTrue(manager.getAdministrators().contains(administrator));
    }


    @Test
    public void getAdministratorsTest() {
        ArrayList<Administrator> administrators = new ArrayList<>();
        administrators.add(administrator);
        administrators.add(administrator2);
        manager = new AdministratorManagerMemory(administrators);

        assertTrue(manager.getAdministrators().contains(administrator));
        assertTrue(manager.getAdministrators().contains(administrator2));
    }

    @Test
    public void removeAdministratorTest() {
        ArrayList<Administrator> administrators = new ArrayList<>();
        administrators.add(administrator);
        administrators.add(administrator2);
        manager = new AdministratorManagerMemory(administrators);

        manager.removeAdministrator(administrator2);

        assertTrue(manager.getAdministrators().contains(administrator));
        assertFalse(manager.getAdministrators().contains(administrator2));
    }

    @Test
    public void updateAdministratorTest() {
        ArrayList<Administrator> administrators = new ArrayList<>();
        administrators.add(administrator);
        administrators.add(administrator2);
        manager = new AdministratorManagerMemory(administrators);

        administrator.setPassword("somethingsecure");

        assertTrue(manager.getAdministrators().contains(administrator));
        assertEquals(manager.getAdministratorByUsername("bob").getPassword(), "somethingsecure");
    }


    @Test
    public void collisionExistsNoCollisionTest() {
        ArrayList<Administrator> administrators = new ArrayList<>();
        administrators.add(administrator);
        manager = new AdministratorManagerMemory(administrators);

        assertFalse(manager.collisionExists("some new username"));
    }

    @Test
    public void collisionExistsTrueTest() {
        ArrayList<Administrator> administrators = new ArrayList<>();
        administrators.add(administrator);
        manager = new AdministratorManagerMemory(administrators);

        assertTrue(manager.collisionExists(username1));
    }

    @Test
    public void collisionExistsTrueNumericTest() {
        ArrayList<Administrator> administrators = new ArrayList<>();
        administrators.add(administrator);
        manager = new AdministratorManagerMemory(administrators);

        assertTrue(manager.collisionExists("5"));
    }

    @Test
    public void collisionExistsTrueMultipleTest() {
        ArrayList<Administrator> administrators = new ArrayList<>();
        administrators.add(administrator);
        administrators.add(administrator2);
        manager = new AdministratorManagerMemory(administrators);

        assertTrue(manager.collisionExists(username2));
    }

}
