package seng302.Commands.View;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;

import seng302.Person;
import seng302.State.PersonManager;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

public class PrintAllOrganTest {

    private PersonManager spyPersonManager;
    private PrintAllOrgan spyPrintAllOrgan;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void init() {
        spyPersonManager = spy(new PersonManager());

        spyPrintAllOrgan = spy(new PrintAllOrgan(spyPersonManager));
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void printallorgan_no_persons() {
        ArrayList<Person> people = new ArrayList<>();

        when(spyPersonManager.getPeople()).thenReturn(people);
        String[] inputs = {};

        CommandLine.run(spyPrintAllOrgan, System.out, inputs);

        assertThat(outContent.toString(), containsString("No people exist"));
    }

    @Test
    public void printallorgan_single_person() throws OrganAlreadyRegisteredException {
        Person person = new Person("First", "mid", "Last", LocalDate.of(1970, 1, 1), 1);
        person.setOrganStatus(Organ.LIVER, true);
        person.setOrganStatus(Organ.KIDNEY, true);

        ArrayList<Person> people = new ArrayList<>();
        people.add(person);

        when(spyPersonManager.getPeople()).thenReturn(people);
        String[] inputs = {};

        CommandLine.run(spyPrintAllOrgan, System.out, inputs);

        assertTrue(outContent.toString().contains("User: 1. Name: First mid Last, Donation status: Kidney, Liver")
                || outContent.toString().contains("User: 1. Name: First mid Last, Donation status: Liver, Kidney"));
    }

    @Test
    public void printallorgan_multiple_persons() throws OrganAlreadyRegisteredException {
        Person person = new Person("First", "mid", "Last", LocalDate.of(1970, 1, 1), 1);
        Person person2 = new Person("FirstTwo", null, "LastTwo", LocalDate.of(1971, 2, 2), 2);
        Person person3 = new Person("FirstThree", null, "LastThree", LocalDate.of(1971, 2, 2), 3);
        person.setOrganStatus(Organ.LIVER, true);
        person.setOrganStatus(Organ.KIDNEY, true);
        person2.setOrganStatus(Organ.CONNECTIVE_TISSUE, true);

        ArrayList<Person> people = new ArrayList<>();
        people.add(person);
        people.add(person2);
        people.add(person3);

        when(spyPersonManager.getPeople()).thenReturn(people);
        String[] inputs = {};

        CommandLine.run(spyPrintAllOrgan, System.out, inputs);

        assertTrue(outContent.toString().contains("User: 1. Name: First mid Last, Donation status: Kidney, Liver")
                || outContent.toString().contains("User: 1. Name: First mid Last, Donation status: Liver, Kidney"));
        assertThat(outContent.toString(),
                containsString("User: 2. Name: FirstTwo LastTwo, Donation status: Connective tissue"));
        assertThat(outContent.toString(),
                containsString("User: 3. Name: FirstThree LastThree, Donation status: No organs registered for "
                        + "donation"));
    }
}
