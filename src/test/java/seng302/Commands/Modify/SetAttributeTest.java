package seng302.Commands.Modify;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import seng302.Actions.ActionInvoker;
import seng302.Donor;
import seng302.State.DonorManager;
import seng302.Utilities.Enums.BloodType;
import seng302.Utilities.Enums.Gender;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

public class SetAttributeTest {

    private DonorManager spyDonorManager;
    private SetAttribute spySetAttribute;

    @Before
    public void init() {
        spyDonorManager = spy(new DonorManager());

        spySetAttribute = spy(new SetAttribute(spyDonorManager, new ActionInvoker()));

    }

    @Test
    public void setattribute_invalid_format_id() {
        String[] inputs = {"-u", "notint"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(0)).run();
    }

    @Test
    public void setattribute_invalid_option() {
        String[] inputs = {"-u", "1", "--notanoption"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(0)).run();
    }

    @Test
    public void setattribute_non_existent_id() {
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(null);
        String[] inputs = {"-u", "2"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(1)).run();
    }

    @Test
    public void setattribute_valid_name() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1", "--firstname", "NewFirst"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        assertEquals("NewFirst", donor.getFirstName());
    }

    @Test
    public void setattribute_valid_blood_type() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1", "--bloodtype", "O+"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        assertEquals(BloodType.O_POS, donor.getBloodType());
    }

    @Test
    public void setattribute_invalid_blood_type() {
        String[] inputs = {"-u", "1", "--bloodtype", "O"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(0)).run();
    }

    @Test
    public void setattribute_valid_gender() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1", "--gender", "Male"};
        CommandLine.run(spySetAttribute, System.out, inputs);

        assertEquals(Gender.MALE, donor.getGender());
    }

    @Test
    public void setattribute_invalid_gender() {
        String[] inputs = {"-u", "1", "--gender", "Neg"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(0)).run();
    }

    @Test
    public void setattribute_valid_date() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1", "--dateofdeath", "20/01/2038"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        assertEquals(LocalDate.of(2038, 1, 20), donor.getDateOfDeath());
    }


    @Test
    public void setattribute_invalid_date() {
        String[] inputs = {"-u", "1", "--dateofdeath", "20/13/2038"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(0)).run();
    }



}
