package seng302.Commands.Modify;


import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import seng302.Actions.ActionInvoker;
import seng302.Person;
import seng302.State.PersonManager;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import picocli.CommandLine;

public class SetOrganStatusTest {

    private PersonManager spyPersonManager;
    private SetOrganStatus spySetOrganStatus;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void init() {
        spyPersonManager = spy(new PersonManager());

        spySetOrganStatus = spy(new SetOrganStatus(spyPersonManager, new ActionInvoker()));

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

    }

    @Test
    public void setorganstatus_invalid_format_id() {
        String[] inputs = {"-u", "notint"};

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        verify(spySetOrganStatus, times(0)).run();
    }

    @Test
    public void setorganstatus_invalid_option() {
        String[] inputs = {"-u", "1", "--notanoption"};

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        verify(spySetOrganStatus, times(0)).run();
    }

    @Test
    public void setorganstatus_non_existent_id() {
        when(spyPersonManager.getPersonByID(anyInt())).thenReturn(null);
        String[] inputs = {"-u", "2"};

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        verify(spySetOrganStatus, times(1)).run();
    }

    @Test
    public void setorganstatus_valid_set_to_true() {
        Person person = new Person("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        when(spyPersonManager.getPersonByID(anyInt())).thenReturn(person);
        String[] inputs = {"-u", "1", "--liver"};

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        assertEquals(true, person.getOrganStatus().get(Organ.LIVER));
    }

    @Test
    public void setorganstatus_valid_set_to_false() throws OrganAlreadyRegisteredException {
        Person person = new Person("First", null, "Last", LocalDate.of(1970, 1, 1), 1);

        person.setOrganStatus(Organ.LIVER, true);

        when(spyPersonManager.getPersonByID(anyInt())).thenReturn(person);
        String[] inputs = {"-u", "1", "--liver=false"};

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        assertEquals(false, person.getOrganStatus().get(Organ.LIVER));
    }

    @Test
    public void setorganstatus_invalid_set_to_true_already_true() throws OrganAlreadyRegisteredException {
        Person person = new Person("First", null, "Last", LocalDate.of(1970, 1, 1), 1);

        person.setOrganStatus(Organ.LIVER, true);

        when(spyPersonManager.getPersonByID(anyInt())).thenReturn(person);
        String[] inputs = {"-u", "1", "--liver"};

        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        assertThat(outContent.toString(), containsString("Liver is already registered for donation"));
    }

    @Test
    public void setorganstatus_valid_multiple_updates() {
        Person person = new Person("First", null, "Last", LocalDate.of(1970, 1, 1), 1);

        when(spyPersonManager.getPersonByID(anyInt())).thenReturn(person);
        String[] inputs = {"-u", "1", "--liver", "--kidney"};

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        assertEquals(true, person.getOrganStatus().get(Organ.LIVER));
        assertEquals(true, person.getOrganStatus().get(Organ.KIDNEY));
    }

    @Test
    public void setorganstatus_valid_multiple_updates_some_invalid() throws OrganAlreadyRegisteredException {
        Person person = new Person("First", null, "Last", LocalDate.of(1970, 1, 1), 1);

        person.setOrganStatus(Organ.LIVER, true);
        person.setOrganStatus(Organ.BONE, true);

        when(spyPersonManager.getPersonByID(anyInt())).thenReturn(person);
        String[] inputs = {"-u", "1", "--liver", "--kidney", "--bone=false"};

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        assertThat(outContent.toString(), containsString("Liver is already registered for donation"));

        assertEquals(true, person.getOrganStatus().get(Organ.KIDNEY));
        assertEquals(false, person.getOrganStatus().get(Organ.BONE));
    }
}
