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
}