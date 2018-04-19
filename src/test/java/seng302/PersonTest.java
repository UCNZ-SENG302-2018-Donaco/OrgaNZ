package seng302;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

import java.time.LocalDate;
import java.util.List;

public class PersonTest {

    private Person person;
    private Condition condition1;
    private Condition condition2;
    private Condition condition3;

    @Before
    public void setUp() {
        person = new Person();

        condition1 = new Condition("Condition 1", LocalDate.now());
        condition1.setResolutionDate(LocalDate.now());
        condition2 = new Condition("Condition 2", LocalDate.now());
        condition2.setChronic(true);
        condition3 = new Condition("Condition 3", LocalDate.now());

        person.addCondition(condition1);
        person.addCondition(condition2);
    }

    @Test
    public void testGetAllConditions() {
        List<Condition> conditions = person.getAllConditions();

        Assert.assertTrue(conditions.contains(condition1));
        Assert.assertTrue(conditions.contains(condition2));
    }

    @Test
    public void testGetCurrentConditions() {
        List<Condition> currentConditions = person.getCurrentConditions();

        Assert.assertTrue(currentConditions.contains(condition2));
        Assert.assertFalse(currentConditions.contains(condition1));
    }

    @Test
    public void testGetResolvedConditions() {
        List<Condition> resolvedConditions = person.getResolvedConditions();

        Assert.assertTrue(resolvedConditions.contains(condition1));
        Assert.assertFalse(resolvedConditions.contains(condition2));
    }

    @Test
    public void testAddCondition() {
        person.addCondition(condition3);
        List<Condition> conditions = person.getAllConditions();

        Assert.assertTrue(conditions.contains(condition3));
    }
}
