package com.humanharvest.organz.validators;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.utilities.validators.ClientValidator;
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
    public void validUidTest() {
        Client client = getTestClient();
        client.setUid(1);

        assertNull(validator.validate(client));
    }

    @Test
    public void invalidUidTest() {
        Client client = getTestClient();
        client.setUid(-1);

        assertNotNull(validator.validate(client));
    }

    @Test
    public void validFirstNameTest() {
        Client client = getTestClient();
        client.setFirstName("Amy");

        assertNull(validator.validate(client));
    }

    @Test
    public void invalidFirstNameTest() {
        Client client = getTestClient();
        client.setFirstName("");

        assertNotNull(validator.validate(client));
    }

    @Test
    public void validLastNameTest() {
        Client client = getTestClient();
        client.setLastName("Williams");

        assertNull(validator.validate(client));
    }

    @Test
    public void invalidLastNameTest() {
        Client client = getTestClient();
        client.setLastName("");

        assertNotNull(validator.validate(client));
    }

    @Test
    public void validDateOfBirthTest() {
        Client client = getTestClient();
        LocalDate dob = LocalDate.of(2000, 5, 4);
        client.setDateOfBirth(dob);

        assertNull(validator.validate(client));
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
