package seng302;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Period;

import static org.junit.Assert.*;

public class DonorTest {
	private Donor donor;

	@Before
	public void createDonor() {
		donor = new Donor();
	}

	@Test
	public void getBMITest1() {
		donor.setWeight(70);
		donor.setHeight(180);
		assertEquals(donor.getBMI(), 21.6, 0.01);
	}

	@Test
	public void getBMITest2() {
		donor.setWeight(0);
		donor.setHeight(180);
		assertEquals(donor.getBMI(), 0, 0.0);
	}

	@Test
	public void getAge() {
		LocalDate dob = LocalDate.of(2000, 1, 1);
		int age = Period.between(dob, LocalDate.now()).getYears();
		donor.setDateOfBirth(dob);
		assertEquals(donor.getAge(), age);

	}

	@Test
	public void getAge2() {
		LocalDate dob = LocalDate.of(2000, 1, 1);
		LocalDate dod = LocalDate.of(2010, 1, 1);
		donor.setDateOfBirth(dob);
		donor.setDateOfDeath(dod);
		assertEquals(10, donor.getAge());
	}

    @Test
    public void CheckNameContainsValidTest() {
        donor = new Donor("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(donor.nameContains("First"));
    }

    @Test
    public void CheckNameContainsCaseInsensitivityValidTest() {
        donor = new Donor("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(donor.nameContains("first"));
    }

    @Test
    public void CheckNameContainsLastNameValidTest() {
        donor = new Donor("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(donor.nameContains("La"));
    }

    @Test
    public void CheckNameContainsMiddleNameValidTest() {
        donor = new Donor("First", "middlename", "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(donor.nameContains("mid"));
    }

    @Test
    public void CheckNameContainsNotValidTest() {
        donor = new Donor("First", "middlename", "Last", LocalDate.of(1970, 1, 1), 1);
        assertFalse(donor.nameContains("notin"));
    }

    @Test
    public void CheckNameContainsMultipleChecksValidTest() {
        donor = new Donor("First", "middlename", "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(donor.nameContains("F Last"));
    }

    @Test
    public void CheckNameContainsMultipleChecksOneInvalidTest() {
        donor = new Donor("First", "middlename", "Last", LocalDate.of(1970, 1, 1), 1);
        assertFalse(donor.nameContains("F mid not"));
    }

    @Test
    public void GetFullNameNoMiddleNameTest() {
        donor = new Donor("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertEquals("First Last", donor.getFullName());
    }

    @Test
    public void GetFullNameWithMiddleNameTest() {
        donor = new Donor("First", "Mid Name", "Last", LocalDate.of(1970, 1, 1), 1);
        assertEquals("First Mid Name Last", donor.getFullName());
    }
}