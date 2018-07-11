package com.humanharvest.organz.utilities.validators;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.time.LocalDate;

import com.humanharvest.organz.Client;
import org.junit.Test;

public class ClientValidatorTest {

    private ClientValidator validator = new ClientValidator();

    private static Client getTestClient() {
        return new Client("Adam",
                "",
                "Eve",
                LocalDate.now().minusYears(25),
                1);
    }

    private static void editPrivateField(Object obj, String fieldName, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (NoSuchFieldException | IllegalAccessException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Test
    public void invalidUidTest() {
        Client client = getTestClient();
        client.setUid(-1);

        assertNotNull(validator.validate(client));
    }

    @Test
    public void invalidFirstNameTest() {
        Client client = getTestClient();
        client.setFirstName("");

        assertNotNull(validator.validate(client));
    }

    @Test
    public void invalidLastNameTest() {
        Client client = getTestClient();
        client.setLastName("");
        System.out.println(validator.validate(client));

        assertNotNull(validator.validate(client));
    }

    @Test
    public void invalidDateOfBirthTest() {
        Client client = getTestClient();
        LocalDate dob = LocalDate.of(2000, 5, 4);
        editPrivateField(dob, "month", (short) 14);
        client.setDateOfBirth(dob);

        assertNotNull(validator.validate(client));
    }
}
