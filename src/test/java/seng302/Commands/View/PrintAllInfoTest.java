package seng302.Commands.View;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;

import seng302.Person;
import seng302.State.PersonManager;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

public class PrintAllInfoTest {

    private PersonManager spyPersonManager;
    private PrintAllInfo spyPrintAllInfo;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void init() {
        spyPersonManager = spy(new PersonManager());

        spyPrintAllInfo = spy(new PrintAllInfo(spyPersonManager));
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void printallinfo_no_persons() {
        ArrayList<Person> people = new ArrayList<>();

        when(spyPersonManager.getPeople()).thenReturn(people);
        String[] inputs = {};

        CommandLine.run(spyPrintAllInfo, System.out, inputs);

        assertThat(outContent.toString(), containsString("No people exist"));
    }

    @Test
    public void printallinfo_single_person() {
        Person person = new Person("First", "mid", "Last", LocalDate.of(1970, 1, 1), 1);

        ArrayList<Person> people = new ArrayList<>();
        people.add(person);

        when(spyPersonManager.getPeople()).thenReturn(people);
        String[] inputs = {};

        CommandLine.run(spyPrintAllInfo, System.out, inputs);

        assertThat(outContent.toString(),
                containsString("User: 1. Name: First mid Last, date of birth: 1970-01-01, date of death: null"));
    }

    @Test
    public void printallinfo_multiple_persons() {
        Person person = new Person("First", "mid", "Last", LocalDate.of(1970, 1, 1), 1);
        Person person2 = new Person("FirstTwo", null, "LastTwo", LocalDate.of(1971, 2, 2), 2);

        ArrayList<Person> people = new ArrayList<>();
        people.add(person);
        people.add(person2);

        when(spyPersonManager.getPeople()).thenReturn(people);
        String[] inputs = {};

        CommandLine.run(spyPrintAllInfo, System.out, inputs);

        assertThat(outContent.toString(),
                containsString("User: 1. Name: First mid Last, date of birth: 1970-01-01, date of death: null"));
        assertThat(outContent.toString(),
                containsString("User: 2. Name: FirstTwo LastTwo, date of birth: 1971-02-02, date of death: null"));
    }
}
