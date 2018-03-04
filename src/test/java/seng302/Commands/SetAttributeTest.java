package seng302.Commands;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import picocli.CommandLine;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.BloodType;
import seng302.Utilities.Gender;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SetAttributeTest {

    private DonorManager spyDonorManager;


    private SetAttribute spySetAttribute;

    @Before
    public void init() {
        spyDonorManager = spy(new DonorManager());

        spySetAttribute = spy(new SetAttribute(spyDonorManager));

    }

    @Test
    public void updateuser_invalid_format_id() {
        doNothing().when(spyDonorManager).addDonor(any());
        String[] inputs = {"-u", "notint"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(0)).run();
    }

    @Test
    public void updateuser_invalid_option() {
        String[] inputs = {"-u", "1", "--notanoption"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(0)).run();
    }

    @Test
    public void updateuser_non_existent_id() {
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(null);
        String[] inputs = {"-u", "2"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(1)).run();
        verify(spyDonorManager, times(0)).updateDonor(any());
    }

    @Test
    public void updateuser_valid_name() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1", "--firstname", "NewFirst"};

        ArgumentCaptor<Donor> captor = ArgumentCaptor.forClass(Donor.class);

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spyDonorManager).updateDonor(captor.capture());

        assertEquals("NewFirst", captor.getValue().getFirstName());
    }

    @Test
    public void updateuser_valid_blood_type() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1", "--bloodtype", "O+"};

        ArgumentCaptor<Donor> captor = ArgumentCaptor.forClass(Donor.class);

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spyDonorManager).updateDonor(captor.capture());

        assertEquals(BloodType.O_POS, captor.getValue().getBloodType());
    }

    @Test
    public void updateuser_invalid_blood_type() {
        String[] inputs = {"-u", "1", "--bloodtype", "O"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(0)).run();
    }

    @Test
    public void updateuser_valid_gender() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1", "--gender", "Male"};

        ArgumentCaptor<Donor> captor = ArgumentCaptor.forClass(Donor.class);

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spyDonorManager).updateDonor(captor.capture());

        assertEquals(Gender.MALE, captor.getValue().getGender());
    }

    @Test
    public void updateuser_invalid_gender() {
        String[] inputs = {"-u", "1", "--gender", "Neg"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(0)).run();
    }

    @Test
    public void updateuser_valid_date() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1", "--dateofdeath", "20/01/2038"};

        ArgumentCaptor<Donor> captor = ArgumentCaptor.forClass(Donor.class);

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spyDonorManager).updateDonor(captor.capture());

        assertEquals(LocalDate.of(2038, 1, 20), captor.getValue().getDateOfDeath());
    }


    @Test
    public void updateuser_invalid_date() {
        String[] inputs = {"-u", "1", "--dateofdeath", "20/13/2038"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(0)).run();
    }



}
