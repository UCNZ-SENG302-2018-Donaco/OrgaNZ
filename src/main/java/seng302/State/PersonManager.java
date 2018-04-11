package seng302.State;

import java.time.LocalDate;
import java.util.ArrayList;

import seng302.Person;

/**
 * The class to handle the Person inputs, including adding,
 * setting attributes and updating the values of the person.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 08/03/2018
 */

public class PersonManager {

    private ArrayList<Person> people;
    private int uid;

    public PersonManager() {
        people = new ArrayList<>();
        uid = 1;
    }

    public PersonManager(ArrayList<Person> people) {
        this.people = people;
        uid = calculateNextId();
    }

    public void setPeople(ArrayList<Person> people) {
        this.people = people;
    }

    /**
     * Add a person
     * @param person Person to be added
     */
    public void addPerson(Person person) {
        people.add(person);
    }

    /**
     * Get the list of people
     * @return ArrayList of current people
     */
    public ArrayList<Person> getPeople() {
        return people;
    }

    /**
     * Remove a person object
     * @param person Person to be removed
     */
    public void removePerson(Person person) {
        people.remove(person);
    }

    /**
     * Get the next user ID
     * @return Next userID to be used
     */
    public int getUid() {
        return uid++;
    }

    /**
     * Set the user ID
     * @param uid Value to set the user IF
     */
    public void setUid(int uid) {
        this.uid = uid;
    }

    /**
     * Checks if a user already exists with that first + last name and date of birth
     * @param firstName First name
     * @param lastName Last name
     * @param dateOfBirth Date of birth (LocalDate)
     * @return Boolean
     */
    public boolean collisionExists(String firstName, String lastName, LocalDate dateOfBirth) {
        for (Person person : people) {
            if (person.getFirstName().equals(firstName) &&
                    person.getLastName().equals(lastName) &&
                    person.getDateOfBirth().isEqual(dateOfBirth)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return a person matching that UID
     * @param id To be matched
     * @return Person object or null if none exists
     */
    public Person getPersonByID(int id) {
        return people.stream()
                .filter(d -> d.getUid() == id).findFirst().orElse(null);
    }

    private int calculateNextId() {
        int id = 1;
        for (Person person : people) {
            if (person.getUid() >= id) {
                id = person.getUid() + 1;
            }
        }
        return id;
    }

}
